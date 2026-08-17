package com.example.paxrioverde.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun AsyncImageWrapper(
    uri: String?,
    placeholder: org.jetbrains.compose.resources.DrawableResource,
    modifier: Modifier
) {
    var imageBitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        if (uri.isNullOrEmpty()) {
            imageBitmap = null
            return@LaunchedEffect
        }

        try {
            val imageBytes = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                if (uri.startsWith("data:image")) {
                    val base64String = uri.substringAfter("base64,")
                    Base64.decode(base64String, Base64.DEFAULT)
                } else if (uri.startsWith("http")) {
                    // Senior Note: Carregamento de URL com Downsampling (Executado em IO Thread)
                    val connection = java.net.URL(uri).openConnection()
                    connection.connect()
                    connection.getInputStream().use { it.readBytes() }
                } else null
            }

            if (imageBytes != null) {
                imageBitmap = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
                    options.inSampleSize = calculateInSampleSize(options, 512, 512)
                    options.inJustDecodeBounds = false
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
                    bitmap?.asImageBitmap()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imageBitmap = null
        }
    }

    val bitmap = imageBitmap
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(placeholder),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Senior Utility: Calcula o fator de escala para decodificação eficiente.
 */
private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
