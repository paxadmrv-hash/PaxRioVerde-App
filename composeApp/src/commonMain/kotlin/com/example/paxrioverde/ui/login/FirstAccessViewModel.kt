package com.example.paxrioverde.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paxrioverde.api.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FirstAccessUiState(
    val cpf: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class FirstAccessViewModel(
    private val api: ApiService
) : ViewModel() {
    var uiState by mutableStateOf(FirstAccessUiState())
        private set

    fun onCpfChange(value: String) {
        uiState = uiState.copy(cpf = value.filter { it.isDigit() }.take(11))
    }

    fun onPhoneChange(value: String) {
        uiState = uiState.copy(phone = value.filter { it.isDigit() })
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun onConfirmPasswordChange(value: String) {
        uiState = uiState.copy(confirmPassword = value)
    }

    fun togglePasswordVisibility() {
        uiState = uiState.copy(passwordVisible = !uiState.passwordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        uiState = uiState.copy(confirmPasswordVisible = !uiState.confirmPasswordVisible)
    }

    fun handleRegister(onSuccess: () -> Unit) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val response = api.registrar(
                    cpf = uiState.cpf,
                    celular = uiState.phone,
                    email = uiState.email,
                    senha = uiState.password
                )
                if (response.success) {
                    uiState = uiState.copy(successMessage = "Solicitação enviada com sucesso!", isLoading = false)
                    delay(2000)
                    onSuccess()
                } else {
                    uiState = uiState.copy(errorMessage = response.message ?: "Erro ao solicitar acesso", isLoading = false)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Erro de conexão: ${e.message}", isLoading = false)
            }
        }
    }
}
