package com.example.paxrioverde.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paxrioverde.api.AnoItem
import com.example.paxrioverde.api.MensalidadeItem
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.model.PlanStatus
import com.example.paxrioverde.domain.repository.FinanceRepository
import com.example.paxrioverde.ui.notifications.NotificationType
import com.example.paxrioverde.util.NotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class FinanceUiState(
    val anosData: List<AnoItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedYear: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.year,
    val selectedMensalidade: MensalidadeItem? = null,
    val oldestUnpaid: MensalidadeItem? = null,
    val planStatus: PlanStatus = PlanStatus.ACTIVE,
    val isGeneratingPayment: Boolean = false,
    val pixCode: String? = null,
    val barCode: String? = null,
    val showPixDialog: Boolean = false,
    val showBoletoDialog: Boolean = false
)

class FinanceViewModel(
    private val repository: FinanceRepository,
    private val notificationManager: NotificationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMensalidades(idcliente: Int) {
        if (idcliente == 0) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = repository.getMensalidades(idcliente)
            
            when (result) {
                is NetworkResult.Success -> {
                    val anos = result.data
                    val oldest = repository.getOldestUnpaid(anos)
                    val status = repository.calculatePlanStatus(anos)
                    _uiState.update { 
                        it.copy(
                            anosData = anos, 
                            oldestUnpaid = oldest, 
                            planStatus = status,
                            isLoading = false
                        ) 
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun selectYear(year: Int) {
        _uiState.update { it.copy(selectedYear = year) }
    }

    fun selectMensalidade(item: MensalidadeItem?) {
        _uiState.update { it.copy(selectedMensalidade = item) }
    }

    fun dismissPixDialog() {
        _uiState.update { it.copy(showPixDialog = false, pixCode = null) }
    }

    fun dismissBoletoDialog() {
        _uiState.update { it.copy(showBoletoDialog = false, barCode = null) }
    }

    fun gerarPix(idcaixa: Int, totalValor: String, onSuccess: () -> Unit) {
        val mens = _uiState.value.selectedMensalidade ?: _uiState.value.oldestUnpaid
        if (mens == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingPayment = true) }
            
            val result = repository.gerarPix(idcaixa, mens, totalValor)

            when (result) {
                is NetworkResult.Success -> {
                    val pixResponse = result.data
                    if (!pixResponse.pixCode.isNullOrEmpty()) {
                        _uiState.update { 
                            it.copy(pixCode = pixResponse.pixCode, showPixDialog = true, isGeneratingPayment = false)
                        }
                        
                        notificationManager.addNotification(
                            title = "PIX Gerado",
                            message = "O código para a mensalidade de R$ $totalValor foi gerado com sucesso.",
                            type = NotificationType.PAYMENT
                        )
                        onSuccess()
                    } else {
                        _uiState.update { 
                            it.copy(isGeneratingPayment = false, errorMessage = pixResponse.message ?: "Erro ao gerar PIX")
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isGeneratingPayment = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun gerarBoleto(totalValor: String, onSuccess: () -> Unit) {
        val mens = _uiState.value.selectedMensalidade ?: _uiState.value.oldestUnpaid
        if (mens == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingPayment = true) }
            
            val result = repository.gerarBoleto(mens, totalValor)

            when (result) {
                is NetworkResult.Success -> {
                    val boletoResponse = result.data
                    if (boletoResponse.status == null) {
                        _uiState.update { 
                            it.copy(barCode = boletoResponse.codigoBarra ?: "Boleto disponível no PDF", showBoletoDialog = true, isGeneratingPayment = false)
                        }
                        
                        notificationManager.addNotification(
                            title = "Boleto Gerado",
                            message = "A linha digitável da mensalidade de R$ $totalValor já está disponível.",
                            type = NotificationType.PAYMENT
                        )
                        onSuccess()
                    } else {
                        _uiState.update { it.copy(isGeneratingPayment = false, errorMessage = boletoResponse.status) }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isGeneratingPayment = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun getHistoryInvoices(): List<MensalidadeItem> {
        return repository.getHistoryInvoices(_uiState.value.anosData, _uiState.value.selectedYear)
    }

    fun getYears(): List<Int> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return _uiState.value.anosData.map { it.ano }
            .filter { it <= today.year }
            .distinct()
            .sortedDescending()
    }
}
