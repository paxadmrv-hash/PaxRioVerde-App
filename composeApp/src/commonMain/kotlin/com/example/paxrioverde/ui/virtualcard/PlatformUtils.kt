package com.example.paxrioverde.ui.virtualcard

import androidx.compose.ui.graphics.ImageBitmap

expect suspend fun renderPdfBase64ToBitmap(base64Str: String): ImageBitmap?

expect fun isCardExpired(validity: String): Boolean
