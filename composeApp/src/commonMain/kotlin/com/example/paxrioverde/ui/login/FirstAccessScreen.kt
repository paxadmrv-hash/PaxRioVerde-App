package com.example.paxrioverde.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.viewmodel.koinViewModel
import com.example.paxrioverde.api.ApiService
import com.example.paxrioverde.util.urlEncode
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.bg_login_family

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstAccessScreen(
    onBack: () -> Unit,
    viewModel: FirstAccessViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState
    val uriHandler = LocalUriHandler.current
    val focusManager = LocalFocusManager.current
    val fontScale = LocalDensity.current.fontScale

    val isFormValid = uiState.cpf.length == 11 && 
                     uiState.phone.length >= 10 && 
                     uiState.email.contains("@") && 
                     uiState.password.length >= 6 && 
                     uiState.password == uiState.confirmPassword

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
                .imePadding()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(16.dp).background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF386641))
            }

            Card(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(if (fontScale > 1.3) 16.dp else 32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Primeiro Acesso", 
                        fontSize = if (fontScale > 1.3) 20.sp else 24.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF386641),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Preencha os dados para criar sua conta", 
                        fontSize = 14.sp, 
                        color = Color.Gray, 
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(if (fontScale > 1.3) 16.dp else 32.dp))

                    LoginTextField(
                        value = uiState.cpf,
                        onValueChange = { viewModel.onCpfChange(it) },
                        label = "Seu CPF (TITULAR DO PLANO OU DEPENDENTE COM ACESSO)",
                        icon = Icons.Default.Badge,
                        keyboardType = KeyboardType.Number,
                        visualTransformation = CpfVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = uiState.phone,
                        onValueChange = { viewModel.onPhoneChange(it) },
                        label = "WhatsApp com DDD",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = "E-mail",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Crie uma Senha",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        isPasswordVisible = uiState.passwordVisible,
                        onVisibilityChange = { viewModel.togglePasswordVisibility() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = uiState.confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        label = "Confirme a Senha",
                        icon = Icons.Default.LockReset,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        isPasswordVisible = uiState.confirmPasswordVisible,
                        onVisibilityChange = { viewModel.toggleConfirmPasswordVisibility() }
                    )

                    if (uiState.errorMessage != null) {
                        Text(uiState.errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    if (uiState.successMessage != null) {
                        Text(uiState.successMessage, color = Color(0xFF386641), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(if (fontScale > 1.3) 16.dp else 32.dp))

                    Button(
                        onClick = { viewModel.handleRegister(onBack) },
                        modifier = Modifier.fillMaxWidth().height(if (fontScale > 1.3) 48.dp else 56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF386641)),
                        enabled = isFormValid && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("SOLICITAR ACESSO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TextButton(onClick = {
                        val whatsappAjuda = "556492331101"
                        val msg = "Olá, preciso de ajuda com o primeiro acesso no aplicativo."
                        uriHandler.openUri("https://wa.me/$whatsappAjuda?text=${urlEncode(msg)}")
                    }) {
                        Text("Precisa de ajuda? Fale conosco", color = Color(0xFF386641))
                    }
                }
            }
        }
    }
}
