package com.example.paxrioverde.util

import android.content.Context
import android.content.SharedPreferences
import com.example.paxrioverde.AndroidContext

actual class SessionManager actual constructor() {
    private val prefs: SharedPreferences = AndroidContext.get().getSharedPreferences("pax_prefs", Context.MODE_PRIVATE)

    actual fun isBiometricEnabled(): Boolean = prefs.getBoolean("biometric_enabled", false)
    actual fun setBiometricEnabled(enabled: Boolean) = prefs.edit().putBoolean("biometric_enabled", enabled).apply()
    actual fun isRememberMeEnabled(): Boolean = prefs.getBoolean("remember_me", false)
    actual fun setRememberMeEnabled(enabled: Boolean) = prefs.edit().putBoolean("remember_me", enabled).apply()
    actual fun getSavedCpf(): String = prefs.getString("saved_cpf", "") ?: ""
    actual fun saveCpf(cpf: String) = prefs.edit().putString("saved_cpf", cpf).apply()
    actual fun clearCpf() = prefs.edit().remove("saved_cpf").apply()
    actual fun getSavedPassword(): String = prefs.getString("saved_password", "") ?: ""
    actual fun savePassword(password: String) = prefs.edit().putString("saved_password", password).apply()
    actual fun clearPassword() = prefs.edit().remove("saved_password").apply()

    actual fun getSavedPetsJson(): String = prefs.getString("saved_pets_json", "") ?: ""
    actual fun savePetsJson(json: String) = prefs.edit().putString("saved_pets_json", json).apply()
}
