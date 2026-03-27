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
    // Implementação simplificada para iOS usando placeholder
    // Em um app real, usaria-se uma biblioteca como o Coil (quando disponível para Compose Multiplatform)
    // ou uma implementação nativa com UIImage
    Image(
        painter = painterResource(placeholder),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
