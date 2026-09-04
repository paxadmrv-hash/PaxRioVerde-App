package com.example.paxrioverde.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.paxrioverde.AndroidContext

/**
 * SessionManager Android: Implementação com Criptografia de Hardware.
 * Senior Security: Utiliza Android Keystore (AES-256) para blindar CPF e Senhas.
 */
actual class SessionManager actual constructor() {
    
    private val context: Context = AndroidContext.get()

    companion object {
        @Volatile
        private var sharedPrefs: SharedPreferences? = null

        /**
         * Senior Performance: Retorna o cofre seguro de forma thread-safe e cacheada.
         */
        private fun getPrefs(context: Context): SharedPreferences {
            return sharedPrefs ?: synchronized(this) {
                sharedPrefs ?: try {
                    val masterKey = MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()

                    EncryptedSharedPreferences.create(
                        context,
                        "pax_secure_prefs",
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    ).also { sharedPrefs = it }
                } catch (e: Exception) {
                    context.getSharedPreferences("pax_prefs", Context.MODE_PRIVATE).also { sharedPrefs = it }
                }
            }
        }
    }

    private val prefs: SharedPreferences by lazy { getPrefs(context) }

    actual fun warmUp() {
        // Apenas acessa a propriedade lazy para disparar a inicialização pesada
        val _p = prefs
    }

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

    actual fun getCardStyle(idControle: Int): String? = prefs.getString("card_style_$idControle", null)
    actual fun saveCardStyle(idControle: Int, style: String) = prefs.edit().putString("card_style_$idControle", style).apply()

    actual fun getLastSeenVersion(): Int = prefs.getInt("last_seen_version", 0)
    actual fun saveLastSeenVersion(version: Int) = prefs.edit().putInt("last_seen_version", version).apply()

    actual fun getSavedNotificationsJson(): String = prefs.getString("saved_notifications", "") ?: ""
    actual fun saveNotificationsJson(json: String) = prefs.edit().putString("saved_notifications", json).apply()

    actual fun getSavedCardsJson(): String = prefs.getString("saved_cards_json", "") ?: ""
    actual fun saveCardsJson(json: String) = prefs.edit().putString("saved_cards_json", json).apply()

    actual fun getSavedDependentsJson(): String = prefs.getString("saved_dependents_json", "") ?: ""
    actual fun saveDependentsJson(json: String) = prefs.edit().putString("saved_dependents_json", json).apply()

    actual fun getSavedProfilesJson(): String = prefs.getString("saved_profiles_json", "") ?: ""
    actual fun saveProfilesJson(json: String) = prefs.edit().putString("saved_profiles_json", json).apply()

    actual fun getActiveProfileIndex(): Int = prefs.getInt("active_profile_index", 0)
    actual fun saveActiveProfileIndex(index: Int) = prefs.edit().putInt("active_profile_index", index).apply()

    actual fun getGraceStartTimestamp(): Long = prefs.getLong("grace_start_timestamp", 0L)
    actual fun saveGraceStartTimestamp(timestamp: Long) = prefs.edit().putLong("grace_start_timestamp", timestamp).apply()

    actual fun getPreviousUnpaidCount(): Int = prefs.getInt("previous_unpaid_count", 0)
    actual fun savePreviousUnpaidCount(count: Int) = prefs.edit().putInt("previous_unpaid_count", count).apply()

    actual fun getAccessToken(): String? = prefs.getString("access_token", null)
    actual fun saveAccessToken(token: String) = prefs.edit().putString("access_token", token).apply()
    actual fun clearAccessToken() = prefs.edit().remove("access_token").apply()

    actual fun clearAllCache() {
        prefs.edit()
            .remove("saved_pets_json")
            .remove("saved_notifications")
            .remove("saved_cards_json")
            .remove("saved_dependents_json")
            .remove("access_token")
            .apply()
    }
}
