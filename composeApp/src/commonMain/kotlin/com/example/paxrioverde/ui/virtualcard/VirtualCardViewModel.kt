package com.example.paxrioverde.ui.virtualcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paxrioverde.api.CartaoItem
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.repository.VirtualCardRepository
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

class VirtualCardViewModel(
    private val repository: VirtualCardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VirtualCardState>(VirtualCardState.Idle)
    val uiState: StateFlow<VirtualCardState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    fun gerarCartaoPix(idcaixa: Int, idcliente: Int, tipo: String, nomeDependente: String, estiloSelecionado: String) {
        viewModelScope.launch {
            _uiState.value = VirtualCardState.Loading
            
            val result = repository.gerarCartaoPix(idcaixa, idcliente, tipo, nomeDependente)

            when (result) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.success && response.pix != null && response.identificador_pix != null) {
                        _uiState.value = VirtualCardState.PixGenerated(response.pix, response.identificador_pix)
                        startPolling(response.identificador_pix, idcliente, estiloSelecionado)
                    } else {
                        _uiState.value = VirtualCardState.Error(response.message ?: "Erro ao gerar PIX")
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = VirtualCardState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    private fun startPolling(identificadorPix: String, idcliente: Int, estiloSelecionado: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                
                val result = repository.verificarPixPago(identificadorPix)
                
                if (result is NetworkResult.Success && result.data) {
                    onPaymentSuccess(idcliente, estiloSelecionado)
                    break
                }
            }
        }
    }

    fun gerarCartaoDireto(
        idcaixa: Int,
        idcliente: Int,
        tipo: String,
        nomeDependente: String?,
        isGratuito: Boolean,
        idcontrato: Int = 0,
        idconvenio: Int = 0,
        cpfDependente: String? = null,
        dtvencimento: String? = null,
        parentesco: String? = null,
        idfilial: Int = 0
    ) {
        viewModelScope.launch {
            _uiState.value = VirtualCardState.Loading
            
            val result = repository.gerarCartaoDireto(
                idcaixa, idcliente, tipo, nomeDependente, isGratuito,
                idcontrato, idconvenio, cpfDependente, dtvencimento, parentesco, idfilial
            )

            when (result) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.success) {
                        onPaymentSuccess(idcliente, "Adulto")
                    } else {
                        _uiState.value = VirtualCardState.Error(response.message ?: "Erro ao gerar cartão")
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = VirtualCardState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    private suspend fun onPaymentSuccess(idcliente: Int, estiloSelecionado: String) {
        val oldIds = repository.getCartoes().map { it.idControle }.toSet()
        
        // Refresh cache via Repository
        repository.refreshData(idcliente)
        
        val newCard = repository.getCartoes().find { it.idControle !in oldIds }
        
        _uiState.value = VirtualCardState.Success(newCard)
    }

    fun resetState() {
        pollingJob?.cancel()
        _uiState.value = VirtualCardState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
