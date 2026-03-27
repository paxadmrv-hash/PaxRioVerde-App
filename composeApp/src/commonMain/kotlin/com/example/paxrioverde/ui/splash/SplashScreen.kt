package com.example.paxrioverde.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.paxrioverde.util.SessionManager
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.logo_pax_30_anos

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit, onNavigateToDashboard: () -> Unit) {
    val scale = remember { Animatable(0.9f) }
    val alpha = remember { Animatable(0f) }
    val sessionManager = remember { SessionManager() }

    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 300))
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        delay(100)
        scale.animateTo(targetValue = 1.0f, animationSpec = tween(durationMillis = 300))
        delay(1500L)

        if (sessionManager.isBiometricEnabled()) {
            // No Multiplatform, a lógica de biometria real ficaria em expect/actual.
            // Por enquanto, seguimos para a Dashboard se estiver habilitado (simulado).
            onNavigateToDashboard()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(Res.drawable.logo_pax_30_anos),
            contentDescription = "Logo Pax Rio Verde",
            modifier = Modifier
                .size(280.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}
