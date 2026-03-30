package com.example.paxrioverde.util

/**
 * Codifica uma string para ser usada em uma URL (CommonMain)
 * Resolve problemas de caracteres especiais e espaços no iOS/Android
 */
fun urlEncode(text: String): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
    return text.encodeToByteArray().joinToString("") { byte ->
        val code = byte.toInt() and 0xFF
        val char = code.toChar()
        if (char in allowedChars) {
            char.toString()
        } else {
            "%" + code.toString(16).uppercase().padStart(2, '0')
        }
    }
}
