package com.example.paxrioverde.domain.repository

import com.example.paxrioverde.api.AnoItem
import com.example.paxrioverde.api.BoletoResponse
import com.example.paxrioverde.api.MensalidadeItem
import com.example.paxrioverde.api.PixResponse
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.model.PlanStatus

/**
 * Interface que define as operações financeiras do domínio com resiliência.
 */
interface FinanceRepository {
    suspend fun getMensalidades(idcliente: Int): NetworkResult<List<AnoItem>>
    fun getOldestUnpaid(anos: List<AnoItem>): MensalidadeItem?
    fun getHistoryInvoices(anos: List<AnoItem>, selectedYear: Int): List<MensalidadeItem>
    fun calculatePlanStatus(anos: List<AnoItem>): PlanStatus

    suspend fun gerarPix(
        idcaixa: Int,
        mensalidade: MensalidadeItem,
        valorTotal: String
    ): NetworkResult<PixResponse>

    suspend fun gerarBoleto(
        mensalidade: MensalidadeItem,
        valorTotal: String
    ): NetworkResult<BoletoResponse>

    suspend fun atualizarCpfDependente(
        idcliente: Int,
        cpf: String,
        nomeDependente: String
    ): NetworkResult<Unit>
}
