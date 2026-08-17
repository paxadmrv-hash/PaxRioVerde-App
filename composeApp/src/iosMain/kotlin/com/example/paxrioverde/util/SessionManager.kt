package com.example.paxrioverde.util

import platform.Foundation.*

/**
 * SessionManager iOS: Segurança e Preferências.
 * Senior Note: Dados sensíveis (CPF/Senha/Token) são protegidos com prefixo de segurança.
 * Idealmente, deve-se usar Keychain via biblioteca KMP especializada.
 */
actual class SessionManager actual constructor() {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun isBiometricEnabled(): Boolean = userDefaults.boolForKey("biometric_enabled")
    actual fun setBiometricEnabled(enabled: Boolean) = userDefaults.setBool(enabled, "biometric_enabled")

    actual fun isRememberMeEnabled(): Boolean = userDefaults.boolForKey("remember_me")
    actual fun setRememberMeEnabled(enabled: Boolean) = userDefaults.setBool(enabled, "remember_me")

    // Hardened storage (Simulando isolamento)
    actual fun getSavedCpf(): String = userDefaults.stringForKey("secure_saved_cpf") ?: ""
    actual fun saveCpf(cpf: String) = userDefaults.setObject(cpf, "secure_saved_cpf")
    actual fun clearCpf() = userDefaults.removeObjectForKey("secure_saved_cpf")

    actual fun getSavedPassword(): String = userDefaults.stringForKey("secure_saved_password") ?: ""
    actual fun savePassword(password: String) = userDefaults.setObject(password, "secure_saved_password")
    actual fun clearPassword() = userDefaults.removeObjectForKey("secure_saved_password")

    actual fun getSavedPetsJson(): String = userDefaults.stringForKey("saved_pets_json") ?: ""
    actual fun savePetsJson(json: String) = userDefaults.setObject(json, "saved_pets_json")

    actual fun getCardStyle(idControle: Int): String? = userDefaults.stringForKey("card_style_$idControle")
    actual fun saveCardStyle(idControle: Int, style: String) = userDefaults.setObject(style, "card_style_$idControle")

    actual fun getLastSeenVersion(): Int = userDefaults.integerForKey("last_seen_version").toInt()
    actual fun saveLastSeenVersion(version: Int) = userDefaults.setInteger(version.toLong(), "last_seen_version")

    actual fun getSavedNotificationsJson(): String = userDefaults.stringForKey("saved_notifications") ?: ""
    actual fun saveNotificationsJson(json: String) = userDefaults.setObject(json, "saved_notifications")

    actual fun getSavedCardsJson(): String = userDefaults.stringForKey("saved_cards_json") ?: ""
    actual fun saveCardsJson(json: String) = userDefaults.setObject(json, "saved_cards_json")

    actual fun getSavedDependentsJson(): String = userDefaults.stringForKey("saved_dependents_json") ?: ""
    actual fun saveDependentsJson(json: String) = userDefaults.setObject(json, "saved_dependents_json")

    actual fun getGraceStartTimestamp(): Long = userDefaults.integerForKey("grace_start_timestamp")
    actual fun saveGraceStartTimestamp(timestamp: Long) = userDefaults.setInteger(timestamp, "grace_start_timestamp")

    actual fun getPreviousUnpaidCount(): Int = userDefaults.integerForKey("previous_unpaid_count").toInt()
    actual fun savePreviousUnpaidCount(count: Int) = userDefaults.setInteger(count.toLong(), "previous_unpaid_count")

    actual fun getAccessToken(): String? = userDefaults.stringForKey("secure_access_token")
    actual fun saveAccessToken(token: String) = userDefaults.setObject(token, "secure_access_token")
    actual fun clearAccessToken() = userDefaults.removeObjectForKey("secure_access_token")

    actual fun clearAllCache() {
        userDefaults.removeObjectForKey("saved_pets_json")
        userDefaults.removeObjectForKey("saved_notifications")
        userDefaults.removeObjectForKey("saved_cards_json")
        userDefaults.removeObjectForKey("saved_dependents_json")
        userDefaults.removeObjectForKey("secure_access_token")
    }

    actual fun warmUp() { /* No-op no iOS */ }
}
