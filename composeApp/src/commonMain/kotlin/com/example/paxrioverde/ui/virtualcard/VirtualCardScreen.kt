package com.example.paxrioverde.ui.virtualcard

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paxrioverde.ui.components.shimmerEffect
import org.koin.compose.koinInject
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import com.example.paxrioverde.api.*
import com.example.paxrioverde.ui.components.PaxPageIndicator
import com.example.paxrioverde.ui.components.PaxScreenHeader
import com.example.paxrioverde.ui.theme.PaxDesignSystem
import com.example.paxrioverde.util.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.*
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.delay

val WalletDarkBg = Color(0xFF13211C)
val WalletCardBg = Color(0xFF1E1E1E)
val TextWhite = Color(0xFFE3E3E3)
val TextGray = Color(0xFF8E8E93)
val ExpiredRed = Color(0xFFFF5252)

private fun formatCardName(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    if (parts.size <= 2) return name.uppercase()
    
    val firstName = parts.first()
    val lastName = parts.last()
    
    val result = StringBuilder(firstName)
    for (i in 1 until parts.size - 1) {
        val part = parts[i]
        // Mantém preposições curtas sem abreviar, ou abrevia nomes maiores
        if (part.lowercase() in listOf("da", "de", "do", "das", "dos", "e")) {
            result.append(" ").append(part)
        } else {
            result.append(" ").append(part.first()).append(".")
        }
    }
    result.append(" ").append(lastName)
    
    return result.toString().uppercase()
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VirtualCardScreen(
    onBack: () -> Unit,
    idcliente: Int = 0,
    idcontrato: Int = 0,
    idconvenio: Int = 0,
    idmensalidade: Int = 0,
    idcaixa: Int = 0,
    idfilial: Int = 0,
    dtvencimento: String = "",
    valorCartao: String? = null,
    isDependent: Boolean = false,
    userName: String? = null,
    userCpf: String? = null,
    onCardGenerated: () -> Unit = {},
    onNavigateToFinance: () -> Unit = {},
    viewModel: VirtualCardViewModel = koinViewModel()
) {
    val walletCache: WalletCache = koinInject()
    val cartoesList = walletCache.cartoesList
    val isPreloading = walletCache.isPreloading
    val fontScale = LocalDensity.current.fontScale

    val filteredCartoes = remember(cartoesList, isDependent, userName) {
        if (isDependent && userName != null) {
            cartoesList.filter { 
                it.nomeDependente?.trim()?.equals(userName.trim(), ignoreCase = true) == true ||
                it.nomeCliente.trim().equals(userName.trim(), ignoreCase = true)
            }
        } else {
            cartoesList
        }
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showGerarDialog by remember { mutableStateOf(false) }
    var expandedCard by remember { mutableStateOf<CartaoItem?>(null) }
    var cardToShare by remember { mutableStateOf<CartaoItem?>(null) }

    val silentCaptureLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()

    // Efeito para captura silenciosa (Carrossel)
    LaunchedEffect(cardToShare) {
        cardToShare?.let { card ->
            try {
                delay(100)
                val bitmap = silentCaptureLayer.toImageBitmap()
                val bytes = bitmap.toByteArray()
                if (bytes != null) {
                    shareImage(
                        bytes = bytes,
                        fileName = "cartao_pax_${card.idContrato}_${card.nomeCliente.take(5)}",
                        title = "Compartilhar Cartão Virtual"
                    )
                }
            } catch (e: Exception) {
                PaxLogger.e("Erro na captura de imagem", e, "VirtualCard")
            } finally {
                cardToShare = null
            }
        }
    }

    LaunchedEffect(idcliente) {
        if (idcliente != 0) {
            walletCache.preLoad(idcliente)
        }
    }

    // Senior Note: Limpeza de memória ao sair da tela.
    // Evita que Bitmaps pesados fiquem presos no Singleton WalletCache.
    DisposableEffect(Unit) {
        onDispose {
            walletCache.clearBitmaps()
        }
    }

    val pagerState = rememberPagerState(pageCount = { filteredCartoes.size })

    Scaffold(
        containerColor = WalletDarkBg,
        topBar = {
            // Senior: Header minimalista que se funde ao fundo dark da carteira
            PaxScreenHeader(
                onBackClick = onBack,
                backgroundBrush = Brush.verticalGradient(
                    listOf(WalletDarkBg, WalletDarkBg.copy(alpha = 0.8f))
                )
            ) {
                Text(
                    text = "Carteira",
                    fontSize = if (fontScale > 1.3) 22.sp else 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite,
                    letterSpacing = (-0.5).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            // Área do Cartão com design estilo Carrossel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = if (fontScale > 1.3) 240.dp else 280.dp)
                    .fillMaxHeight(if (fontScale > 1.3) 0.55f else 0.45f)
                    .padding(vertical = if (fontScale > 1.3) 8.dp else 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SEUS CARTÕES",
                    color = TextWhite,
                    fontSize = if (fontScale > 1.3) 14.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Deslize para ver todos os seus cartões",
                    color = TextGray,
                    fontSize = if (fontScale > 1.3) 10.sp else 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (isPreloading && filteredCartoes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                            .aspectRatio(1.586f)
                            .clip(RoundedCornerShape(20.dp))
                            .shimmerEffect()
                    )
                } else if (filteredCartoes.isEmpty()) {
                    Text("Nenhum cartão ativo.", color = TextGray)
                } else {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 48.dp),
                        pageSpacing = 16.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val item = filteredCartoes[page]

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.586f)
                                .clickable { expandedCard = item }
                                .graphicsLayer {
                                    val pageOffset = (
                                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                    )
                                    
                                    // Efeito 3D: Rotação no eixo Y
                                    rotationY = pageOffset * 25f
                                    
                                    // Escala dinâmica
                                    val scale = lerp(
                                        start = 1f,
                                        stop = 0.85f,
                                        fraction = pageOffset.absoluteValue.coerceIn(0f, 1f)
                                    )
                                    scaleX = scale
                                    scaleY = scale
                                    
                                    // Transparência para cartões distantes
                                    alpha = lerp(
                                        start = 1f,
                                        stop = 0.6f,
                                        fraction = pageOffset.absoluteValue.coerceIn(0f, 1f)
                                    )
                                    
                                    // Profundidade da câmera (essencial para o efeito 3D)
                                    cameraDistance = 8 * density
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CardContent(
                                    item = item,
                                    onShareClick = { cardToShare = item }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Novo Indicador Animado Senior
                    PaxPageIndicator(pagerState = pagerState)
                }
            }

            // Lista de cartões com peso para ocupar o resto da tela
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp, 24.dp))
                    .background(WalletCardBg)
                    .navigationBarsPadding()
                    .padding(24.dp)
            ) {
                Button(
                    onClick = { showGerarDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PaxDesignSystem.Colors.BrandLightGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isDependent) "Gerar Meu Cartão" else "Gerar Novo Cartão",
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    "Cartões Ativos",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(filteredCartoes) { index, card ->
                        // Animação de entrada em cascata
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = index * 100)) +
                                    slideInVertically(animationSpec = tween(durationMillis = 500, delayMillis = index * 100)) { it / 2 }
                        ) {
                            Box(modifier = Modifier.clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }) {
                                ActiveCardRow(item = card)
                            }
                        }
                    }
                }
            }
        }

        // Camada de Captura Silenciosa (Invisível)
        if (cardToShare != null) {
            Box(
                modifier = Modifier
                    .size(width = 400.dp, height = 250.dp)
                    .graphicsLayer { alpha = 0.01f }
                    .drawWithContent {
                        silentCaptureLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(silentCaptureLayer)
                    }
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    CardContent(item = cardToShare!!)
                }
            }
        }
    }

    if (showGerarDialog) {
        GerarCartaoDialog(
            onDismiss = { 
                showGerarDialog = false
                viewModel.resetState()
            },
            idcliente = idcliente,
            idcaixa = idcaixa,
            idcontrato = idcontrato,
            idconvenio = idconvenio,
            idfilial = idfilial,
            valorCartao = valorCartao,
            isUserDependent = isDependent,
            userName = userName,
            userCpf = userCpf,
            onSuccess = onCardGenerated,
            onNavigateToFinance = onNavigateToFinance,
            viewModel = viewModel
        )
    }

    if (expandedCard != null) {
        CardExpansionDialog(
            item = expandedCard!!,
            onDismiss = { expandedCard = null }
        )
    }
}

