package com.example.paxrioverde.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.paxrioverde.ui.theme.PaxDesignSystem

/**
 * Botão Padronizado do Pax Rio Verde.
 * Já inclui o estilo da marca e suporte a estado de carregamento.
 */
@Composable
fun PaxButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = PaxDesignSystem.Colors.BrandGreen,
    contentColor: Color = PaxDesignSystem.Colors.White
) {
    val haptic = LocalHapticFeedback.current
    
    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .bounceClick()
            .heightIn(min = 50.dp),
        enabled = enabled && !isLoading,
        shape = PaxDesignSystem.Shapes.Button,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = contentColor,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text.uppercase(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Versão secundária (Outlined) do botão.
 */
@Composable
fun PaxOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = PaxDesignSystem.Colors.BrandGreen
) {
    val haptic = LocalHapticFeedback.current

    OutlinedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .bounceClick()
            .heightIn(min = 50.dp),
        enabled = enabled,
        shape = PaxDesignSystem.Shapes.Button,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
