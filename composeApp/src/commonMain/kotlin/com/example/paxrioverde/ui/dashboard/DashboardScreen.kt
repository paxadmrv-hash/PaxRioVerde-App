package com.example.paxrioverde.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.viewmodel.koinViewModel
import com.example.paxrioverde.api.LoginResponse
import com.example.paxrioverde.domain.model.PlanStatus
import com.example.paxrioverde.ui.components.WhatsNewModal
import com.example.paxrioverde.ui.components.bounceClick
import com.example.paxrioverde.ui.components.shimmerEffect
import com.example.paxrioverde.util.ReviewManager
import com.example.paxrioverde.ui.virtualcard.getAppVersionCode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import paxrioverde.composeapp.generated.resources.*
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.med_saude
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

val BrandGreen = Color(0xFF386641)
val SurfaceColor = Color(0xFFFFFFFF)
val BackgroundColor = Color(0xFFF2F6F3)
val TextDark = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
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
    onOpenDrawer: () -> Unit,
    onRefresh: (onComplete: () -> Unit) -> Unit = {},
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState
    val currentVersion = remember { getAppVersionCode() }
    val haptic = LocalHapticFeedback.current
    val fontScale = LocalDensity.current.fontScale
    val reviewManager = koinInject<ReviewManager>()
    val scope = rememberCoroutineScope()
    
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.checkWhatsNew(currentVersion)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                userData?.idcliente?.let { viewModel.updatePlanStatus(it) }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(userData?.prox_mens) {
        viewModel.scheduleNotifications(userData?.prox_mens)
    }

    LaunchedEffect(userData?.idcliente) {
        userData?.idcliente?.let { viewModel.updatePlanStatus(it) }
    }

    // Gatilho de Review: Quando o plano estiver ativo, sugere avaliação
    LaunchedEffect(uiState.planStatus) {
        if (uiState.planStatus == PlanStatus.ACTIVE) {
            reviewManager.requestReview()
        }
    }

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh { 
                scope.launch {
                    delay(500L)
                    isRefreshing = false 
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        indicator = {
            Box(modifier = Modifier.fillMaxWidth().statusBarsPadding(), contentAlignment = Alignment.TopCenter) {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    containerColor = Color.White,
                    color = BrandGreen
                )
            }
        }
    ) {
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
                    planStatus = uiState.planStatus,
                    userProxMens = uiState.nextPaymentDate ?: userData?.prox_mens ?: "--/--/----",
                    valorMensalidade = uiState.nextPaymentValue ?: userData?.valormens_prox_mens ?: "0,00",
                    valorCartao = userData?.valorcartao,
                    isLoading = userData == null,
                    fontScale = fontScale,
                    onOpenDrawer = onOpenDrawer,
                    onOpenNotifications = onOpenNotifications,
                    onOpenBoleto = onOpenBoleto
                )
            }

            item {
                HighlightsCarousel(onImageClick = { viewModel.onImageClick(it) })
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(text = "Acesso Rápido", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(bottom = 12.dp))
                    if (fontScale > 1.3) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                QuickActionItem(icon = Icons.Outlined.CreditCard, label = "Carteira", modifier = Modifier.weight(1f), onClick = onOpenWallet)
                                QuickActionItem(icon = Icons.AutoMirrored.Outlined.ReceiptLong, label = "Mensalidades", modifier = Modifier.weight(1f), onClick = onOpenBoleto)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                QuickActionItem(icon = Icons.Outlined.CardGiftcard, label = "Vantagens", modifier = Modifier.weight(1f), onClick = onOpenBenefits)
                                QuickActionItem(icon = Icons.Outlined.PersonAdd, label = "Indicar", modifier = Modifier.weight(1f), onClick = onOpenReferral)
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            QuickActionItem(icon = Icons.Outlined.CreditCard, label = "Carteira", onClick = onOpenWallet)
                            QuickActionItem(icon = Icons.AutoMirrored.Outlined.ReceiptLong, label = "Mensalidades", onClick = onOpenBoleto)
                            QuickActionItem(icon = Icons.Outlined.CardGiftcard, label = "Vantagens", onClick = onOpenBenefits)
                            QuickActionItem(icon = Icons.Outlined.PersonAdd, label = "Indicar", onClick = onOpenReferral)
                        }
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
                        if (userData?.dependente != "S") {
                            ServiceRowItem(Icons.Outlined.Description, "Meu Plano", "Ver coberturas e dependentes", onOpenPlans)
                        }
                        ServiceRowItem(Icons.Outlined.SupportAgent, "Fale Conosco", "Atendimento 24h", onOpenContact)
                    }
                }
            }
        }
    }

    if (uiState.expandedImageRes != null) {
        ExpandedImageDialog(imageRes = uiState.expandedImageRes, onDismiss = { viewModel.onImageClick(null) })
    }

    if (uiState.showWhatsNew) {
        WhatsNewModal(
            onDismiss = {
                viewModel.dismissWhatsNew(currentVersion)
            }
        )
    }
}

