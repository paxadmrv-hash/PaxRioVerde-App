package com.example.paxrioverde.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.ui.theme.PaxDesignSystem

/**
 * Componente de erro padronizado para todo o aplicativo.
 * Senior UX: Focado em reduzir a frustração do usuário com mensagens amigáveis.
 */
@Composable
fun PaxErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícone amigável em vez de técnico
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = PaxDesignSystem.Colors.BrandGreen.copy(alpha = 0.4f),
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Ops! Algo deu errado",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PaxDesignSystem.Colors.TextDark
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = translateErrorMessage(message),
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PaxButton(
                text = "Tentar Novamente",
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}

/**
 * Converte erros técnicos de API em mensagens humanas.
 */
private fun translateErrorMessage(error: String): String {
    return when {
        error.contains("Unable to resolve host", ignoreCase = true) -> 
            "Não conseguimos conectar ao servidor. Verifique sua internet."
        error.contains("timeout", ignoreCase = true) -> 
            "O servidor demorou muito para responder. Tente novamente."
        error.contains("500") -> 
            "Nosso sistema está passando por uma manutenção rápida. Voltamos logo!"
        else -> error
    }
}
