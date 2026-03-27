package com.example.paxrioverde.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.api.ApiService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.bg_login_family

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstAccessScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    var cpf by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = cpf.length == 11 && phone.length >= 10 && email.contains("@") && password.length >= 6 && password == confirmPassword

    fun handleRegister() {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val response = ApiService.registrar(
                    cpf = cpf,
                    celular = phone,
                    email = email,
                    senha = password
                )
                if (response.success) {
                    successMessage = "Solicitação enviada com sucesso!"
                    kotlinx.coroutines.delay(2000)
                    onBack()
                } else {
                    errorMessage = response.message ?: "Erro ao solicitar acesso"
                }
            } catch (e: Exception) {
                errorMessage = "Erro de conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        .padding(32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Primeiro Acesso", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF386641))
                    Text("Preencha os dados para criar sua conta", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    LoginTextField(
                        value = cpf,
                        onValueChange = { cpf = it.filter { c -> c.isDigit() }.take(11) },
                        label = "Seu CPF (apenas números)",
                        icon = Icons.Default.Badge,
                        keyboardType = KeyboardType.Number,
                        visualTransformation = CpfVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = phone,
                        onValueChange = { phone = it.filter { c -> c.isDigit() } },
                        label = "WhatsApp com DDD",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "E-mail",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Crie uma Senha",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Text,
                        isPassword = true,
                        isPasswordVisible = passwordVisible,
                        onVisibilityChange = { passwordVisible = !passwordVisible }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirme a Senha",
                        icon = Icons.Default.LockReset,
                        keyboardType = KeyboardType.Text,
                        isPassword = true,
                        isPasswordVisible = confirmPasswordVisible,
                        onVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible }
                    )

                    if (errorMessage != null) {
                        Text(errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    if (successMessage != null) {
                        Text(successMessage!!, color = Color(0xFF386641), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { handleRegister() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF386641)),
                        enabled = isFormValid && !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("SOLICITAR ACESSO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TextButton(onClick = {
                        val whatsappAjuda = "5564992784186"
                        val msg = "Olá, preciso de ajuda com o primeiro acesso no aplicativo."
                        uriHandler.openUri("https://wa.me/$whatsappAjuda?text=${msg.replace(" ", "%20")}")
                    }) {
                        Text("Precisa de ajuda? Fale conosco", color = Color(0xFF386641))
                    }
                }
            }
        }
    }
}
