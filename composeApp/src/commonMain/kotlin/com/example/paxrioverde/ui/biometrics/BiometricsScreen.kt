package com.example.paxrioverde.ui.biometrics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.ui.notifications.NotificationCenter
import com.example.paxrioverde.ui.notifications.NotificationType
import com.example.paxrioverde.util.SessionManager

val BrandGreen = Color(0xFF386641)
val SoftGrayBg = Color(0xFFF2F6F3) // Corrigido para manter padrão
val SurfaceWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1F2937)

@Composable
fun BiometricsScreen(onBack: () -> Unit) {
    val sessionManager = remember { SessionManager() }
    var isBiometricEnabled by remember { mutableStateOf(sessionManager.isBiometricEnabled()) }

    Scaffold(
        containerColor = SoftGrayBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.1f))
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextDark
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Biometria",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Entrar com Digital",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ative para fazer login mais rápido usando sua impressão digital cadastrada no celular.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBiometricEnabled) "Ativado" else "Desativado",
                            fontWeight = FontWeight.Bold,
                            color = if (isBiometricEnabled) BrandGreen else Color.Gray
                        )

                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { checked ->
                                isBiometricEnabled = checked
                                sessionManager.setBiometricEnabled(checked)
                                
                                // Adiciona notificação de alteração de segurança
                                val status = if (checked) "ativada" else "desativada"
                                NotificationCenter.addNotification(
                                    title = "Segurança Atualizada",
                                    message = "A entrada por biometria foi $status com sucesso.",
                                    type = NotificationType.SYSTEM
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SurfaceWhite,
                                checkedTrackColor = BrandGreen
                            )
                        )
                    }
                }
            }
        }
    }
}
