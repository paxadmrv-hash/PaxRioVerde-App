package com.example.paxrioverde.domain.repository

import com.example.paxrioverde.api.LoginResponse
import com.example.paxrioverde.domain.model.NetworkResult

/**
 * Interface que define as operações de autenticação do domínio.
 */
interface AuthRepository {
    /**
     * Realiza o login retornando um [NetworkResult].
     */
    suspend fun login(cpf: String, pass: String, rememberMe: Boolean): NetworkResult<LoginResponse>
    
    /**
     * Retorna as credenciais salvas (CPF e Senha) caso o "Lembrar" esteja ativo.
     */
    fun getSavedCredentials(): Pair<String, String>?
    
    /**
     * Verifica se a opção de lembrar login está ativa.
     */
    fun isRememberMeEnabled(): Boolean

    /**
     * Verifica se há credenciais salvas (para biometria ou lembrar login).
     */
    fun hasSavedCredentials(): Boolean

    /**
     * Retorna as credenciais salvas independente do estado de "Lembrar login".
     * Usado para autenticação biométrica.
     */
    fun getCredentialsForBiometrics(): Pair<String, String>?

    /**
     * Atualiza o estado da biometria e limpa dados se necessário.
     */
    fun updateBiometricStatus(enabled: Boolean)
}
