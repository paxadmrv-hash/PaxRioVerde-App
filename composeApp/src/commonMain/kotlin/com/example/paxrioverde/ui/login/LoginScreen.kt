package com.example.paxrioverde.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import com.example.paxrioverde.api.LoginResponse
import com.example.paxrioverde.ui.components.PaxButton
import com.example.paxrioverde.ui.components.PaxOutlinedButton
import com.example.paxrioverde.ui.components.bounceClick
import com.example.paxrioverde.ui.theme.PaxDesignSystem
import com.example.paxrioverde.util.BiometricAuthenticator
import com.example.paxrioverde.util.SessionManager
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.bg_login_family

class CpfVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 11) text.text.substring(0..10) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 2 || i == 5) out += "."
            if (i == 8) out += "-"
        }

        val cpfOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 8) return offset + 2
                if (offset <= 11) return offset + 3
                return 14
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 11) return offset - 2
                if (offset <= 14) return offset - 3
                return 11
            }
        }
        return TransformedText(AnnotatedString(out), cpfOffsetTranslator)
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: (LoginResponse) -> Unit,
    onProfileSelection: (List<LoginResponse>) -> Unit,
    onFirstAccessClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val sessionManager = koinInject<SessionManager>()
    val biometricAuthenticator = koinInject<BiometricAuthenticator>()
    val authRepository = koinInject<com.example.paxrioverde.domain.repository.AuthRepository>()

    val density = LocalDensity.current
    val fontScale = density.fontScale
    val scrollState = rememberScrollState()
    
    var isBiometricAvailable by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isBiometricAvailable = sessionManager.isBiometricEnabled() && 
                             biometricAuthenticator.canAuthenticate() &&
                             authRepository.hasSavedCredentials()
    }

    LaunchedEffect(uiState.showProfileSelection) {
        if (uiState.showProfileSelection) {
            onProfileSelection(uiState.profiles)
            viewModel.resetProfileSelection()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { focusManager.clearFocus() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.bg_login_family),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Card(
            shape = PaxDesignSystem.Shapes.Card,
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .imePadding() // Garante que o teclado não cubra os campos
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = if (fontScale > 1.3) 16.dp else 32.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Acesse sua conta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PaxDesignSystem.Colors.TextDark,
                    letterSpacing = (-0.5).sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(if (fontScale > 1.3) 12.dp else 24.dp))

                LoginTextField(
                    value = uiState.cpf,
                    onValueChange = { viewModel.onCpfChange(it) },
                    label = "CPF",
                    icon = Icons.Outlined.Person,
                    keyboardType = KeyboardType.Number,
                    visualTransformation = CpfVisualTransformation()
                )

                Spacer(modifier = Modifier.height(if (fontScale > 1.3) 8.dp else 16.dp))

                LoginTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = "Senha",
                    icon = Icons.Outlined.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    isPasswordVisible = uiState.passwordVisible,
                    onVisibilityChange = { viewModel.togglePasswordVisibility() }
                )

                if (fontScale > 1.2) {
                    // Layout vertical para fontes grandes
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.onRememberMeChange(!uiState.rememberMe) }
                        ) {
                            Checkbox(
                                checked = uiState.rememberMe,
                                onCheckedChange = { viewModel.onRememberMeChange(it) },
                                colors = CheckboxDefaults.colors(checkedColor = PaxDesignSystem.Colors.BrandGreen)
                            )
                            Text(
                                text = "Lembrar login",
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "Esqueci a senha",
                            fontSize = 14.sp,
                            color = PaxDesignSystem.Colors.BrandGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(start = 12.dp, top = 4.dp)
                                .clickable { onForgotPasswordClick() }
                        )
                    }
                } else {
                    // Layout horizontal padrão
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = uiState.rememberMe,
                                onCheckedChange = { viewModel.onRememberMeChange(it) },
                                colors = CheckboxDefaults.colors(checkedColor = PaxDesignSystem.Colors.BrandGreen)
                            )
                            Text(
                                text = "Lembrar login",
                                fontSize = 14.sp,
                                modifier = Modifier.clickable { viewModel.onRememberMeChange(!uiState.rememberMe) }
                            )
                        }
                        Text(
                            text = "Esqueci a senha",
                            fontSize = 14.sp,
                            color = PaxDesignSystem.Colors.BrandGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onForgotPasswordClick() }
                        )
                    }
                }

                uiState.errorMessage?.let { message ->
                    LoginErrorCard(
                        message = message,
                        suggestFirstAccess = uiState.suggestFirstAccess,
                        onFirstAccessClick = onFirstAccessClick
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PaxButton(
                        text = "ENTRAR",
                        onClick = { viewModel.login(onLoginSuccess) },
                        isLoading = uiState.isLoading,
                        modifier = Modifier.weight(1f)
                    )

                    if (isBiometricAvailable) {
                        IconButton(
                            onClick = {
                                biometricAuthenticator.authenticate(
                                    title = "Login com Biometria",
                                    subtitle = "Use sua digital para acessar",
                                    onSuccess = { viewModel.performBiometricLogin(onLoginSuccess) },
                                    onError = { error ->
                                        viewModel.onBiometricError(error)
                                    }
                                )
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .background(PaxDesignSystem.Colors.BrandGreen.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometria",
                                tint = PaxDesignSystem.Colors.BrandGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                PaxOutlinedButton(
                    text = "PRIMEIRO ACESSO",
                    onClick = onFirstAccessClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                val uriHandler = LocalUriHandler.current
                Text(
                    text = buildAnnotatedString {
                        append("Precisa de ajuda? ")
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )) {
                            append("Fale Conosco")
                        }
                    },
                    fontSize = 14.sp,
                    color = PaxDesignSystem.Colors.BrandGreen,
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://wa.me/556492331101")
                    }
                )
            }
        }

        Text(
            text = "© 2026 Pax Rio Verde | Desenvolvido pelo T.I. Interno",
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
        )
    }
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = PaxDesignSystem.Colors.BrandGreen) },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onVisibilityChange) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !isPasswordVisible) {
            PasswordVisualTransformation()
        } else {
            visualTransformation
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false
        ),
        singleLine = true,
        shape = PaxDesignSystem.Shapes.Button,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PaxDesignSystem.Colors.BrandGreen,
            focusedLabelColor = PaxDesignSystem.Colors.BrandGreen,
            unfocusedBorderColor = PaxDesignSystem.Colors.TextSecondary.copy(alpha = 0.2f),
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        )
    )
}

@Composable
fun LoginErrorCard(
    message: String,
    suggestFirstAccess: Boolean,
    onFirstAccessClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        color = if (suggestFirstAccess) Color(0xFFFFF3E0) else Color(0xFFFFEBEE),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (suggestFirstAccess) Color(0xFFFFB74D) else Color(0xFFEF9A9A))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (suggestFirstAccess) Icons.Default.Lightbulb else Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = if (suggestFirstAccess) Color(0xFFE65100) else Color(0xFFC62828),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    color = if (suggestFirstAccess) Color(0xFFE65100) else Color(0xFFC62828),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            if (suggestFirstAccess) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onFirstAccessClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        "FAZER PRIMEIRO ACESSO AGORA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
