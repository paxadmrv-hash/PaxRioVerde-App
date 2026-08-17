package com.example.paxrioverde.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * PaxDesignSystem: O "Cérebro" Visual do Aplicativo.
 * Centraliza todos os tokens de design (cores, gradientes, formas) para garantir
 * consistência e facilitar mudanças globais de marca.
 */
object PaxDesignSystem {
    
    // CORES DE MARCA
    object Colors {
        val BrandGreen = Color(0xFF386641)
        val BrandGreenDark = Color(0xFF1B5E20)
        val BrandLightGreen = Color(0xFF6FAD2B)
        
        // NEUTROS
        val Background = Color(0xFFF0F2F5)
        val Surface = Color(0xFFFFFFFF)
        val TextPrimary = Color(0xFF101820)
        val TextSecondary = Color(0xFF606C76)
        val TextDark = Color(0xFF1F2937)
        val White = Color.White
        
        // STATUS
        val Error = Color(0xFFD32F2F)
        val Success = Color(0xFF386641)
        val Warning = Color(0xFFFFA000)
    }

    // GRADIENTES PADRONIZADOS
    object Gradients {
        val Primary = Brush.verticalGradient(
            colors = listOf(Colors.BrandGreen, Colors.BrandGreenDark)
        )
    }

    // FORMAS (SHAPES)
    object Shapes {
        val Small = RoundedCornerShape(8.dp)
        val Medium = RoundedCornerShape(16.dp)
        val Large = RoundedCornerShape(24.dp)
        val ExtraLarge = RoundedCornerShape(32.dp)
        val Button = RoundedCornerShape(12.dp)
        val Card = RoundedCornerShape(24.dp)
    }
}
