package com.example.paxrioverde.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.delay

object WalletCache {
    val cartoesList = mutableStateListOf<CartaoItem>()
    val dependentesList = mutableStateListOf<DependenteItem>()
    val loadedBitmaps = mutableStateMapOf<Int, ImageBitmap>()
    var isPreloading by mutableStateOf(false)
    
    // Rastreia se houve uma geração de cartão nesta sessão
    var pendingCardFee by mutableStateOf<String?>(null)

    val totalValorCartoes: Double
        get() = cartoesList.sumOf { 
            it.valor?.replace(",", ".")?.toDoubleOrNull() ?: 0.0 
        }

    suspend fun preLoad(idcliente: Int, forceRefresh: Boolean = false) {
        if (idcliente == 0) return
        if (isPreloading && !forceRefresh) return
        
        if (forceRefresh) {
            cartoesList.clear()
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

            // 2. Busca a lista de cartões
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

    fun clear() {
        cartoesList.clear()
        dependentesList.clear()
        loadedBitmaps.clear()
        pendingCardFee = null
    }
}
