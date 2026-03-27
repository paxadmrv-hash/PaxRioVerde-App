package com.example.paxrioverde.ui.laboratorio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.*

// Cores Institucionais
val BrandGreenMain = Color(0xFF386641)
val BrandGreenDark = Color(0xFF254D2E)
val WhatsAppColor = Color(0xFF25D366)
val SoftGray = Color(0xFFF7F9FB)

@Composable
fun ExamesLaboratoriaisScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val whatsappNumber = "556484037105"
    val contatoDireto = "64992784186"

    val examesPopulares = listOf(
        "Hemograma Completo", "Glicose em Jejum", "Perfil Lipídico (Colesterol)",
        "TSH e T4 Livre", "Vitamina D e B12", "Ureia e Creatinina",
        "EAS (Urina) e Parasitológico", "TGO e TGP (Fígado)"
    )

    Scaffold(
        containerColor = Color.White,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { HeaderSection(onBack) }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                    // Banner de Desconto
                    Surface(
                        modifier = Modifier.padding(top = 24.dp),
                        color = BrandGreenMain.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BrandGreenMain.copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, null, tint = BrandGreenMain)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Associados PAX: Até 70% de desconto!",
                                fontWeight = FontWeight.Bold,
                                color = BrandGreenMain,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Horários e Contato (Simétricos)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickInfoCard(
                            icon = Icons.Default.AccessTime,
                            label = "Coleta",
                            value = "07h às 09h",
                            modifier = Modifier.weight(1f)
                        )

                        QuickInfoCard(
                            icon = Icons.Default.Phone,
                            label = "Contato",
                            value = "(64) 99278-4186",
                            modifier = Modifier.weight(1f),
                            instruction = "Toque para ligar",
                            onClick = {
                                uriHandler.openUri("tel:$contatoDireto")
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Aviso de Jejum
                    Surface(
                        color = Color(0xFFFFF9E6),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFE082))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Info, null, tint = Color(0xFFF57C00), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Alguns exames exigem preparo especial ou jejum. Consulte-nos.",
                                fontSize = 13.sp,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botão de Orçamento
                    Text("Dúvidas ou Orçamentos?", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val msg = "Olá, gostaria de um orçamento para exames laboratoriais."
                            uriHandler.openUri("https://wa.me/$whatsappNumber?text=${msg.replace(" ", "%20")}")
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppColor),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(painterResource(Res.drawable.ic_whatsapp_social), null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Chamar no WhatsApp", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text("Exames mais frequentes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandGreenMain)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(examesPopulares) { exame -> ExameRow(exame) }

            item {
                FooterSection()
            }
        }
    }
}

@Composable
fun QuickInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier,
    instruction: String? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .height(95.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        color = SoftGray,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = BrandGreenMain, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandGreenMain)

            if (instruction != null) {
                Text(instruction, fontSize = 9.sp, color = BrandGreenMain.copy(alpha = 0.6f))
            } else {
                Spacer(modifier = Modifier.height(11.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(colors = listOf(BrandGreenMain, BrandGreenDark)),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Exames Laboratoriais", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Qualidade e Precisão nos resultados", fontSize = 15.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun ExameRow(nome: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, null, tint = BrandGreenMain.copy(alpha = 0.2f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(nome, fontSize = 15.sp, color = Color(0xFF34495E), fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = SoftGray, thickness = 0.5.dp)
}

@Composable
fun FooterSection() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 60.dp).padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = SoftGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Para consultar outros exames, entre em contato.", textAlign = TextAlign.Center, color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Pax Rio Verde\nRua Joaquim Vaz do Nascimento, 154 - Centro", textAlign = TextAlign.Center, color = Color.LightGray, fontSize = 12.sp, lineHeight = 18.sp)
    }
}
