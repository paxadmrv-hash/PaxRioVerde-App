package com.example.paxrioverde.ui.virtualcard

import androidx.compose.ui.graphics.ImageBitmap

actual suspend fun renderPdfBase64ToBitmap(base64Str: String): ImageBitmap? {
    // Implementação para Web
    return null
}

actual fun isCardExpired(validity: String): Boolean {
    // Implementação para Web
    return false
}
