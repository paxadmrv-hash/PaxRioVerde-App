package com.example.paxrioverde.ui.virtualcard

import androidx.compose.ui.graphics.ImageBitmap

actual suspend fun renderPdfBase64ToBitmap(base64Str: String): ImageBitmap? = null

actual fun isCardExpired(validity: String): Boolean = false

actual fun ImageBitmap.toByteArray(): ByteArray? = null
