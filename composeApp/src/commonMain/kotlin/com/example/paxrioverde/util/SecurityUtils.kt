package com.example.paxrioverde.util

/**
 * Utilitário de segurança para controle de captura de tela e privacidade.
 * expect fun para permitir comportamentos nativos (FLAG_SECURE no Android, AppSwitcher no iOS).
 */
expect fun setScreenSecurity(enabled: Boolean)

/**
 * Verifica se o dispositivo está com Root (Android) ou Jailbreak (iOS).
 */
expect fun isDeviceRooted(): Boolean

