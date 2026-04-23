package com.example.paxrioverde.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.paxrioverde.api.LoginResponse
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.*
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.med_saude

// CORES PADRÃO
val BrandGreen = Color(0xFF386641)
val SurfaceColor = Color(0xFFFFFFFF)
val BackgroundColor = Color(0xFFF2F6F3)
val TextDark = Color(0xFF1F2937)

@Composable
fun DashboardScreen(
    userData: LoginResponse?,
    valorCartao: String? = null,
    onOpenWallet: () -> Unit,
    onOpenBoleto: () -> Unit,
    onOpenReferral: () -> Unit,
    onOpenContact: () -> Unit,
    onOpenPlans: () -> Unit,
    onOpenBenefits: () -> Unit,
    onOpenPet: () -> Unit,
    onOpenMedSaude: () -> Unit,
    onOpenExames: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    var expandedImageRes by remember { mutableStateOf<org.jetbrains.compose.resources.DrawableResource?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            DashboardHeader(
                userName = userData?.nomecliente ?: "Visitante",
                userPlano = userData?.plano ?: "Carregando...",
                userProxMens = userData?.prox_mens ?: "--/--/----",
                valorMensalidade = userData?.valormens_prox_mens ?: "0,00",
                valorCartao = userData?.valorcartao,
                onOpenDrawer = onOpenDrawer,
                onOpenNotifications = onOpenNotifications,
                onOpenBoleto = onOpenBoleto
            )
        }

        item {
            HighlightsCarousel(onImageClick = { expandedImageRes = it })
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(text = "Acesso Rápido", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(bottom = 12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    QuickActionItem(icon = Icons.Outlined.CreditCard, label = "Carteira", onClick = onOpenWallet)
                    QuickActionItem(icon = Icons.Outlined.Receipt, label = "Mensalidades", onClick = onOpenBoleto)
                    QuickActionItem(icon = Icons.Outlined.CardGiftcard, label = "Vantagens", onClick = onOpenBenefits)
                    QuickActionItem(icon = Icons.Outlined.PersonAdd, label = "Indicar", onClick = onOpenReferral)
                }
            }
        }

        item { Box(modifier = Modifier.padding(horizontal = 24.dp)) { PetAssistanceCard(onClick = onOpenPet) } }
        item { Box(modifier = Modifier.padding(horizontal = 24.dp)) { MedSaudeCard(onClick = onOpenMedSaude) } }
        item { Box(modifier = Modifier.padding(horizontal = 24.dp)) { ExamesLaboratoriaisCard(onClick = onOpenExames) } }

        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(text = "Serviços", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(bottom = 12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ServiceRowItem(Icons.Outlined.Description, "Meu Plano", "Ver coberturas e dependentes", onOpenPlans)
                    ServiceRowItem(Icons.Outlined.SupportAgent, "Fale Conosco", "Atendimento 24h", onOpenContact)
                }
            }
        }
    }

    if (expandedImageRes != null) {
        ExpandedImageDialog(imageRes = expandedImageRes!!, onDismiss = { expandedImageRes = null })
    }
}

@Composable
fun DashboardHeader(
    userName: String, 
    userPlano: String, 
    userProxMens: String, 
    valorMensalidade: String,
    valorCartao: String?,
    onOpenDrawer: () -> Unit, 
    onOpenNotifications: () -> Unit, 
    onOpenBoleto: () -> Unit
) {
    val firstName = remember(userName) {
        val rawName = userName.trim().split(" ").firstOrNull() ?: "Cliente"
        rawName.lowercase().replaceFirstChar { it.uppercase() }
    }

    val totalMensalidade = remember(valorMensalidade, com.example.paxrioverde.api.WalletCache.pendingCardFee) {
        try {
            // Limpa o valor da mensalidade base (ex: "R$ 55,00" -> 55.0)
            val baseStr = valorMensalidade.replace("R$", "").replace(".", "").replace(",", ".").trim()
            val base = baseStr.toDoubleOrNull() ?: 0.0
            
            // Ignoramos completamente o valorCartao que vem por parâmetro (da API)
            // e usamos APENAS o que foi gerado nesta sessão específica.
            val extraStr = com.example.paxrioverde.api.WalletCache.pendingCardFee ?: "0.00"
            val extra = extraStr.replace("R$", "").replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
            
            if (extra > 0.1) {
                val total = base + extra
                val totalStr = total.toString().replace(".", ",")
                if (totalStr.contains(",")) {
                    val parts = totalStr.split(",")
                    val decimals = if (parts[1].length == 1) parts[1] + "0" else parts[1].take(2)
                    "${parts[0]},$decimals"
                } else {
                    "$totalStr,00"
                }
            } else {
                // Se não gerou cartão nesta sessão, mostra exatamente o que veio na mensalidade base
                valorMensalidade.replace("R$", "").trim()
            }
        } catch (e: Exception) {
            valorMensalidade.replace("R$", "").trim()
        }
    }

    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(BrandGreen).statusBarsPadding().padding(24.dp)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenDrawer, modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                    Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Olá, $firstName!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("$userPlano • Ativo", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
                IconButton(onClick = onOpenNotifications, modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                    Icon(Icons.Outlined.Notifications, "Notificações", tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Próxima Mensalidade: R$ $totalMensalidade", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        
                        Text(userProxMens, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = onOpenBoleto, colors = ButtonDefaults.buttonColors(containerColor = SurfaceColor)) {
                        Text("Pagar", color = BrandGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightsCarousel(onImageClick: (org.jetbrains.compose.resources.DrawableResource) -> Unit) {
    val images = listOf(Res.drawable.destaque_imagem_1, Res.drawable.destaque_imagem_2, Res.drawable.destaque_imagem_3, Res.drawable.destaque_imagem_4)
    val pagerState = rememberPagerState(pageCount = { images.size })

    LaunchedEffect(Unit) {
        while(true) {
            delay(4000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % images.size)
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Destaques", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(text = "Toque na imagem para saber mais", fontSize = 11.sp, color = Color.Gray.copy(alpha = 0.8f))
        }

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp
        ) { page ->
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clickable { onImageClick(images[page]) }
            ) {
                Image(
                    painter = painterResource(images[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            Modifier
                .height(24.dp)
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) BrandGreen else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
fun MedSaudeCard(onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(Res.drawable.med_saude), contentDescription = null, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Med Saúde", fontWeight = FontWeight.Bold, color = TextDark)
                Text("Clínica Odontológica", fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun PetAssistanceCard(onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFFFF3E0)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Pets, null, tint = Color(0xFFFB8C00), modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Mundo Pet", fontWeight = FontWeight.Bold, color = TextDark)
                Text("Assistência 24h para seu pet", fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun ExamesLaboratoriaisCard(onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFE1F5FE)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Biotech, null, tint = Color(0xFF0288D1), modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Exames Laboratoriais", fontWeight = FontWeight.Bold, color = TextDark)
                Text("Todos os exames simples", fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.size(56.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, label, tint = BrandGreen) }
        }
        Text(label, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp), color = TextDark)
    }
}

@Composable
fun ServiceRowItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = SurfaceColor, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(BrandGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = BrandGreen)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun ExpandedImageDialog(imageRes: org.jetbrains.compose.resources.DrawableResource, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.95f)).clickable { onDismiss() }, contentAlignment = Alignment.Center) {
            Image(painterResource(imageRes), null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
        }
    }
}