@Composable
fun DashboardHeader(
    userName: String, 
    userPlano: String, 
    planStatus: PlanStatus,
    userProxMens: String, 
    valorMensalidade: String,
    valorCartao: String?,
    isLoading: Boolean = false,
    fontScale: Float = 1f,
    onOpenDrawer: () -> Unit, 
    onOpenNotifications: () -> Unit, 
    onOpenBoleto: () -> Unit
) {
    val firstName = remember(userName) {
        val rawName = userName.trim().split(" ").firstOrNull() ?: "Cliente"
        rawName.lowercase().replaceFirstChar { it.uppercase() }
    }

    val totalMensalidade = remember(valorMensalidade) {
        valorMensalidade.replace("R$", "").trim()
    }

    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(BrandGreen).statusBarsPadding().padding(if (fontScale > 1.3) 16.dp else 24.dp)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenDrawer, modifier = Modifier.size(if (fontScale > 1.3) 40.dp else 48.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                    Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    if (isLoading) {
                        Box(modifier = Modifier.width(120.dp).height(24.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.width(80.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    } else {
                        Text(
                            text = "Olá, $firstName!", 
                            color = Color.White, 
                            fontSize = if (fontScale > 1.3) 18.sp else 22.sp, 
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        if (fontScale > 1.4) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(userPlano, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                Surface(
                                    color = if (planStatus == PlanStatus.ACTIVE) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = if (planStatus == PlanStatus.ACTIVE) null else BorderStroke(1.dp, planStatus.color.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = planStatus.label.uppercase(),
                                        color = if (planStatus == PlanStatus.ACTIVE) Color.White else planStatus.color,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(userPlano, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = if (planStatus == PlanStatus.ACTIVE) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = if (planStatus == PlanStatus.ACTIVE) null else BorderStroke(1.dp, planStatus.color.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = planStatus.label.uppercase(),
                                        color = if (planStatus == PlanStatus.ACTIVE) Color.White else planStatus.color,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                IconButton(onClick = onOpenNotifications, modifier = Modifier.size(if (fontScale > 1.3) 40.dp else 48.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                    Icon(Icons.Outlined.Notifications, "Notificações", tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(if (fontScale > 1.3) 16.dp else 24.dp))

            Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                val rowModifier = Modifier.padding(16.dp)
                if (fontScale > 1.5) {
                    Column(modifier = rowModifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (isLoading) {
                                Box(modifier = Modifier.width(140.dp).height(12.dp).clip(RoundedCornerShape(2.dp)).shimmerEffect())
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(modifier = Modifier.width(100.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                            } else {
                                Text(
                                    text = "Próxima Mensalidade: R$ $totalMensalidade", 
                                    color = Color.White.copy(alpha = 0.9f), 
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = userProxMens, 
                                    color = Color.White, 
                                    fontSize = 20.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (!isLoading) {
                            Button(
                                onClick = onOpenBoleto, 
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceColor),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text("Pagar Agora", color = BrandGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Row(modifier = rowModifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (isLoading) {
                                Box(modifier = Modifier.width(140.dp).height(12.dp).clip(RoundedCornerShape(2.dp)).shimmerEffect())
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(modifier = Modifier.width(100.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                            } else {
                                Text(
                                    text = "Próxima Mensalidade", 
                                    color = Color.White.copy(alpha = 0.9f), 
                                    fontSize = if (fontScale > 1.3) 10.sp else 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "R$ $totalMensalidade - $userProxMens",
                                    color = Color.White, 
                                    fontSize = if (fontScale > 1.3) 14.sp else 16.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (!isLoading) {
                            Button(
                                onClick = onOpenBoleto, 
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceColor),
                                contentPadding = PaddingValues(horizontal = if (fontScale > 1.3) 12.dp else 16.dp),
                                modifier = Modifier.height(if (fontScale > 1.3) 36.dp else 40.dp)
                            ) {
                                Text("Pagar", color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = if (fontScale > 1.3) 12.sp else 14.sp)
                            }
                        }
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
            Text(
                text = "Toque na imagem para saber mais", 
                fontSize = 11.sp, 
                color = Color.Gray.copy(alpha = 0.8f),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f).padding(start = 16.dp)
            )
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
    val haptic = LocalHapticFeedback.current
    val fontScale = LocalDensity.current.fontScale
    Card(
        shape = RoundedCornerShape(20.dp), 
        elevation = CardDefaults.cardElevation(2.dp), 
        colors = CardDefaults.cardColors(containerColor = SurfaceColor), 
        modifier = Modifier.fillMaxWidth().bounceClick().clickable { 
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick() 
        }
    ) {
        Row(modifier = Modifier.padding(if (fontScale > 1.3) 12.dp else 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(if (fontScale > 1.3) 48.dp else 56.dp).clip(CircleShape).background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(Res.drawable.med_saude), contentDescription = null, modifier = Modifier.size(if (fontScale > 1.3) 24.dp else 32.dp))
            }
            Spacer(modifier = Modifier.width(if (fontScale > 1.3) 12.dp else 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Med Saúde", 
                    fontWeight = FontWeight.Bold, 
                    color = TextDark,
                    fontSize = if (fontScale > 1.3) 14.sp else 16.sp
                )
                Text(
                    text = "Clínica Odontológica", 
                    fontSize = if (fontScale > 1.3) 11.sp else 12.sp, 
                    color = Color.Gray
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun PetAssistanceCard(onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val fontScale = LocalDensity.current.fontScale
    Card(
        shape = RoundedCornerShape(20.dp), 
        elevation = CardDefaults.cardElevation(2.dp), 
        colors = CardDefaults.cardColors(containerColor = SurfaceColor), 
        modifier = Modifier.fillMaxWidth().bounceClick().clickable { 
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick() 
        }
    ) {
        Row(modifier = Modifier.padding(if (fontScale > 1.3) 12.dp else 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(if (fontScale > 1.3) 48.dp else 56.dp).clip(CircleShape).background(Color(0xFFFFF3E0)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Pets, null, tint = Color(0xFFFB8C00), modifier = Modifier.size(if (fontScale > 1.3) 24.dp else 28.dp))
            }
            Spacer(modifier = Modifier.width(if (fontScale > 1.3) 12.dp else 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mundo Pet", 
                    fontWeight = FontWeight.Bold, 
                    color = TextDark,
                    fontSize = if (fontScale > 1.3) 14.sp else 16.sp
                )
                Text(
                    text = "Assistência para seu pet", 
                    fontSize = if (fontScale > 1.3) 11.sp else 12.sp, 
                    color = Color.Gray
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun ExamesLaboratoriaisCard(onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val fontScale = LocalDensity.current.fontScale
    Card(
        shape = RoundedCornerShape(20.dp), 
        elevation = CardDefaults.cardElevation(2.dp), 
        colors = CardDefaults.cardColors(containerColor = SurfaceColor), 
        modifier = Modifier.fillMaxWidth().bounceClick().clickable { 
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick() 
        }
    ) {
        Row(modifier = Modifier.padding(if (fontScale > 1.3) 12.dp else 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(if (fontScale > 1.3) 48.dp else 56.dp).clip(CircleShape).background(Color(0xFFE1F5FE)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Biotech, null, tint = Color(0xFF0288D1), modifier = Modifier.size(if (fontScale > 1.3) 24.dp else 28.dp))
            }
            Spacer(modifier = Modifier.width(if (fontScale > 1.3) 12.dp else 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Exames Laboratoriais", 
                    fontWeight = FontWeight.Bold, 
                    color = TextDark,
                    fontSize = if (fontScale > 1.3) 14.sp else 16.sp
                )
                Text(
                    text = "Todos os exames simples", 
                    fontSize = if (fontScale > 1.3) 11.sp else 12.sp, 
                    color = Color.Gray
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = modifier.bounceClick().clickable { 
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick() 
        }
    ) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.size(56.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, label, tint = BrandGreen) }
        }
        Text(
            text = label, 
            fontSize = 12.sp, 
            modifier = Modifier.padding(top = 8.dp), 
            color = TextDark,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
