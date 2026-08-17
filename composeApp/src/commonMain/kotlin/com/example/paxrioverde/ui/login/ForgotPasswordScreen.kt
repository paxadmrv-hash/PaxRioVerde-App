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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.viewmodel.koinViewModel
import com.example.paxrioverde.api.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.bg_login_family

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState
    val focusManager = LocalFocusManager.current
    val fontScale = LocalDensity.current.fontScale

    val brandGreen = Color(0xFF386641)

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
                        .padding(horizontal = 32.dp, vertical = if (fontScale > 1.3) 16.dp else 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Indicador de progresso (Steps)
                    Row(
                        modifier = Modifier.padding(bottom = if (fontScale > 1.3) 12.dp else 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StepIndicator(isActive = uiState.step >= 1, color = brandGreen)
                        Box(modifier = Modifier.width(40.dp).height(2.dp).background(if (uiState.step >= 2) brandGreen else Color.LightGray))
                        StepIndicator(isActive = uiState.step >= 2, color = brandGreen)
                    }

                    AnimatedContent(
                        targetState = uiState.step,
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
                                fontSize = if (fontScale > 1.3) 22.sp else 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = brandGreen,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Text(
                                text = if (currentStep == 1) 
                                    "Informe seu CPF ou E-mail para receber o código de validação." 
                                    else "Enviamos um código para seu e-mail. Digite-o abaixo junto com sua nova senha.",
                                fontSize = 15.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp, bottom = if (fontScale > 1.3) 16.dp else 32.dp),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            if (currentStep == 1) {
                                LoginTextField(
                                    value = uiState.cpfOrEmail,
                                    onValueChange = { viewModel.onCpfOrEmailChange(it) },
                                    label = "CPF ou E-mail",
                                    icon = Icons.Outlined.Person,
                                    keyboardType = KeyboardType.Text
                                )
                            } else {
                                LoginTextField(
                                    value = uiState.token,
                                    onValueChange = { viewModel.onTokenChange(it) },
                                    label = "Código de 6 dígitos",
                                    icon = Icons.Default.Key,
                                    keyboardType = KeyboardType.Number
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                LoginTextField(
                                    value = uiState.newPassword,
                                    onValueChange = { viewModel.onNewPasswordChange(it) },
                                    label = "Nova Senha",
                                    icon = Icons.Outlined.Lock,
                                    keyboardType = KeyboardType.Password,
                                    isPassword = true,
                                    isPasswordVisible = uiState.passwordVisible,
                                    onVisibilityChange = { viewModel.togglePasswordVisibility() }
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                LoginTextField(
                                    value = uiState.confirmPassword,
                                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                                    label = "Confirmar Nova Senha",
                                    icon = Icons.Default.LockReset,
                                    keyboardType = KeyboardType.Password,
                                    isPassword = true,
                                    isPasswordVisible = uiState.confirmPasswordVisible,
                                    onVisibilityChange = { viewModel.toggleConfirmPasswordVisibility() }
                                )
                            }
                        }
                    }

                    if (uiState.errorMessage != null) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        ) {
                            Text(
                                uiState.errorMessage, 
                                color = Color.Red, 
                                fontSize = 13.sp, 
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    if (uiState.successMessage != null) {
                        Surface(
                            color = brandGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        ) {
                            Text(
                                uiState.successMessage, 
                                color = brandGreen, 
                                fontSize = 13.sp, 
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(if (fontScale > 1.3) 20.dp else 40.dp))

                    Button(
                        onClick = { if (uiState.step == 1) viewModel.handleRequestToken() else viewModel.handleResetPassword(onBack) },
                        modifier = Modifier.fillMaxWidth().height(if (fontScale > 1.3) 50.dp else 60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                        enabled = !uiState.isLoading,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                if (uiState.step == 1) "ENVIAR CÓDIGO" else "REDEFINIR SENHA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }

                    if (uiState.step == 2) {
                        TextButton(
                            onClick = { viewModel.setStep(1) },
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
