package com.example.paxrioverde.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.paxrioverde.ui.theme.PaxDesignSystem

/**
 * Cabeçalho unificado com o gradiente da marca.
 * Utiliza o conceito de "Slots" para permitir conteúdo flexível.
 * Senior: Agora aceita [backgroundBrush] para permitir versões Dark ou Transparentes.
 */
@Composable
fun PaxScreenHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundBrush: Brush = PaxDesignSystem.Gradients.Primary,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundBrush)
            .clip(PaxDesignSystem.Shapes.ExtraLarge)
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)
        ) {
            // Botão Voltar Padronizado
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .offset(x = (-12).dp)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = PaxDesignSystem.Colors.White
                )
            }
            
            // Aqui entra o conteúdo dinâmico de cada tela (Slot)
            content()
        }
    }
}

/**
 * Utilitário para exibir o título principal dentro do Header.
 */
@Composable
fun HeaderTitle(
    title: String,
    subtitle: String? = null,
    color: Color = PaxDesignSystem.Colors.White
) {
    Column {
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = color.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = title,
            color = color,
            style = MaterialTheme.typography.headlineLarge,
            // Adicionando um peso extra para o headline se necessário
        )
    }
}
