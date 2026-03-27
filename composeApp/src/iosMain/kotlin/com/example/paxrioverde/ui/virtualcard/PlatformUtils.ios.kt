package com.example.paxrioverde.ui.virtualcard

import androidx.compose.ui.graphics.ImageBitmap

actual suspend fun renderPdfBase64ToBitmap(base64Str: String): ImageBitmap? {
    // Implementação para iOS usaria PDFKit, por enquanto retorna null para compilar
    return null
}

actual fun isCardExpired(validity: String): Boolean {
    // Implementação simplificada para iOS ou usar KNSDate
    return false
}
