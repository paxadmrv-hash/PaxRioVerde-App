package com.example.paxrioverde.ui.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.api.ApiService
import com.example.paxrioverde.api.DependenteItem
import com.example.paxrioverde.api.WalletCache
import kotlinx.coroutines.launch

// CORES
val PrimaryLimeGreen = Color(0xFF2E7D32)
val SecondarySageGreen = Color(0xFF91ad72)
val SoftGrayBg = Color(0xFFF2F6F3)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1F2937)

@Composable
fun PlansScreen(
    onBack: () -> Unit,
    idcliente: Int,
    userPlano: String = "Plano Familiar",
    valorMensalidade: String = "150,00"
) {
    val dependentList = WalletCache.dependentesList
    val isLoading = WalletCache.isPreloading
    
    LaunchedEffect(idcliente) {
        if (idcliente != 0) {
            WalletCache.preLoad(idcliente)
        }
    }

    Scaffold(
        containerColor = SoftGrayBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.1f))
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextDark
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Meu Plano",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                BentoCardHero(userPlano, valorMensalidade)
            }

            item {
                SectionTitleWidget("O que está incluso")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    IncludedItemCard(
                        title = "Assistência Funerária",
                        desc = "Urna, Flores, Higienização, Kit Lanche e Capelas.",
                        icon = Icons.Outlined.Church
                    )
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IncludedSmallCard(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            title = "Urna Pet",
                            desc = "Exclusivo para 1 pet, por plano",
                            icon = Icons.Filled.Pets
                        )
                        IncludedSmallCard(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            title = "Psicóloga",
                            desc = "Apoio e acolhimento ao luto",
                            icon = Icons.Outlined.Psychology
                        )
                    }

                    IncludedItemCard(
                        title = "Materiais Convalescentes",
                        desc = "Cadeiras de rodas, cadeira de banho, muletas (60 dias).",
                        icon = Icons.AutoMirrored.Filled.Accessible
                    )

                    IncludedItemCard(
                        title = "Show de Prêmios Anual",
                        desc = "Sorteio de 1 Moto 0km e prêmios variados.",
                        icon = Icons.Outlined.Celebration
                    )
                    IncludedItemCard(
                        title = "Sorteio Trimestral",
                        desc = "Sorteio de Eletrodomésticos e utilidades.",
                        icon = Icons.Outlined.CardGiftcard
                    )
                }
            }

            item {
                SectionTitleWidget("O que não está incluso")
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ExcludedItemRow("Taxas de Cemitério (Sepultamento/Jazigo)")
                        HorizontalDivider(color = SoftGrayBg, modifier = Modifier.padding(vertical = 8.dp))
                        ExcludedItemRow("Tanatopraxia e Embalsamento")
                        HorizontalDivider(color = SoftGrayBg, modifier = Modifier.padding(vertical = 8.dp))
                        ExcludedItemRow("Roupas para o falecido")
                        HorizontalDivider(color = SoftGrayBg, modifier = Modifier.padding(vertical = 8.dp))
                        ExcludedItemRow("Reconstituição Facial")
                    }
                }
            }

            item {
                SectionTitleWidget("Dependentes Cadastrados")
                Text(
                    text = "Abaixo listamos os dependentes ativos em seu plano.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (isLoading && dependentList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryLimeGreen)
                    }
                } else if (dependentList.isEmpty()) {
                    Text(
                        text = "Nenhum dependente encontrado.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                    )
                }
            }

            items(dependentList) { dependent ->
                DependentSoftCard(dependent)
            }
        }
    }
}

// COMPONENTES UI

@Composable
fun SectionTitleWidget(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun BentoCardHero(userPlano: String, valorMensalidade: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(10.dp, RoundedCornerShape(32.dp), spotColor = PrimaryLimeGreen.copy(alpha = 0.4f))
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(PrimaryLimeGreen, Color(0xFF558B2F))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Shield, null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = userPlano.uppercase(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cobertura Completa + Sogros",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Mensalidade: R$ $valorMensalidade",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun IncludedItemCard(title: String, desc: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SecondarySageGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PrimaryLimeGreen, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
            Text(desc, fontSize = 12.sp, color = Color.Gray, lineHeight = 14.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Filled.CheckCircle, null, tint = PrimaryLimeGreen, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun IncludedSmallCard(
    modifier: Modifier = Modifier,
    title: String,
    desc: String? = null,
    icon: ImageVector
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SecondarySageGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PrimaryLimeGreen, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)

        if (desc != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun ExcludedItemRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Block, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun DependentSoftCard(dependent: DependenteItem) {
    val nome = dependent.nomeDependente ?: "Dependente"
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(SoftGrayBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nome.firstOrNull()?.toString() ?: "?",
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = nome,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextDark
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PrimaryLimeGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = (dependent.parentesco ?: "Dependente").uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLimeGreen
                    )
                }
            }
        }
    }
}