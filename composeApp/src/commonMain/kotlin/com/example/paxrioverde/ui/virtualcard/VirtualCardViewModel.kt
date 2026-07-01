package com.example.paxrioverde.ui.virtualcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paxrioverde.api.ApiService
import com.example.paxrioverde.api.CartaoItem
import com.example.paxrioverde.api.WalletCache
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class VirtualCardState {
    object Idle : VirtualCardState()
    object Loading : VirtualCardState()
    data class PixGenerated(val pixCode: String, val identificadorPix: String) : VirtualCardState()
    data class Success(val newCard: CartaoItem?) : VirtualCardState()
    data class Error(val message: String) : VirtualCardState()
}

class VirtualCardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<VirtualCardState>(VirtualCardState.Idle)
    val uiState: StateFlow<VirtualCardState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    fun gerarCartaoPix(idcaixa: Int, idcliente: Int, tipo: String, nomeDependente: String, estiloSelecionado: String) {
        viewModelScope.launch {
            _uiState.value = VirtualCardState.Loading
            try {
                val response = ApiService.gerarCartaoPix(idcaixa, idcliente, tipo, nomeDependente)
                if (response.success && response.pix != null && response.identificador_pix != null) {
                    _uiState.value = VirtualCardState.PixGenerated(response.pix, response.identificador_pix)
                    startPolling(response.identificador_pix, idcliente, estiloSelecionado)
                } else {
                    _uiState.value = VirtualCardState.Error(response.message ?: "Erro ao gerar PIX")
                }
            } catch (e: Exception) {
                _uiState.value = VirtualCardState.Error("Erro de conexão: ${e.message}")
            }
        }
    }

    private fun startPolling(identificadorPix: String, idcliente: Int, estiloSelecionado: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                try {
                    val response = ApiService.verificarPixPago(identificadorPix)
                    if (response.pago) {
                        onPaymentSuccess(idcliente, estiloSelecionado)
                        break
                    }
                } catch (e: Exception) {
                    // Silently continue polling on network error
                }
            }
        }
    }

    private suspend fun onPaymentSuccess(idcliente: Int, estiloSelecionado: String) {
        val oldIds = WalletCache.cartoesList.map { it.idControle }.toSet()
        
        // Refresh cache
        WalletCache.clear()
        WalletCache.preLoad(idcliente, forceRefresh = true)
        
        val newCard = WalletCache.cartoesList.find { it.idControle !in oldIds }
        
        // Apply style override (using the same mechanism as in VirtualCardScreen.kt)
        newCard?.let {
            // We need a way to access cardStyleOverrides. 
            // In VirtualCardScreen.kt it is a private top-level property.
            // For now, we'll assume the UI will handle the override when it sees the Success state.
        }

        _uiState.value = VirtualCardState.Success(newCard)
    }

    fun resetState() {
        pollingJob?.cancel()
        _uiState.value = VirtualCardState.Idle
    }
}
