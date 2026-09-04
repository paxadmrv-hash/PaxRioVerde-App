package com.example.paxrioverde.data.repository

import com.example.paxrioverde.api.*
import com.example.paxrioverde.data.util.safeApiCall
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.model.PlanStatus
import com.example.paxrioverde.domain.repository.FinanceRepository
import com.example.paxrioverde.util.SessionManager
import kotlinx.datetime.*

class FinanceRepositoryImpl(
    private val api: ApiService,
    private val sessionManager: SessionManager,
    private val walletCache: WalletCache
) : FinanceRepository {

    override suspend fun getMensalidades(idcliente: Int): NetworkResult<List<AnoItem>> {
        return safeApiCall {
            val response = api.getMensalidades(idcliente)
            if (response.success) {
                // Senior Sincronização: Atualiza o cache global para que o Dashboard e Carteira fiquem em dia.
                walletCache.mensalidadesList.clear()
                walletCache.mensalidadesList.addAll(response.anos?.flatMap { it.mensalidades } ?: emptyList())
                
                response.anos ?: emptyList()
            } else {
                throw Exception(response.message ?: "Erro ao carregar mensalidades")
            }
        }
    }

    override fun getOldestUnpaid(anos: List<AnoItem>): MensalidadeItem? {
        return anos.flatMap { it.mensalidades }
            .filter { !it.pago }
            .mapNotNull { item -> parseDate(item.dtvencimento)?.let { it to item } }
            .sortedBy { it.first }
            .firstOrNull()?.second
    }

    override fun getHistoryInvoices(anos: List<AnoItem>, selectedYear: Int): List<MensalidadeItem> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val allForYear = anos.find { it.ano == selectedYear }?.mensalidades ?: emptyList()
        
        val futureUnpaidIds = allForYear
            .filter { !it.pago }
            .mapNotNull { item -> parseDate(item.dtvencimento)?.let { it to item } }
            .sortedBy { it.first }
            .take(1)
            .map { it.second.idmensalidade }
            .toSet()

        return allForYear.filter { item ->
            if (item.pago) return@filter true
            if (futureUnpaidIds.contains(item.idmensalidade)) return@filter true
            val date = parseDate(item.dtvencimento) ?: return@filter true
            date.year < today.year || (date.year == today.year && date.monthNumber <= today.monthNumber)
        }.sortedWith(
            compareBy<MensalidadeItem> { it.pago }
                .thenBy { parseDate(it.dtvencimento) }
        )
    }

    override fun calculatePlanStatus(anos: List<AnoItem>): PlanStatus {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val unpaidItems = anos.flatMap { it.mensalidades }
            .filter { !it.pago }
            .filter { parseDate(it.dtvencimento)?.let { date -> date < today } ?: false }
        
        val currentUnpaidCount = unpaidItems.size
        val previousUnpaidCount = sessionManager.getPreviousUnpaidCount()
        val graceStartTimestamp = sessionManager.getGraceStartTimestamp()
        
        // 1. Detectar o Evento de Pagamento que dispara a carência
        // Se antes tinha 4+ e agora tem 3 ou menos, inicia os 60 dias.
        if (previousUnpaidCount >= 4 && currentUnpaidCount < 4) {
            val now = Clock.System.now().toEpochMilliseconds()
            sessionManager.saveGraceStartTimestamp(now)
        }
        
        // Atualizar contador para a próxima verificação
        sessionManager.savePreviousUnpaidCount(currentUnpaidCount)
        
        // 2. Verificar se está dentro da janela de carência de 60 dias
        val isInsideGracePeriod = if (graceStartTimestamp > 0) {
            val now = Clock.System.now().toEpochMilliseconds()
            val sixtyDaysInMillis = 60L * 24 * 60 * 60 * 1000
            now - graceStartTimestamp < sixtyDaysInMillis
        } else false
        
        // Limpar timestamp se a carência expirou
        if (graceStartTimestamp > 0 && !isInsideGracePeriod) {
            sessionManager.saveGraceStartTimestamp(0L)
        }

        // 3. Máquina de Estados (Prioridades)
        return when {
            // Se houver 4+ parcelas
            currentUnpaidCount >= 4 -> PlanStatus.ATTENTION

            // Se estiver na carência (timer ativo) e houver < 4 parcelas -> EM CARÊNCIA
            isInsideGracePeriod -> PlanStatus.GRACE_PERIOD

            // Fluxo Normal
            currentUnpaidCount == 3 -> PlanStatus.ATTENTION
            else -> PlanStatus.ACTIVE
        }
    }

    override suspend fun gerarPix(
        idcaixa: Int,
        mensalidade: MensalidadeItem,
        valorTotal: String
    ): NetworkResult<PixResponse> {
        return safeApiCall {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val originalDate = parseDate(mensalidade.dtvencimento)
            val finalDateStr = if (originalDate != null && originalDate < today) {
                "${today.dayOfMonth.toString().padStart(2, '0')}/${today.monthNumber.toString().padStart(2, '0')}/${today.year}"
            } else {
                mensalidade.dtvencimento
            }

            api.gerarPix(
                idcaixa = idcaixa,
                idcontrato = mensalidade.idcontrato,
                idconvenio = mensalidade.idconvenio,
                dtvencimento = finalDateStr,
                idmensalidade = mensalidade.idmensalidade,
                valorCartao = null,
                valorTotal = valorTotal
            )
        }
    }

    override suspend fun gerarBoleto(
        mensalidade: MensalidadeItem,
        valorTotal: String
    ): NetworkResult<BoletoResponse> {
        return safeApiCall {
            val dateParts = mensalidade.dtvencimento.split("/")
            val mesAno = if (dateParts.size == 3) "${dateParts[1]}${dateParts[2]}" else ""
            val cpf = sessionManager.getSavedCpf()

            api.getBoleto(
                idcontrato = mensalidade.idcontrato,
                idconvenio = mensalidade.idconvenio,
                idmensalidade = mensalidade.idmensalidade,
                cpf = cpf,
                mesano = mesAno,
                valorCartao = null,
                valorTotal = valorTotal
            )
        }
    }

    override suspend fun atualizarCpfDependente(
        idcliente: Int,
        cpf: String,
        nomeDependente: String
    ): NetworkResult<Unit> {
        return safeApiCall {
            val response = api.atuCpfDependente(idcliente, cpf, nomeDependente)
            if (response.success) {
                Unit
            } else {
                throw Exception(response.message ?: "Erro ao atualizar CPF")
            }
        }
    }

    private fun parseDate(dateStr: String): LocalDate? {
        return try {
            val parts = dateStr.split("/")
            if (parts.size == 3) {
                LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
