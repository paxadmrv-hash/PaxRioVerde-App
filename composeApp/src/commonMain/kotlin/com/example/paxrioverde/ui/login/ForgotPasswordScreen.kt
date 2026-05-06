package com.example.paxrioverde.ui.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.api.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.bg_login_family

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var step by remember { mutableStateOf(1) } // 1: Request Token, 2: Reset Password
    
    var cpfOrEmail by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val brandGreen = Color(0xFF386641)

    fun handleRequestToken() {
        if (cpfOrEmail.isEmpty()) {
            errorMessage = "Informe seu CPF ou E-mail"
            return
        }
        
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val response = ApiService.esquecerSenha(cpfOrEmail)
                if (response.success) {
                    successMessage = response.message ?: "Código enviado com sucesso!"
                    delay(1500)
                    successMessage = null
                    step = 2
                } else {
                    errorMessage = response.message ?: "Erro ao solicitar recuperação"
                }
            } catch (e: Exception) {
                errorMessage = "Erro de conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun handleResetPassword() {
        if (token.length != 6) {
            errorMessage = "O código deve ter 6 dígitos"
            return
        }
        if (newPassword.length < 6) {
            errorMessage = "A senha deve ter pelo menos 6 caracteres"
            return
        }
        if (newPassword != confirmPassword) {
            errorMessage = "As senhas não coincidem"
            return
        }

        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val response = ApiService.redefinirSenha(
                    cpf = cpfOrEmail, 
                    token = token,
                    senha = newPassword
                )
                if (response.success) {
                    successMessage = "Senha redefinida com sucesso!"
                    delay(2000)
                    onBack()
                } else {
                    errorMessage = response.message ?: "Erro ao redefinir senha"
                }
            } catch (e: Exception) {
                errorMessage = "Erro de conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { focusManager.clearFocus() }
    ) {
        Image(
            painter = painterResource(Res.drawable.bg_login_family),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .clip(CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Voltar", 
                        tint = brandGreen
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 32.dp, vertical = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Indicador de progresso (Steps)
                    Row(
                        modifier = Modifier.padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StepIndicator(isActive = step >= 1, color = brandGreen)
                        Box(modifier = Modifier.width(40.dp).height(2.dp).background(if (step >= 2) brandGreen else Color.LightGray))
                        StepIndicator(isActive = step >= 2, color = brandGreen)
                    }

                    AnimatedContent(
                        targetState = step,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width } + fadeOut()
                                )
                            } else {
                                (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> width } + fadeOut()
                                )
                            }
                        }
                    ) { currentStep ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (currentStep == 1) "Esqueci minha senha" else "Nova Senha",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = brandGreen,
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = if (currentStep == 1) 
                                    "Informe seu CPF ou E-mail para receber o código de validação." 
                                    else "Enviamos um código para seu e-mail. Digite-o abaixo junto com sua nova senha.",
                                fontSize = 15.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 12.dp, bottom = 32.dp),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            if (currentStep == 1) {
                                LoginTextField(
                                    value = cpfOrEmail,
                                    onValueChange = { cpfOrEmail = it },
                                    label = "CPF ou E-mail",
                                    icon = Icons.Outlined.Person,
                                    keyboardType = KeyboardType.Text
                                )
                            } else {
                                LoginTextField(
                                    value = token,
                                    onValueChange = { if (it.length <= 6) token = it.filter { c -> c.isDigit() } },
                                    label = "Código de 6 dígitos",
                                    icon = Icons.Default.Key,
                                    keyboardType = KeyboardType.Number
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                LoginTextField(
                                    value = newPassword,
                                    onValueChange = { newPassword = it },
                                    label = "Nova Senha",
                                    icon = Icons.Outlined.Lock,
                                    keyboardType = KeyboardType.Password,
                                    isPassword = true,
                                    isPasswordVisible = passwordVisible,
                                    onVisibilityChange = { passwordVisible = !passwordVisible }
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                LoginTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    label = "Confirmar Nova Senha",
                                    icon = Icons.Default.LockReset,
                                    keyboardType = KeyboardType.Password,
                                    isPassword = true,
                                    isPasswordVisible = confirmPasswordVisible,
                                    onVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible }
                                )
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        ) {
                            Text(
                                errorMessage!!, 
                                color = Color.Red, 
                                fontSize = 13.sp, 
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    if (successMessage != null) {
                        Surface(
                            color = brandGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        ) {
                            Text(
                                successMessage!!, 
                                color = brandGreen, 
                                fontSize = 13.sp, 
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { if (step == 1) handleRequestToken() else handleResetPassword() },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                        enabled = !isLoading,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                if (step == 1) "ENVIAR CÓDIGO" else "REDEFINIR SENHA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }

                    if (step == 2) {
                        TextButton(
                            onClick = { step = 1 },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Voltar para a página anterior", color = brandGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun StepIndicator(isActive: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(if (isActive) color else Color.LightGray, CircleShape)
    )
}
