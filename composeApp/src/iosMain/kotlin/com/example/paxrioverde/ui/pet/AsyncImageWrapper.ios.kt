package com.example.paxrioverde.ui.pet

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image as SkiaImage
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import platform.UIKit.UIImage
import platform.Foundation.NSData
import platform.Foundation.NSDataBase64DecodingIgnoreUnknownCharacters
import platform.Foundation.create
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned

@OptIn(ExperimentalForeignApi::class)
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
                val nsData = NSData.create(base64EncodedString = base64String, options = NSDataBase64DecodingIgnoreUnknownCharacters)
                if (nsData != null) {
                    val bytes = nsData.toByteArray()
                    val skiaImage = SkiaImage.makeFromEncoded(bytes)
                    imageBitmap = skiaImage.toComposeImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                imageBitmap = null
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
