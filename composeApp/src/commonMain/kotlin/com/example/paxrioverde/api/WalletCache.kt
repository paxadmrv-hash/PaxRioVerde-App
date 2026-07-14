package com.example.paxrioverde.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.example.paxrioverde.util.SessionManager
import kotlinx.coroutines.delay
import kotlinx.datetime.*

object WalletCache {
    val cartoesList = mutableStateListOf<CartaoItem>()
    val dependentesList = mutableStateListOf<DependenteItem>()
    val mensalidadesList = mutableStateListOf<MensalidadeItem>()
    val loadedBitmaps = mutableStateMapOf<Int, ImageBitmap>()
    var isPreloading by mutableStateOf(false)
    
    private val sessionManager = SessionManager()

    val totalValorCartoes: Double
        get() = cartoesList.sumOf { 
            it.valor?.replace(",", ".")?.toDoubleOrNull() ?: 0.0 
        }

    suspend fun preLoad(idcliente: Int, forceRefresh: Boolean = false) {
        if (idcliente == 0) return
        if (isPreloading && !forceRefresh) return
        
        if (forceRefresh) {
            cartoesList.clear()
            dependentesList.clear()
            mensalidadesList.clear()
            loadedBitmaps.clear()
        }

        isPreloading = true
        try {
            // 1. Busca a lista de dependentes
            try {
                val resDep = ApiService.getDependentes(idcliente)
                if (resDep.success) {
                    val novosDependentes = resDep.dependentes.orEmpty()
                        .distinctBy { it.nomeDependente + it.cpf + it.parentesco }
                    
                    dependentesList.clear()
                    dependentesList.addAll(novosDependentes)
                }
            } catch (e: Exception) {
                println("WalletCache: Erro ao carregar dependentes: ${e.message}")
            }

            // 2. Busca a lista de mensalidades para validade dinâmica
            try {
                val resMens = ApiService.getMensalidades(idcliente)
                if (resMens.success) {
                    mensalidadesList.clear()
                    val todasMensalidades = resMens.anos?.flatMap { it.mensalidades } ?: emptyList()
                    mensalidadesList.addAll(todasMensalidades)
                }
            } catch (e: Exception) {
                println("WalletCache: Erro ao carregar mensalidades: ${e.message}")
            }

            // 3. Busca a lista de cartões
            val response = ApiService.getCartoes(idcliente)
            if (response.success && response.cartoes != null) {
                cartoesList.clear()
                cartoesList.addAll(response.cartoes.orEmpty())
            }
        } catch (e: Exception) {
            println("WalletCache: Erro no processo de pre-load: ${e.message}")
        } finally {
            isPreloading = false
        }
    }

    /**
     * Calcula a validade do cartão baseada no histórico de fidelidade.
     * < 12 mensalidades pagas = +6 meses de validade.
     * >= 12 mensalidades pagas = +1 ano de validade.
     */
    fun getCalculatedValidity(originalValidity: String): String {
        val totalPaid = mensalidadesList.count { it.pago }
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val monthsToAdd = if (totalPaid >= 12) 12 else 6
        
        val validityDate = today.plus(monthsToAdd, DateTimeUnit.MONTH)
        
        return formatDate(validityDate)
    }

    private fun parseDate(dateStr: String): LocalDate? {
        return try {
            val parts = dateStr.split("/")
            if (parts.size == 3) {
                LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDate(date: LocalDate): String {
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val month = date.monthNumber.toString().padStart(2, '0')
        val year = date.year
        return "$day/$month/$year"
    }

    fun clear() {
        cartoesList.clear()
        dependentesList.clear()
        mensalidadesList.clear()
        loadedBitmaps.clear()
    }
}
