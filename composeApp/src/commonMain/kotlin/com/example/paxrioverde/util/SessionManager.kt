package com.example.paxrioverde.util

expect class SessionManager() {
    fun isBiometricEnabled(): Boolean
    fun setBiometricEnabled(enabled: Boolean)

    fun isRememberMeEnabled(): Boolean
    fun setRememberMeEnabled(enabled: Boolean)

    fun getSavedCpf(): String
    fun saveCpf(cpf: String)
    fun clearCpf()

    fun getSavedPassword(): String
    fun savePassword(password: String)
    fun clearPassword()

    fun getSavedPetsJson(): String
    fun savePetsJson(json: String)

    fun getCardStyle(idControle: Int): String?
    fun saveCardStyle(idControle: Int, style: String)

    fun getLastSeenVersion(): Int
    fun saveLastSeenVersion(version: Int)

    fun getSavedNotificationsJson(): String
    fun saveNotificationsJson(json: String)

    fun getSavedCardsJson(): String
    fun saveCardsJson(json: String)

    fun getSavedDependentsJson(): String
    fun saveDependentsJson(json: String)

    fun getGraceStartTimestamp(): Long
    fun saveGraceStartTimestamp(timestamp: Long)

    fun getPreviousUnpaidCount(): Int
    fun savePreviousUnpaidCount(count: Int)

    fun getAccessToken(): String?
    fun saveAccessToken(token: String)
    fun clearAccessToken()

    /**
     * Senior Performance: Limpa todos os caches de dados (cartões, dependentes, etc).
     * Usado no logout ou troca de usuário.
     */
    fun clearAllCache()

    /**
     * Senior Performance: Inicializa a criptografia pesada em background.
     */
    fun warmUp()
}
