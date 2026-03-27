package com.example.paxrioverde.ui.pet

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun AsyncImageWrapper(
    uri: String?,
    placeholder: org.jetbrains.compose.resources.DrawableResource,
    modifier: Modifier
) {
    // No JVM/Desktop, usamos o placeholder. 
    // Para suporte real a imagens, bibliotecas como Coil3 ou KMP-Image-Loader seriam necessárias.
    Image(
        painter = painterResource(placeholder),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
