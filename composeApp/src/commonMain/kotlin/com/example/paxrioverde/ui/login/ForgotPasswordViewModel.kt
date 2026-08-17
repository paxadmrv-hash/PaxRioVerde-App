package com.example.paxrioverde.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paxrioverde.api.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val step: Int = 1,
    val cpfOrEmail: String = "",
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ForgotPasswordViewModel(
    private val api: ApiService
) : ViewModel() {
    var uiState by mutableStateOf(ForgotPasswordUiState())
        private set

    fun onCpfOrEmailChange(value: String) {
        uiState = uiState.copy(cpfOrEmail = value)
    }

    fun onTokenChange(value: String) {
        uiState = uiState.copy(token = value.filter { it.isDigit() }.take(6))
    }

    fun onNewPasswordChange(value: String) {
        uiState = uiState.copy(newPassword = value)
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

    fun setStep(value: Int) {
        uiState = uiState.copy(step = value)
    }

    fun handleRequestToken() {
        if (uiState.cpfOrEmail.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Informe seu CPF ou E-mail")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val response = api.esquecerSenha(uiState.cpfOrEmail)
                if (response.success) {
                    uiState = uiState.copy(
                        successMessage = response.message ?: "Código enviado com sucesso!",
                        isLoading = false
                    )
                    delay(1500)
                    uiState = uiState.copy(successMessage = null, step = 2)
                } else {
                    uiState = uiState.copy(
                        errorMessage = response.message ?: "Erro ao solicitar recuperação",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Erro de conexão: ${e.message}", isLoading = false)
            }
        }
    }

    fun handleResetPassword(onSuccess: () -> Unit) {
        if (uiState.token.length != 6) {
            uiState = uiState.copy(errorMessage = "O código deve ter 6 dígitos")
            return
        }
        if (uiState.newPassword.length < 6) {
            uiState = uiState.copy(errorMessage = "A senha deve ter pelo menos 6 caracteres")
            return
        }
        if (uiState.newPassword != uiState.confirmPassword) {
            uiState = uiState.copy(errorMessage = "As senhas não coincidem")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val response = api.redefinirSenha(
                    cpfOrEmail = uiState.cpfOrEmail,
                    token = uiState.token,
                    senha = uiState.newPassword
                )
                if (response.success) {
                    uiState = uiState.copy(successMessage = "Senha redefinida com sucesso!", isLoading = false)
                    delay(2000)
                    onSuccess()
                } else {
                    uiState = uiState.copy(errorMessage = response.message ?: "Erro ao redefinir senha", isLoading = false)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Erro de conexão: ${e.message}", isLoading = false)
            }
        }
    }
}
