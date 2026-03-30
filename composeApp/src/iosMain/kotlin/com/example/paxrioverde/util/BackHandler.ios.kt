package com.example.paxrioverde.util

import androidx.compose.runtime.Composable

@Composable
actual fun CommonBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No iOS, o comportamento de "voltar" é gerenciado nativamente ou pelo UINavigationController.
    // Esta implementação permanece vazia para evitar conflitos com os gestos padrão do iOS.
}
