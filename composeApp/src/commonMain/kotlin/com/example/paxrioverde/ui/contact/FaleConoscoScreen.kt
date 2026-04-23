package com.example.paxrioverde.ui.contact

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.util.urlEncode
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.*

// --- CONFIGURAÇÃO DE CORES ---
val BrandGreenMain = Color(0xFF386641)
val BrandGreenDark = Color(0xFF254D2E)
val WhatsAppGreen = Color(0xFF25D366)
val LightBackground = Color(0xFFF5F5F5)

@Composable
fun FaleConoscoScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current
    val enderecoSede = "R. Joaquim Vaz do Nascimento, 154, Centro, Rio Verde - GO"

    Scaffold(
        containerColor = LightBackground,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(scrollState)
        ) {
            HeaderSection(onBackClick)

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                SectionTitle(icon = Icons.Default.Call, title = "Telefones")
                ContactCard(Icons.Default.Call, "Pax Rio Verde", "(64) 3620-3100") {
                    uriHandler.openUri("tel:6436203100")
                }
                ContactCard(Icons.Default.DateRange, "Plantão 24h", "(64) 3620-3131") {
                    uriHandler.openUri("tel:6436203131")
                }
                ContactCard(Icons.Default.LocationOn, "Pax Montividiu", "(64) 3629-1440") {
                    uriHandler.openUri("tel:6436291440")
                }

                SectionTitle(icon = Icons.Default.Phone, title = "WhatsApp")
                ContactCard(Icons.Default.Phone, "WhatsApp Comercial", "(64) 9278-4186", isWhatsApp = true) {
                    uriHandler.openUri("https://wa.me/556481460004")
                }
                ContactCard(Icons.Default.Phone, "WhatsApp Funerária (Plantão 24h)", "(64) 98403-9405", isWhatsApp = true) {
                    uriHandler.openUri("https://wa.me/5564984039405")
                }

                Text("Redes Sociais", fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(top = 8.dp, start = 8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    SocialButtonCircle(Res.drawable.ic_instagram_social, Color(0xFFE1306C)) {
                        uriHandler.openUri("https://www.instagram.com/paxrioverde/")
                    }
                    SocialButtonCircle(Res.drawable.ic_facebook_social, Color(0xFF1877F2)) {
                        uriHandler.openUri("https://www.facebook.com/paxrioverde.rv/")
                    }
                    SocialButtonCircle(Res.drawable.ic_whatsapp_social, WhatsAppGreen) {
                        uriHandler.openUri("https://wa.me/5564992784186")
                    }
                    SocialButtonCircle(Res.drawable.ic_linkedin_social, Color(0xFF0A66C2)) {
                        uriHandler.openUri("https://www.linkedin.com/company/pax-rio-verde/")
                    }
                    SocialButtonCircle(Res.drawable.ic_youtube_social, Color(0xFFFF0000)) {
                        uriHandler.openUri("https://www.youtube.com/@paxrioverde7457")
                    }
                }

                InfoCard(
                    icon = Icons.Default.LocationOn,
                    title = "Endereço",
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=${urlEncode(enderecoSede)}")
                    }
                ) {
                    Text("Grupo Universo - Sede Administrativa", fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
                    Text("R. Joaquim Vaz do Nascimento, 154 - Centro", color = Color.Gray)
                    Text("Rio Verde - GO, 75901-220", color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Toque para abrir no Google Maps", color = BrandGreenMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                InfoCard(Icons.Default.DateRange, "Horário de Atendimento") {
                    ScheduleRow("Segunda a Sexta", "07:00 - 18:00")
                    ScheduleRow("Sábado", "07:00 - 12:00")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFEEEEEE))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Plantão 24h", fontWeight = FontWeight.Bold)
                        Text("Sempre disponível", color = BrandGreenMain, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
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
            Text("Fale Conosco", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Estamos aqui para ajudar você", fontSize = 15.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun SocialButtonCircle(res: DrawableResource, backgroundColor: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(54.dp).clip(CircleShape).clickable { onClick() },
        color = backgroundColor,
        shadowElevation = 3.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(14.dp)) {
            Image(
                painter = painterResource(res),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ContactCard(icon: ImageVector, title: String, detail: String, isWhatsApp: Boolean = false, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).background(Color(0xFFF2F2F2), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.DarkGray, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(detail, color = Color.Gray, fontSize = 14.sp)
            }
            Surface(
                modifier = Modifier.size(42.dp).clip(CircleShape).clickable { onClick() },
                color = if (isWhatsApp) WhatsAppGreen else BrandGreenMain
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)) {
                    if (isWhatsApp) {
                        Image(
                            painter = painterResource(Res.drawable.ic_whatsapp_social),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Call, null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(icon: ImageVector, title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = BrandGreenMain)
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ScheduleRow(day: String, hour: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(day, color = Color.Gray, fontSize = 14.sp)
        Text(hour, fontSize = 14.sp)
    }
}

@Composable
fun SectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        Icon(icon, null, tint = BrandGreenMain, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}
