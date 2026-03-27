package com.example.paxrioverde.ui.virtualcard

import androidx.compose.ui.graphics.ImageBitmap

actual suspend fun renderPdfBase64ToBitmap(base64Str: String): ImageBitmap? {
    return null
}

actual fun isCardExpired(validity: String): Boolean {
    return false
}
