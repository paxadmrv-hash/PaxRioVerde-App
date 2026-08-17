package com.example.paxrioverde.util

/**
 * PaxLogger: Sistema de logs centralizado para o aplicativo Pax Rio Verde.
 * Senior Strategy: Facilita o monitoramento em debug e evita vazamento de 
 * informações sensíveis em produção.
 */
object PaxLogger {
    
    private const val TAG = "PAX_RIO_VERDE"

    /**
     * Log de Informação
     */
    fun i(message: String, subTag: String? = null) {
        if (!isDebug) return
        val finalTag = subTag?.let { "[$it]" } ?: ""
        println("INFO: $TAG$finalTag -> $message")
    }

    /**
     * Log de Erro
     */
    fun e(message: String, throwable: Throwable? = null, subTag: String? = null) {
        if (!isDebug) return
        val finalTag = subTag?.let { "[$it]" } ?: ""
        println("ERROR: $TAG$finalTag -> $message")
        throwable?.message?.let { println("CAUSE: $it") }
        // Em um ambiente de produção, aqui integraríamos com Crashlytics ou Sentry
    }

    /**
     * Log de Debug (Apenas para desenvolvimento)
     */
    fun d(message: String, subTag: String? = null) {
        if (!isDebug) return
        val finalTag = subTag?.let { "[$it]" } ?: ""
        println("DEBUG: $TAG$finalTag -> $message")
    }
}
