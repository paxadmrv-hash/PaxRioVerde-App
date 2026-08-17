package com.example.paxrioverde.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Representa os estados possíveis de um plano baseados em regras de negócio e carência.
 */
enum class PlanStatus(val label: String, val color: Color) {
    ACTIVE("Ativo", Color(0xFF386641)),
    ATTENTION("Atenção", Color(0xFFFBC02D)),
    GRACE_PERIOD("Carência", Color(0xFFD32F2F))
}
