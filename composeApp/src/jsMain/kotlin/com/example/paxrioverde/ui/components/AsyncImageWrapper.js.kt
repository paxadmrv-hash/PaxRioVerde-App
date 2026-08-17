package com.example.paxrioverde.ui.components

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
    // Implementação básica para JS (pode ser melhorada com HTML Image)
    Image(
        painter = painterResource(placeholder),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
