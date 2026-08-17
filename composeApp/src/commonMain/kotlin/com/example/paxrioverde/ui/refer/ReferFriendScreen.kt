package com.example.paxrioverde.ui.refer

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
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.ui.components.PaxButton
import com.example.paxrioverde.ui.components.PaxTextField
import com.example.paxrioverde.ui.theme.PaxDesignSystem
import com.example.paxrioverde.util.urlEncode

// CORES LOCAIS REFINADAS
val ReferGold = Color(0xFFF59E0B)
val ReferBackground = Color(0xFFF9FAFB)

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
            .background(PaxDesignSystem.Colors.White)
            .navigationBarsPadding()
            .imePadding()
    ) {
        // CABEÇALHO (HERO)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(PaxDesignSystem.Gradients.Primary)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Indicou, Fechou,\nGanhou!!!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                lineHeight = 40.sp,
                letterSpacing = (-1.5).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Compartilhe os benefícios da Pax com seus amigos e seja recompensado.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.85f),
                lineHeight = 22.sp,
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
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = PaxDesignSystem.Colors.TextDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RewardCard(
                    modifier = Modifier.weight(1f),
                    title = "Mensalidade",
                    desc = "Ganhos para associados",
                    benefit = "1 MÊS GRÁTIS",
                    icon = Icons.AutoMirrored.Filled.FactCheck,
                    color = ReferGold
                )
                RewardCard(
                    modifier = Modifier.weight(1f),
                    title = "Renda Extra",
                    desc = "Para todos usuários",
                    benefit = "R$ 50,00",
                    icon = Icons.Default.Payments,
                    color = PaxDesignSystem.Colors.BrandLightGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // FORMULÁRIO DE INDICAÇÃO
            Surface(
                color = ReferBackground,
                shape = PaxDesignSystem.Shapes.Card,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Quem você vai indicar?",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = PaxDesignSystem.Colors.TextDark,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    PaxTextField(
                        value = friendName,
                        onValueChange = { friendName = it },
                        label = "Nome do Amigo",
                        leadingIcon = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PaxTextField(
                        value = friendPhone,
                        onValueChange = { 
                            // Senior Note: Filtra apenas números e limita a 11 dígitos (celular com DDD)
                            val digits = it.filter { char -> char.isDigit() }
                            if (digits.length <= 11) friendPhone = digits 
                        },
                        label = "WhatsApp (Com DDD)",
                        leadingIcon = Icons.AutoMirrored.Filled.Chat,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        visualTransformation = PhoneVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PaxButton(
                        text = if (isFormValid) "Enviar Indicação" else "Preencha os dados",
                        onClick = { handleSendIndication() },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PaxDesignSystem.Colors.Error.copy(alpha = 0.05f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PaxDesignSystem.Colors.Error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "A bonificação é liberada após a confirmação do plano e o pagamento da adesão pelo indicado.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PaxDesignSystem.Colors.Error,
                    lineHeight = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Máscara de Telefone (Brasil)
 */
class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 11) text.text.substring(0, 11) else text.text
        var out = ""
        for (i in trimmed.indices) {
            if (i == 0) out += "("
            out += trimmed[i]
            if (i == 1) out += ") "
            if (i == 6) out += "-"
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Senior Fix: Garante que offset 0 nunca retorne > 0 se o texto for vazio,
                // e usa coerceAtMost para evitar índices fora do range da string formatada.
                if (offset <= 0) return 0
                if (offset < 2) return (offset + 1).coerceAtMost(out.length)
                if (offset < 7) return (offset + 3).coerceAtMost(out.length)
                return (offset + 4).coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 1) return 0
                if (offset <= 2) return (offset - 1).coerceAtLeast(0)
                if (offset <= 5) return 2
                if (offset <= 10) return (offset - 3).coerceAtLeast(0)
                if (offset <= 11) return 7
                return (offset - 4).coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(androidx.compose.ui.text.AnnotatedString(out), numberOffsetTranslator)
    }
}

@Composable
fun RewardCard(
    modifier: Modifier,
    title: String,
    desc: String,
    benefit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier.height(160.dp),
        color = ReferBackground,
        shape = PaxDesignSystem.Shapes.Card,
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            
            Column {
                Text(
                    text = title, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 13.sp,
                    color = PaxDesignSystem.Colors.TextSecondary
                )
                Text(
                    text = benefit, 
                    fontWeight = FontWeight.Black, 
                    fontSize = 18.sp, 
                    color = color
                )
                Text(
                    text = desc, 
                    fontSize = 11.sp, 
                    color = PaxDesignSystem.Colors.TextSecondary,
                    lineHeight = 13.sp,
                    maxLines = 2
                )
            }
        }
    }
}
