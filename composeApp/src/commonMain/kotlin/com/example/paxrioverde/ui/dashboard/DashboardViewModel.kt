package com.example.paxrioverde.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.model.PlanStatus
import com.example.paxrioverde.domain.repository.FinanceRepository
import com.example.paxrioverde.util.BillingNotificationManager
import com.example.paxrioverde.util.SessionManager
import com.example.paxrioverde.util.getNotificationScheduler
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource

data class DashboardUiState(
    val expandedImageRes: DrawableResource? = null,
    val showWhatsNew: Boolean = false,
    val planStatus: PlanStatus = PlanStatus.ACTIVE
)

class DashboardViewModel(
    private val sessionManager: SessionManager,
    private val financeRepository: FinanceRepository
) : ViewModel() {
    var uiState by mutableStateOf(DashboardUiState())
        private set

    fun checkWhatsNew(currentVersion: Int) {
        val lastSeenVersion = sessionManager.getLastSeenVersion()
        if (currentVersion > lastSeenVersion) {
            uiState = uiState.copy(showWhatsNew = true)
        }
    }

    fun dismissWhatsNew(version: Int) {
        uiState = uiState.copy(showWhatsNew = false)
        sessionManager.saveLastSeenVersion(version)
    }

    private var lastScheduledDate: String? = null

    fun scheduleNotifications(dueDate: String?) {
        if (!dueDate.isNullOrEmpty() && dueDate != "--/--/----" && dueDate != lastScheduledDate) {
            BillingNotificationManager.scheduleBillingNotifications(dueDate)
            getNotificationScheduler().requestPermission()
            lastScheduledDate = dueDate
        }
    }

    fun onImageClick(res: DrawableResource?) {
        uiState = uiState.copy(expandedImageRes = res)
    }

    fun updatePlanStatus(idcliente: Int) {
        if (idcliente == 0) return
        
        viewModelScope.launch {
            val result = financeRepository.getMensalidades(idcliente)
            if (result is NetworkResult.Success) {
                val status = financeRepository.calculatePlanStatus(result.data)
                uiState = uiState.copy(planStatus = status)
            }
        }
    }
}
