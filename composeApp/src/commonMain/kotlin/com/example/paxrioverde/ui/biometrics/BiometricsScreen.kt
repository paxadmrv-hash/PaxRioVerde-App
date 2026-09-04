package com.example.paxrioverde.ui.biometrics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
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
import com.example.paxrioverde.api.LoginResponse
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.repository.AuthRepository
import com.example.paxrioverde.ui.components.PaxButton
import com.example.paxrioverde.ui.login.LoginTextField
import com.example.paxrioverde.ui.notifications.NotificationType
import com.example.paxrioverde.util.BiometricAuthenticator
import com.example.paxrioverde.util.NotificationManager
import com.example.paxrioverde.util.SessionManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

val BrandGreen = Color(0xFF386641)
val SoftGrayBg = Color(0xFFF2F6F3) 
val SurfaceWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1F2937)

@Composable
fun BiometricsScreen(
    onBack: () -> Unit,
    userData: LoginResponse? = null
) {
    val sessionManager = remember { SessionManager() }
    val notificationManager: NotificationManager = koinInject()
    val authRepository: AuthRepository = koinInject()
    val biometricAuthenticator: BiometricAuthenticator = koinInject()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var isBiometricEnabled by remember { mutableStateOf(sessionManager.isBiometricEnabled()) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var pendingCheckedState by remember { mutableStateOf(false) }
    
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isValidating by remember { mutableStateOf(false) }
    var dialogError by remember { mutableStateOf<String?>(null) }

    fun handleBiometricToggle(checked: Boolean) {
        if (checked) {
            biometricAuthenticator.authenticate(
                title = "Ativar Biometria",
                subtitle = "Confirme sua identidade para habilitar o acesso rápido",
                onSuccess = {
                    val hasCredentials = authRepository.hasSavedCredentials()
                    if (!hasCredentials) {
                        pendingCheckedState = true
                        showPasswordDialog = true
                    } else {
                        isBiometricEnabled = true
                        authRepository.updateBiometricStatus(true)
                        notificationManager.addNotification(
                            title = "Segurança Atualizada",
                            message = "A entrada por biometria foi ativada com sucesso.",
                            type = NotificationType.SYSTEM
                        )
                    }
                },
                onError = { error ->
                    // Senior Fix: Garante que o estado visual reflita a falha
                    isBiometricEnabled = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Erro na biometria: $error")
                    }
                }
            )
        } else {
            isBiometricEnabled = false
            authRepository.updateBiometricStatus(false)
            notificationManager.addNotification(
                title = "Segurança Atualizada",
                message = "A entrada por biometria foi desativada.",
                type = NotificationType.SYSTEM
            )
        }
    }

    Scaffold(
        containerColor = SoftGrayBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            onCheckedChange = { handleBiometricToggle(it) },
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

    if (showPasswordDialog && userData != null) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false
                password = ""
                dialogError = null
            },
            title = { Text("Vincular Biometria", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Para sua segurança, confirme sua senha para habilitar a biometria neste dispositivo.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LoginTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            dialogError = null
                        },
                        label = "Senha Atual",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
                    )
                    if (dialogError != null) {
                        Text(
                            text = dialogError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                PaxButton(
                    text = "CONFIRMAR",
                    isLoading = isValidating,
                    onClick = {
                        if (password.isEmpty()) {
                            dialogError = "Digite sua senha"
                            return@PaxButton
                        }
                        
                        isValidating = true
                        scope.launch {
                            val rawCpf = userData.cpf ?: sessionManager.getSavedCpf()
                            val cleanCpf = rawCpf.filter { it.isDigit() }
                            
                            if (cleanCpf.isEmpty()) {
                                isValidating = false
                                dialogError = "CPF não encontrado. Tente logar novamente."
                                return@launch
                            }

                            val result = authRepository.login(
                                cpf = cleanCpf,
                                pass = password,
                                rememberMe = true // Força o salvamento das credenciais para a biometria
                            )
                            
                            isValidating = false
                            if (result is NetworkResult.Success && result.data.any { it.success }) {
                                isBiometricEnabled = true
                                authRepository.updateBiometricStatus(true)
                                showPasswordDialog = false
                                password = ""
                                
                                notificationManager.addNotification(
                                    title = "Biometria Ativada",
                                    message = "Sua digital foi vinculada com sucesso.",
                                    type = NotificationType.SYSTEM
                                )
                            } else {
                                dialogError = "Senha incorreta. Tente novamente."
                            }
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = SurfaceWhite
        )
    }
}
