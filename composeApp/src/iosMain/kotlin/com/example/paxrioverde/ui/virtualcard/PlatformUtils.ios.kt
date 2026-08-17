package com.example.paxrioverde.ui.virtualcard

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.Foundation.NSBundle

actual suspend fun renderPdfBase64ToBitmap(base64Str: String): ImageBitmap? {
    // Implementação para iOS usaria PDFKit, por enquanto retorna null para compilar
    return null
}

actual fun isCardExpired(validity: String): Boolean {
    // Implementação simplificada para iOS ou usar KNSDate
    return false
}

actual fun ImageBitmap.toByteArray(): ByteArray? {
    val skiaBitmap = this.asSkiaBitmap()
    return Image.makeFromBitmap(skiaBitmap).encodeToData(EncodedImageFormat.PNG, 100)?.bytes
}

actual fun getAppVersionCode(): Int {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion")?.toString()?.toIntOrNull() ?: 0
}
