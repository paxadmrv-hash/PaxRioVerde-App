package com.example.paxrioverde.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.DrawableResource

/**
 * Carregador de imagem assíncrono com suporte a Base64 e URIs.
 * Senior Note: Abstração multiplataforma para carregamento eficiente de imagens.
 */
@Composable
expect fun AsyncImageWrapper(
    uri: String?,
    placeholder: DrawableResource,
    modifier: Modifier = Modifier
)
