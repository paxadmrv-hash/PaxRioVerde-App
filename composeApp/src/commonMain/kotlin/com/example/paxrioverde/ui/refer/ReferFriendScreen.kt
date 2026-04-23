package com.example.paxrioverde.ui.refer

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.util.urlEncode

// CORES PADRÃO DO APP (Pax Rio Verde / Grupo Universo)
val BrandGreenRefer = Color(0xFF386641)
val BrandLimeRefer = Color(0xFF386641)
val GoldAccent = Color(0xFFEAB365)
val SurfaceGray = Color(0xFFF8F9FA)

@Composable
fun ReferFriendScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    var friendName by remember { mutableStateOf("") }
    var friendPhone by remember { mutableStateOf("") }

    val isFormValid = friendName.length > 3 && friendPhone.length >= 10

    fun handleSendIndication() {
        val cleanPhone = friendPhone.replace(Regex("[^0-9]"), "")
        // Como é uma indicação, o usuário envia para a Pax sobre o amigo, 
        // ou envia para o amigo um convite. 
        // Geralmente, "Indicou, Ganhou" envia para o comercial da Pax.
        val whatsappComercial = "556481460004"
        val message = "Olá! Gostaria de indicar um amigo para a Pax Rio Verde:\nNome: $friendName\nWhatsApp: $friendPhone"
        uriHandler.openUri("https://wa.me/$whatsappComercial?text=${urlEncode(message)}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        // CABEÇALHO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BrandGreenRefer, BrandLimeRefer)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Indicou, Fechou,\nGanhou!!!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                lineHeight = 38.sp,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Não deixe seu amigo de fora e desfrute dos nossos benefícios juntos!",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // BENTO GRID - CARDS DE RECOMPENSA
            Text(
                text = "Veja o que você pode ganhar",
                fontWeight = FontWeight.Bold,
                color = BrandGreenRefer,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RewardCard(
                    modifier = Modifier.weight(1f),
                    title = "Mensalidade",
                    desc = "Para associados: Ganhe 1 mês grátis",
                    icon = Icons.AutoMirrored.Filled.FactCheck,
                    color = GoldAccent
                )
                RewardCard(
                    modifier = Modifier.weight(1f),
                    title = "Renda Extra",
                    desc = "Não associados: Receba R$ 50,00 ao indicar",
                    icon = Icons.Default.Payments,
                    color = BrandLimeRefer
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // FORMULÁRIO DE INDICAÇÃO
            Surface(
                color = SurfaceGray,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Quem você vai indicar?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    OutlinedTextField(
                        value = friendName,
                        onValueChange = { friendName = it },
                        label = { Text("Nome do Amigo") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = BrandGreenRefer) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGreenRefer,
                            unfocusedBorderColor = Color.Transparent,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = friendPhone,
                        onValueChange = { friendPhone = it },
                        label = { Text("WhatsApp") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Chat, null, tint = BrandGreenRefer) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGreenRefer,
                            unfocusedBorderColor = Color.Transparent,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { handleSendIndication() },
                        enabled = isFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreenRefer,
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        AnimatedVisibility(visible = isFormValid) {
                            Icon(Icons.AutoMirrored.Filled.Send, null)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFormValid) "Enviar Indicação Agora" else "Preencha os dados",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(contentDescription = "Condição para pagamento",
                    imageVector = Icons.Default.Info,
                    tint = Color(0xFF0288D1),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SÓ GANHA A MENSALIDADE OU EXTRA MEDIANTE CONFIRMAÇÃO DO PLANO E PAGAMENTO DA ADESÃO DO INDICADO.",
                    fontSize = 11.sp,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
fun RewardCard(
    modifier: Modifier,
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier.height(140.dp),
        color = SurfaceGray,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, fontSize = 12.sp, color = Color.Gray, lineHeight = 14.sp)
        }
    }
}
