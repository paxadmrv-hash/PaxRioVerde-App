package com.example.paxrioverde.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.paxrioverde.ui.theme.PaxDesignSystem

/**
 * Indicador de página animado (Worm/Expanding animation).
 * Senior UX: Reage dinamicamente ao deslizar do usuário.
 */
@Composable
fun PaxPageIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = PaxDesignSystem.Colors.BrandLightGreen,
    inactiveColor: Color = Color.DarkGray
) {
    Row(
        modifier = modifier.height(12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val isSelected = pagerState.currentPage == iteration
            
            // Animação de largura: o ponto selecionado fica mais comprido (estilo pílula)
            val width by animateDpAsState(
                targetValue = if (isSelected) 20.dp else 8.dp,
                label = "width"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(width = width, height = 8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) activeColor else inactiveColor)
            )
        }
    }
}
