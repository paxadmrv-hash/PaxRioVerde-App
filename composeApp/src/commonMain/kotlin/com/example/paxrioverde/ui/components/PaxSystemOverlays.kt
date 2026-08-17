package com.example.paxrioverde.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

/**
 * PaxSystemOverlays: Centraliza as camadas de sobreposição globais do sistema.
 * Senior Strategy: Desacopla a lógica visual de status críticos da navegação principal.
 */
@Composable
fun PaxSystemOverlays(
    isOffline: Boolean,
    showBackOnline: Boolean,
    showRootWarning: Boolean,
    onDismissRootWarning: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Alerta de Root (AlertDialog)
        if (showRootWarning) {
            AlertDialog(
                onDismissRequest = onDismissRootWarning,
                icon = { Icon(Icons.Default.Security, null, tint = Color(0xFFD32F2F)) },
                title = { Text("Aparelho Comprometido", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Detectamos que seu celular possui modificações no sistema (Root/Jailbreak). " +
                                "Para sua segurança, algumas funções financeiras podem estar instáveis ou desativadas.",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(onClick = onDismissRootWarning) {
                        Text("ENTENDI", color = Color(0xFF386641), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // 2. Banner Online (Feedback positivo quando a conexão volta)
        AnimatedVisibility(
            visible = showBackOnline && !isOffline,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(999999f)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF386641), // Verde da marca
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Conexão Restabelecida",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Banner Offline "BRUTAL"
        if (isOffline) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(999999f)
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = Color(0xFFD32F2F),
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(vertical = 16.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "DISPOSITIVO OFFLINE",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Conecte-se à internet para usar o aplicativo",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
