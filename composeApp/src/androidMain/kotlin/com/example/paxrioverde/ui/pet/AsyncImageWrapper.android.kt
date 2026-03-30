package com.example.paxrioverde.ui.pet

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
        if (uri != null && uri.startsWith("data:image")) {
            try {
                val base64String = uri.substringAfter("base64,")
                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageBitmap = bitmap?.asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
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
