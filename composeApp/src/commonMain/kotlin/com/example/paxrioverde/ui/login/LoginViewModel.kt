package com.example.paxrioverde.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paxrioverde.api.LoginResponse
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val cpf: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val suggestFirstAccess: Boolean = false,
    val loginResponse: LoginResponse? = null,
    val profiles: List<LoginResponse> = emptyList(),
    val showProfileSelection: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSavedCredentials()
    }

    private fun loadSavedCredentials() {
        authRepository.getSavedCredentials()?.let { (cpf, password) ->
            _uiState.update { 
                it.copy(cpf = cpf, password = password, rememberMe = true)
            }
        }
    }

    fun onCpfChange(newCpf: String) {
        _uiState.update { 
            it.copy(
                cpf = newCpf.filter { char -> char.isDigit() }.take(11), 
                errorMessage = null,
                suggestFirstAccess = false
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null, suggestFirstAccess = false) }
    }

    fun onRememberMeChange(enabled: Boolean) {
        _uiState.update { it.copy(rememberMe = enabled) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun login(onSuccess: (LoginResponse) -> Unit) {
        val currentState = _uiState.value
        
        if (currentState.cpf.length < 11 || currentState.password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos corretamente") }
            return
        }

        executeLogin(currentState.cpf, currentState.password, currentState.rememberMe, onSuccess)
    }

    fun performBiometricLogin(onSuccess: (LoginResponse) -> Unit) {
        val credentials = authRepository.getCredentialsForBiometrics()
        if (credentials != null) {
            val (cpf, password) = credentials
            executeLogin(cpf, password, true, onSuccess)
        } else {
            _uiState.update { it.copy(errorMessage = "Credenciais biométricas não encontradas") }
        }
    }

    private fun executeLogin(cpf: String, pass: String, remember: Boolean, onSuccess: (LoginResponse) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = authRepository.login(cpf, pass, remember)

            when (result) {
                is NetworkResult.Success -> {
                    val profiles = result.data
                    if (profiles.isNotEmpty() && profiles.any { it.success }) {
                        if (profiles.size > 1) {
                            _uiState.update { it.copy(isLoading = false, profiles = profiles, showProfileSelection = true) }
                        } else {
                            val response = profiles.first { it.success }
                            _uiState.update { it.copy(isLoading = false, loginResponse = response) }
                            onSuccess(response)
                        }
                    } else {
                        val response = profiles.firstOrNull()
                        val msg = response?.message ?: "CPF ou senha inválidos"
                        val shouldSuggest = msg.contains("senha não cadastrada", ignoreCase = true) || 
                                           msg.contains("primeiro acesso", ignoreCase = true) ||
                                           msg.contains("usuário sem senha", ignoreCase = true) ||
                                           msg.contains("não possui acesso", ignoreCase = true) ||
                                           msg.contains("não encontrado", ignoreCase = true) ||
                                           msg.contains("não cadastrado", ignoreCase = true) ||
                                           msg.contains("inválido", ignoreCase = true)

                        _uiState.update { it.copy(
                            isLoading = false, 
                            errorMessage = msg,
                            suggestFirstAccess = shouldSuggest
                        ) }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetProfileSelection() {
        _uiState.update { it.copy(showProfileSelection = false) }
    }

    fun onBiometricError(error: String) {
        _uiState.update { it.copy(errorMessage = "Erro na biometria: $error") }
    }
}
