package com.example.paxrioverde.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.paxrioverde.ui.theme.InstitutionalGreen
import com.example.paxrioverde.util.SessionManager
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.logo_pax_30_anos

@Composable
fun SplashScreen(isLoading: Boolean = false, onFinished: () -> Unit) {
    val scale = remember { Animatable(0.9f) }
    val alpha = remember { Animatable(0f) }
    val sessionManager = remember { SessionManager() }

    LaunchedEffect(Unit) {
        // Senior Performance: Inicia o carregamento da criptografia pesada em background
        // enquanto a animação do Splash roda.
        sessionManager.warmUp()
    }

    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 300))
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        delay(100)
        scale.animateTo(targetValue = 1.0f, animationSpec = tween(durationMillis = 300))
        delay(1500L)

        // Senior Note: O Splash agora apenas avisa que terminou.
        // A lógica de "para onde ir" fica centralizada no App.kt para evitar bypass de biometria.
        onFinished()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.logo_pax_30_anos),
                contentDescription = "Logo Pax Rio Verde",
                modifier = Modifier
                    .size(280.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    color = InstitutionalGreen,
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
