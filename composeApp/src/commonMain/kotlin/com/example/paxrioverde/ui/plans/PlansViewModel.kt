package com.example.paxrioverde.ui.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paxrioverde.api.WalletCache
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.model.PlanStatus
import com.example.paxrioverde.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlansUiState(
    val planStatus: PlanStatus = PlanStatus.ACTIVE,
    val isLoading: Boolean = false
)

class PlansViewModel(
    val walletCache: WalletCache,
    private val financeRepository: FinanceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlansUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData(idcliente: Int) {
        if (idcliente != 0) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                walletCache.preLoad(idcliente)
                
                // Buscar mensalidades para calcular o status
                val result = financeRepository.getMensalidades(idcliente)
                if (result is NetworkResult.Success) {
                    val status = financeRepository.calculatePlanStatus(result.data)
                    _uiState.update { it.copy(planStatus = status, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun atualizarCpfDependente(idcliente: Int, cpf: String, nomeDependente: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = financeRepository.atualizarCpfDependente(idcliente, cpf, nomeDependente)
            when (result) {
                is NetworkResult.Success -> {
                    // 1. Atualização Local Instantânea com Busca Robusta
                    val normalizedTarget = nomeDependente.trim().lowercase()
                    val index = walletCache.dependentesList.indexOfFirst { 
                        (it.nomeDependente ?: "").trim().lowercase() == normalizedTarget 
                    }
                    
                    if (index != -1) {
                        val currentDep = walletCache.dependentesList[index]
                        walletCache.dependentesList[index] = currentDep.copy(cpf = cpf)
                        // Senior Fix: Persiste imediatamente no cache offline para evitar perda ao fechar o app
                        walletCache.saveDependentsToPersistentCache()
                    }

                    // 2. Feedback imediato para o modal fechar
                    onComplete(true, "CPF atualizado com sucesso!")

                    // 3. Sincronização em Background Otimizada
                    // Aumentamos o delay para 10s para garantir que o BD do servidor tenha persistido
                    // Usamos o Silent Refresh do WalletCache para não causar flicker
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(10000) 
                        walletCache.preLoad(idcliente, forceRefresh = true)
                    }
                }
                is NetworkResult.Error -> onComplete(false, result.message ?: "Erro ao atualizar CPF")
                else -> onComplete(false, "Erro desconhecido")
            }
        }
    }
}
