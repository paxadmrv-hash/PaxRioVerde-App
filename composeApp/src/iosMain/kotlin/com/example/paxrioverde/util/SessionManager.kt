package com.example.paxrioverde.util

import platform.Foundation.NSUserDefaults

actual class SessionManager actual constructor() {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun isBiometricEnabled(): Boolean {
        return userDefaults.boolForKey("biometric_enabled")
    }

    actual fun setBiometricEnabled(enabled: Boolean) {
        userDefaults.setBool(enabled, "biometric_enabled")
    }

    actual fun isRememberMeEnabled(): Boolean {
        return userDefaults.boolForKey("remember_me")
    }

    actual fun setRememberMeEnabled(enabled: Boolean) {
        userDefaults.setBool(enabled, "remember_me")
    }

    actual fun getSavedCpf(): String {
        return userDefaults.stringForKey("saved_cpf") ?: ""
    }

    actual fun saveCpf(cpf: String) {
        userDefaults.setObject(cpf, "saved_cpf")
    }

    actual fun clearCpf() {
        userDefaults.removeObjectForKey("saved_cpf")
    }

    actual fun getSavedPassword(): String {
        return userDefaults.stringForKey("saved_password") ?: ""
    }

    actual fun savePassword(password: String) {
        userDefaults.setObject(password, "saved_password")
    }

    actual fun clearPassword() {
        userDefaults.removeObjectForKey("saved_password")
    }
}
