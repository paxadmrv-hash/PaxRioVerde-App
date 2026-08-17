package com.example.paxrioverde.data.repository

import com.example.paxrioverde.api.ApiService
import com.example.paxrioverde.api.LoginResponse
import com.example.paxrioverde.data.util.safeApiCall
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.repository.AuthRepository
import com.example.paxrioverde.util.SessionManager

/**
 * Implementação do repositório de autenticação com resiliência de rede.
 */
class AuthRepositoryImpl(
    private val api: ApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(cpf: String, pass: String, rememberMe: Boolean): NetworkResult<LoginResponse> {
        return safeApiCall {
            val cleanCpf = cpf.filter { it.isDigit() }
            val response = api.login(cleanCpf, pass)
            
            // Senior Fix: Garante que o CPF esteja no objeto mesmo que o servidor não retorne
            val finalResponse = if (response.success && response.cpf.isNullOrEmpty()) {
                response.copy(cpf = cleanCpf)
            } else {
                response
            }

            if (finalResponse.success) {
                finalResponse.token?.let { sessionManager.saveAccessToken(it) }
                handleRememberMe(cleanCpf, pass, rememberMe)
            }
            finalResponse
        }
    }

    private fun handleRememberMe(cpf: String, pass: String, rememberMe: Boolean) {
        if (rememberMe) {
            sessionManager.setRememberMeEnabled(true)
            sessionManager.saveCpf(cpf)
            sessionManager.savePassword(pass)
        } else {
            sessionManager.setRememberMeEnabled(false)
            // Senior Security: Só limpa se a biometria também estiver desativada
            if (!sessionManager.isBiometricEnabled()) {
                sessionManager.clearCpf()
                sessionManager.clearPassword()
            }
        }
    }

    override fun getSavedCredentials(): Pair<String, String>? {
        if (sessionManager.isRememberMeEnabled()) {
            val cpf = sessionManager.getSavedCpf()
            val pass = sessionManager.getSavedPassword()
            return if (cpf.isNotEmpty() && pass.isNotEmpty()) cpf to pass else null
        }
        return null
    }

    override fun getCredentialsForBiometrics(): Pair<String, String>? {
        val cpf = sessionManager.getSavedCpf()
        val pass = sessionManager.getSavedPassword()
        return if (cpf.isNotEmpty() && pass.isNotEmpty()) cpf to pass else null
    }

    override fun hasSavedCredentials(): Boolean {
        return sessionManager.getSavedCpf().isNotEmpty() && sessionManager.getSavedPassword().isNotEmpty()
    }

    override fun updateBiometricStatus(enabled: Boolean) {
        sessionManager.setBiometricEnabled(enabled)
        if (!enabled && !sessionManager.isRememberMeEnabled()) {
            sessionManager.clearCpf()
            sessionManager.clearPassword()
        }
    }

    override fun isRememberMeEnabled(): Boolean = sessionManager.isRememberMeEnabled()
}
