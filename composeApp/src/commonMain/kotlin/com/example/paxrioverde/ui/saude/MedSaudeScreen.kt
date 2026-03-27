package com.example.paxrioverde.ui.saude

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.med_foto

// Definição das cores conforme solicitado
val VerdeUniverso = Color(0xFF386641)
val VerdeUniversoDark = Color(0xFF254D2E)
val VerdeAguaTitulo = Color(0xFFF5F1F1)

data class Profissional(val nome: String, val especialidade: String, val icone: ImageVector, val resumo: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedSaudeScreen(onBackClick: () -> Unit) {
    val whatsappMed = "5564993386180"
    val uriHandler = LocalUriHandler.current
    var profSelecionado by remember { mutableStateOf<Profissional?>(null) }

    val listaDoutores = listOf(
        Profissional("Dra. Amanda Quani", "Odontopediatra", Icons.Default.ChildCare, "Especialista em cuidar do sorriso dos pequenos com paciência e carinho."),
        Profissional("Dra. Ana Lacerda", "Endodontista", Icons.Default.Bloodtype, "Especialista em tratamentos de canal e saúde interna dos dentes."),
        Profissional("Dra. Ana Paula Ribeiro", "Prótese Dentária", Icons.Default.SettingsSuggest, "Especialista em reabilitação oral e estética funcional."),
        Profissional("Dra. Camila Bagnara", "Periodontista - Implantodontista", Icons.Default.Layers, "Foco na saúde da gengiva e instalação de implantes."),
        Profissional("Dr. Felipe Gomes", "Implantodontista", Icons.Default.AddModerator, "Especialista em devolver a confiança através de implantes dentários."),
        Profissional("Dra. Larissa Barbosa", "Implantodontista", Icons.Default.AddModerator, "Excelência em cirurgias de implante e reabilitação."),
        Profissional("Dra. Ligía Eduardo", "Clínico Geral", Icons.Default.Face, "Atendimento preventivo e diagnóstico para sua saúde bucal."),
        Profissional("Dra. Lívia Ataides", "Ortodontista", Icons.Default.AppRegistration, "Especialista em correção do sorriso com aparelhos modernos."),
        Profissional("Dra. Loyane Vilela", "Ortodontista", Icons.Default.AppRegistration, "Transformando sorrisos através do alinhamento ortodôntico."),
        Profissional("Dra. Paula Brunelli", "Saúde da Família", Icons.Default.FamilyRestroom, "Atendimento integral focado no bem-estar de toda a família."),
        Profissional("Dra. Stefane Lelis", "Harmonização Facial - Clínico Geral", Icons.Default.AutoFixHigh, "Especialista em estética facial e clínica geral de excelência.")
    )

    Scaffold(
        containerColor = Color(0xFFF7FBF7),
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { HeaderSection(onBackClick) }

            // CABEÇALHO COM IMAGEM E FRASE
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(24.dp))
                    Image(
                        painter = painterResource(Res.drawable.med_foto),
                        contentDescription = "Clínica Med Saúde",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Nossa equipe está pronta para cuidar do seu sorriso com o carinho e profissionalismo que você merece.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = VerdeUniverso,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Rua Joaquim Vaz do Nascimento, 154 - Centro",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    HorizontalDivider(Modifier.padding(vertical = 16.dp), color = VerdeUniverso.copy(alpha = 0.2f))
                }
            }

            items(listaDoutores) { prof ->
                Card(
                    onClick = { profSelecionado = prof },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, VerdeUniverso.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = VerdeUniverso.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(prof.icone, null, tint = VerdeUniverso, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(prof.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(prof.especialidade, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = VerdeUniverso.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // INTERATIVO COM WHATSAPP
        profSelecionado?.let { prof ->
            AlertDialog(
                onDismissRequest = { profSelecionado = null },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White,
                title = { Text(prof.nome, color = VerdeUniverso, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(prof.especialidade, fontWeight = FontWeight.SemiBold, color = VerdeUniverso.copy(alpha = 0.7f))
                        Spacer(Modifier.height(12.dp))
                        Text(prof.resumo)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val mensagem = "Olá, gostaria de agendar uma consulta com ${prof.nome}."
                            uriHandler.openUri("https://wa.me/$whatsappMed?text=${mensagem.replace(" ", "%20")}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.ChatBubbleOutline, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Chamar no WhatsApp")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { profSelecionado = null }) { Text("Voltar", color = Color.Gray) }
                }
            )
        }
    }
}

@Composable
fun HeaderSection(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(colors = listOf(VerdeUniverso, VerdeUniversoDark)),
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
            Text("Med Saúde", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Cuidando da sua saúde com excelência", fontSize = 15.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}
