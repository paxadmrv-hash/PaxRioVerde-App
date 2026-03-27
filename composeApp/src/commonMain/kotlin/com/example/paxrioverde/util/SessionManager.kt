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
}