@Composable
fun CardContent(item: CartaoItem, onShareClick: (() -> Unit)? = null) {
    val walletCache: WalletCache = koinInject()
    val sessionManager = remember { SessionManager() }
    // Verifica se temos um estilo salvo localmente para este ID, senão usa o tipo do backend
    val style = sessionManager.getCardStyle(item.idControle) ?: item.tipo
    val lowerTipo = style.lowercase()
    val isKids = lowerTipo.contains("kids")
    val isTeen = lowerTipo.contains("teen")

    val parentesco = if (item.dep == "S") {
        walletCache.dependentesList.find { it.nomeDependente == item.nomeDependente }?.parentesco ?: "DEPENDENTE"
    } else {
        "TITULAR"
    }

    val backgroundResource = when {
        isKids -> Res.drawable.card_kids
        isTeen -> Res.drawable.card_teen
        else -> Res.drawable.card_titular
    }

    // Valores otimizados para descer as informações e liberar o fundo
    val fontScale = LocalDensity.current.fontScale
    val paddingHoriz = if (isKids) 32.dp else 26.dp
    val paddingVert = if (isKids) 12.dp else 10.dp
    
    // Limitamos o crescimento da fonte dentro do cartão para não quebrar a arte
    val nomeSize = (if (isKids) 11.sp else 13.sp) * (if (fontScale > 1.2) 1.1f else 1f)
    val infoSize = (if (isKids) 9.sp else 11.sp) * (if (fontScale > 1.2) 1.1f else 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Botão de Compartilhar Rápido (Glassmorphism) - Apenas se onShareClick for passado
        if (onShareClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { onShareClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartilhar",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = paddingHoriz, end = paddingHoriz, bottom = paddingVert),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Linha 1: Nome e Parentesco
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val rawName = if (item.dep == "S") item.nomeDependente ?: "" else item.nomeCliente
                Text(
                    text = formatCardName(rawName),
                    color = Color.Black.copy(alpha = 0.85f),
                    fontSize = nomeSize,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = parentesco ?: "",
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = infoSize,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Linha 2: Contrato e Validade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "CONTRATO: ${item.idContrato ?: ""}",
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = infoSize,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
                Text(
                    text = item.dtValidade,
                    color = Color.Black,
                    fontSize = if (isKids) 11.sp else 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
fun CardExpansionDialog(item: CartaoItem, onDismiss: () -> Unit) {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            // Overlay clicável apenas no fundo para não interferir nos botões
            Box(Modifier.fillMaxSize().clickable { onDismiss() })

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.586f)
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CardContent(item = item)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                // Nome Completo destacado abaixo do cartão
                Text(
                    text = (if (item.dep == "S") item.nomeDependente ?: "" else item.nomeCliente).uppercase(),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Validade destacada abaixo do nome
                Text(
                    text = "VALIDADE: ${item.dtValidade}",
                    color = PaxDesignSystem.Colors.BrandLightGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botão Compartilhar
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val bitmap = graphicsLayer.toImageBitmap()
                                    val bytes = bitmap.toByteArray()
                                    if (bytes != null) {
                                        shareImage(
                                            bytes = bytes,
                                            fileName = "cartao_pax_${item.idContrato}",
                                            title = "Compartilhar Cartão Virtual"
                                        )
                                    }
                                } catch (e: Exception) {
                                    PaxLogger.e("Erro na operação de imagem", e, "VirtualCard")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PaxDesignSystem.Colors.BrandLightGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "COMPARTILHAR", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }

                    // Botão Baixar
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                try {
                                    val bitmap = graphicsLayer.toImageBitmap()
                                    val bytes = bitmap.toByteArray()
                                    if (bytes != null) {
                                        saveImageToGallery(
                                            bytes = bytes,
                                            fileName = "cartao_pax_${item.idContrato}"
                                        )
                                    } else {
                                        PaxLogger.e("Erro: toByteArray retornou null", subTag = "VirtualCard")
                                    }
                                } catch (e: Exception) {
                                    PaxLogger.e("Erro ao baixar", e, "VirtualCard")
                                }
                            }
                        },
                        border = BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Download, null, tint = TextWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "BAIXAR", 
                            color = TextWhite, 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("FECHAR", color = TextGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerarCartaoDialog(
    onDismiss: () -> Unit,
    idcliente: Int,
    idcaixa: Int,
    idcontrato: Int,
    idconvenio: Int,
    idfilial: Int,
    valorCartao: String? = null,
    isUserDependent: Boolean = false,
    userName: String? = null,
    userCpf: String? = null,
    onSuccess: () -> Unit = {},
    onNavigateToFinance: () -> Unit = {},
    viewModel: VirtualCardViewModel
) {
    val walletCache: WalletCache = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var isTitular by remember { mutableStateOf(!isUserDependent) }
    val dependentesList = walletCache.dependentesList
    var selectedDependente by remember { mutableStateOf<DependenteItem?>(null) }
    var expanded by remember { mutableStateOf(false) }
    
    val estilos = listOf("Adulto", "Teen", "Kids")
    var selectedEstilo by remember { mutableStateOf(estilos[0]) }
    var expandedEstilo by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current

    // Calcula a nova validade baseada na fidelidade (WalletCache)
    val calculatedValidity = remember(walletCache.mensalidadesList.size) {
        walletCache.getCalculatedValidity("") 
    }
    
    val sessionManager = remember { SessionManager() }

    Dialog(onDismissRequest = { 
        if (uiState !is VirtualCardState.Loading) {
            onDismiss() 
        }
    }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val state = uiState) {
                    is VirtualCardState.Idle -> {
                        Text(
                            if (isUserDependent) "Gerar Meu Cartão" else "Gerar Novo Cartão",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaxDesignSystem.Colors.BrandLightGreen,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (calculatedValidity.isNotEmpty()) {
                            Surface(
                                color = PaxDesignSystem.Colors.BrandLightGreen.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, null, tint = PaxDesignSystem.Colors.BrandLightGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Seu novo cartão será válido até: $calculatedValidity",
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        if (!isUserDependent) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isTitular = true }) {
                                RadioButton(selected = isTitular, onClick = { isTitular = true }, colors = RadioButtonDefaults.colors(selectedColor = PaxDesignSystem.Colors.BrandLightGreen))
                                Text("Titular", color = Color.Black)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isTitular = false }) {
                                RadioButton(selected = !isTitular, onClick = { isTitular = false }, colors = RadioButtonDefaults.colors(selectedColor = PaxDesignSystem.Colors.BrandLightGreen))
                                Text("Dependente", color = Color.Black)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (!isTitular) {
                            Text("Estilo do Cartão", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                            ExposedDropdownMenuBox(expanded = expandedEstilo, onExpandedChange = { expandedEstilo = !expandedEstilo }) {
                                OutlinedTextField(
                                    value = selectedEstilo,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstilo) },
                                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PaxDesignSystem.Colors.BrandLightGreen, unfocusedBorderColor = Color.LightGray, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                                )
                                ExposedDropdownMenu(expanded = expandedEstilo, onDismissRequest = { expandedEstilo = false }, modifier = Modifier.background(Color.White)) {
                                    estilos.forEach { estilo ->
                                        DropdownMenuItem(
                                            text = { Text(estilo, color = Color.Black) },
                                            onClick = { selectedEstilo = estilo; expandedEstilo = false },
                                            colors = MenuDefaults.itemColors(textColor = Color.Black)
                                        )
                                    }
                                }
                            }
                            
                            if (!isUserDependent) {
                                Spacer(modifier = Modifier.height(16.dp))
                                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                                    OutlinedTextField(
                                        value = selectedDependente?.nomeDependente ?: "Selecione o dependente",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PaxDesignSystem.Colors.BrandLightGreen, unfocusedBorderColor = Color.LightGray, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                                    )
                                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color.White)) {
                                        dependentesList.forEach { dep ->
                                            DropdownMenuItem(
                                                text = { Text("${dep.nomeDependente}", color = Color.Black) },
                                                onClick = { selectedDependente = dep; expanded = false },
                                                colors = MenuDefaults.itemColors(textColor = Color.Black)
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Se o usuário for dependente, ele gera o cartão para SI MESMO
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Gerando cartão para: $userName",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            LaunchedEffect(Unit) { selectedEstilo = "Adulto" }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { 
                                if (isUserDependent) {
                                    // Fluxo simplificado para dependente
                                    viewModel.gerarCartaoDireto(
                                        idcaixa = idcaixa,
                                        idcliente = idcliente,
                                        tipo = "dependente",
                                        nomeDependente = userName,
                                        isGratuito = true,
                                        idcontrato = idcontrato,
                                        idconvenio = idconvenio,
                                        cpfDependente = userCpf,
                                        dtvencimento = calculatedValidity,
                                        parentesco = "DEPENDENTE",
                                        idfilial = idfilial
                                    )
                                } else if (isTitular || selectedDependente != null) {
                                    viewModel.gerarCartaoDireto(
                                        idcaixa = idcaixa,
                                        idcliente = idcliente,
                                        tipo = if (isTitular) "titular" else "dependente",
                                        nomeDependente = if (isTitular) null else selectedDependente?.nomeDependente,
                                        isGratuito = true,
                                        idcontrato = idcontrato,
                                        idconvenio = idconvenio,
                                        cpfDependente = if (isTitular) null else selectedDependente?.cpf,
                                        dtvencimento = calculatedValidity,
                                        parentesco = if (isTitular) "TITULAR" else selectedDependente?.parentesco,
                                        idfilial = idfilial
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PaxDesignSystem.Colors.BrandLightGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("GERAR CARTÃO AGORA", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = onDismiss) { Text("Fechar", color = Color.Gray) }
                    }

                    is VirtualCardState.Loading -> {
                        CircularProgressIndicator(color = PaxDesignSystem.Colors.BrandLightGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Processando...", color = Color.Black)
                    }

                    is VirtualCardState.PixGenerated -> {
                        Text("Pagamento PIX", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.BrandLightGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Copie o código abaixo e pague no seu banco:", textAlign = TextAlign.Center, color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = state.pixCode,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { 
                                clipboardManager.setText(AnnotatedString(state.pixCode))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PaxDesignSystem.Colors.BrandLightGreen)
                        ) {
                            Text("COPIAR CÓDIGO")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aguardando pagamento...", color = Color.Gray, fontSize = 12.sp)
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), color = PaxDesignSystem.Colors.BrandLightGreen)
                        TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Red) }
                    }

                    is VirtualCardState.Success -> {
                        Icon(Icons.Default.CheckCircle, null, tint = PaxDesignSystem.Colors.BrandLightGreen, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sucesso!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.BrandLightGreen)
                        Text("Cartão gerado com sucesso.", color = Color.Black)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        state.newCard?.let { card ->
                            // Persiste o estilo escolhido localmente
                            LaunchedEffect(card.idControle) {
                                sessionManager.saveCardStyle(card.idControle, selectedEstilo)
                            }
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().aspectRatio(1.586f)
                            ) {
                                CardContent(item = card)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { 
                                onDismiss()
                                onSuccess()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PaxDesignSystem.Colors.BrandLightGreen)
                        ) {
                            Text("OK")
                        }
                    }

                    is VirtualCardState.Error -> {
                        Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Erro", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Text(state.message, color = Color.Black, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.resetState() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PaxDesignSystem.Colors.BrandLightGreen)
                        ) {
                            Text("TENTAR NOVAMENTE")
                        }
                        TextButton(onClick = onDismiss) { Text("Fechar", color = Color.Gray) }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveCardRow(item: CartaoItem) {
    val isExpired = isCardExpired(item.dtValidade)

    // Glassmorphism: Fundo semi-transparente com borda sutil e brilhante
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f)) // Camada de "vidro"
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isExpired) PaxDesignSystem.Colors.Error.copy(alpha = 0.1f) 
                        else PaxDesignSystem.Colors.BrandLightGreen.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpired) Icons.Default.Warning else Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = if (isExpired) ExpiredRed else PaxDesignSystem.Colors.BrandLightGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (item.dep == "S") item.nomeDependente ?: "" else item.nomeCliente, 
                    color = TextWhite, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 15.sp,
                    maxLines = 2, // Senior UX: Permite 2 linhas para evitar cortes
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Estilo: ${item.tipo}", 
                    color = TextGray, 
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "VALIDADE", 
                color = TextGray, 
                fontSize = 9.sp, 
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = item.dtValidade,
                color = if (isExpired) ExpiredRed else TextWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
    }
}
