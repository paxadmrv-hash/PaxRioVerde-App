package com.example.paxrioverde.ui.benefits

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paxrioverde.api.CartaoItem
import com.example.paxrioverde.api.WalletCache
import com.example.paxrioverde.ui.components.shimmerEffect
import com.example.paxrioverde.util.SessionManager
import com.example.paxrioverde.util.shareText
import com.example.paxrioverde.util.urlEncode
import kotlin.time.Duration.Companion.milliseconds
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import paxrioverde.composeapp.generated.resources.*
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.ic_whatsapp_social

// ─────────────────────────────────────────────
// DESIGN TOKENS
// ─────────────────────────────────────────────
private val Forest800 = Color(0xFF1E3A2F)
private val Forest600 = Color(0xFF386641)
private val Forest50  = Color(0xFFF0F7F1)
private val Amber500  = Color(0xFFD97706)
private val Slate800  = Color(0xFF1E293B)
private val Slate500  = Color(0xFF64748B)
private val Slate200  = Color(0xFFE2E8F0)
private val CardWhite = Color(0xFFFFFFFF)
private val WhatsAppGreen = Color(0xFF25D366)

// ─────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────
private fun formatCardName(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    if (parts.size <= 2) return name.uppercase()
    val firstName = parts.first()
    val lastName = parts.last()
    val result = StringBuilder(firstName)
    for (i in 1 until parts.size - 1) {
        val part = parts[i]
        if (part.lowercase() in listOf("da", "de", "do", "das", "dos", "e")) {
            result.append(" ").append(part)
        } else {
            result.append(" ").append(part.first()).append(".")
        }
    }
    result.append(" ").append(lastName)
    return result.toString().uppercase()
}

@Composable
fun highlightText(fullText: String, query: String, highlightColor: Color): AnnotatedString {
    if (query.isEmpty()) return AnnotatedString(fullText)
    
    return buildAnnotatedString {
        var start = 0
        while (start < fullText.length) {
            val index = fullText.indexOf(query, start, ignoreCase = true)
            if (index == -1) {
                append(fullText.substring(start))
                break
            }
            append(fullText.substring(start, index))
            withStyle(SpanStyle(background = highlightColor.copy(alpha = 0.2f), fontWeight = FontWeight.Black)) {
                append(fullText.substring(index, index + query.length))
            }
            start = index + query.length
        }
    }
}

// ─────────────────────────────────────────
// TELA PRINCIPAL
// ─────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BenefitsScreen(
    onBack: () -> Unit,
    idcliente: Int = 0,
    isDependent: Boolean = false,
    userName: String? = null,
    viewModel: BenefitsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val walletCache: WalletCache = koinInject()
    val scope = rememberCoroutineScope()
    
    var expandedCard by remember { mutableStateOf<CartaoItem?>(null) }

    val filteredCards = remember(walletCache.cartoesList, isDependent, userName) {
        val list = walletCache.cartoesList
        if (isDependent && userName != null) {
            val normalizedUser = userName.trim().lowercase()
            list.filter { card ->
                val cardName = (if (card.dep == "S") card.nomeDependente else card.nomeCliente)?.trim()?.lowercase()
                card.dep == "S" && cardName == normalizedUser
            }
        } else {
            list
        }
    }
    
    val listState = rememberLazyListState()
    
    // Senior UX: Estado para o botão Voltar ao Topo
    val showBackToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2
        }
    }

    // Controle Senior: Evita que a animação de cascata rode a cada scroll, 
    // o que causava o "pulo" na lista ao subir.
    var animateItems by rememberSaveable { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        // Senior Performance: Sincronização com animação de slide (300ms) + margem
        kotlinx.coroutines.delay(350)
        viewModel.setReady()
        
        if (animateItems) {
            kotlinx.coroutines.delay(1150) // Ciclo total de 1.5s
            animateItems = false
        }
    }

    val cities = listOf("Todas", "Rio Verde", "Montividiu", "Aparecida do Rio Doce", "Santo Antônio da Barra")

    LaunchedEffect(idcliente) {
        if (idcliente != 0) {
            walletCache.preLoad(idcliente)
        }
    }

    LaunchedEffect(uiState.selectedCategory, uiState.selectedCity) {
        if (uiState.isReady) {
            listState.animateScrollToItem(0) 
        }
    }

    Scaffold(
        containerColor = Forest50,
        floatingActionButton = {
            AnimatedVisibility(
                visible = showBackToTop,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = Amber500,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Voltar ao topo")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp + padding.calculateBottomPadding())
        ) {
            item {
                BenefitsHeader(
                    partnerCount = realPartners.size,
                    onBack = onBack,
                    cards = filteredCards.take(3),
                    isLoadingCards = walletCache.isPreloading && filteredCards.isEmpty(),
                    onCardClick = { expandedCard = it }
                )
            }

            // Senior UX: Sticky Header para Busca e Filtros
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Forest50,
                    shadowElevation = 8.dp // Senior UX: Sombra sutil ao ficar fixo
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding() // Senior Fix: Evita overlap com ícones do sistema
                            .padding(bottom = 8.dp)
                    ) {
                        SearchBarRow(
                            query = uiState.searchQuery,
                            onQueryChange = { viewModel.onSearchQueryChange(it) }
                        )
                        
                        CityFilterRow(
                            cities = cities,
                            selectedCity = uiState.selectedCity,
                            onCitySelected = { viewModel.onCitySelected(it) }
                        )
                        
                        BenefitsCategoryFilterRow(
                            categories = benefitsCategories,
                            selectedCategory = uiState.selectedCategory,
                            onCategorySelected = { viewModel.onCategorySelected(it) }
                        )
                    }
                }
            }

            item {
                BenefitsSectionHeader(
                    title = if (uiState.selectedCategory == "Todos") "Todos os parceiros" else uiState.selectedCategory,
                    subtitle = if (uiState.selectedCity == "Todas") "Todas as cidades" else uiState.selectedCity,
                    count = uiState.filteredPartners.size
                )
            }

            if (!uiState.isReady) {
                items(3) { PartnerSkeleton() }
            } else if (uiState.filteredPartners.isEmpty()) {
                item { BenefitsEmptyState(uiState.selectedCategory) }
            } else {
                itemsIndexed(
                    items = uiState.filteredPartners,
                    key = { _, p -> p.id },
                    contentType = { _, _ -> "partner" }
                ) { index, partner ->
                    AnimatedPartnerCard(
                        index = index, 
                        partner = partner,
                        shouldAnimate = animateItems,
                        searchQuery = uiState.searchQuery
                    )
                }
            }

            item { BenefitsLegalDisclaimer() }
        }
    }

    if (expandedCard != null) {
        QuickCardDialog(
            item = expandedCard!!,
            onDismiss = { expandedCard = null }
        )
    }
}

// ─────────────────────────────────────────
// HEADER UNIFICADO (ELITE)
// ─────────────────────────────────────────
@Composable
fun BenefitsHeader(
    partnerCount: Int,
    onBack: () -> Unit,
    cards: List<CartaoItem> = emptyList(),
    isLoadingCards: Boolean = false,
    onCardClick: (CartaoItem) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(listOf(Forest800, Forest600)))
            .statusBarsPadding()
    ) {
        // TopBar Integrada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Clube de Vantagens",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    "Pax Rio Verde",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        // Hero Content com QuickCard Stack
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.1f)) {
                Text(
                    partnerCount.toString(),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.offset(x = (-4).dp, y = 8.dp)
                )
                Text(
                    "parceiros com",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.offset(y = (-16).dp)
                )
                Text(
                    "descontos exclusivos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.offset(y = (-18).dp)
                )
                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.offset(y = (-8).dp)
                ) {
                    Text(
                        text = "Apresente seu cartão",
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            
            // Stack de Cartões
            if (cards.isNotEmpty() || isLoadingCards) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (isLoadingCards) {
                        QuickCardSkeleton()
                    } else {
                        QuickCardStack(
                            cards = cards,
                            onClick = { onCardClick(it) }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * Skeleton para o Stack de Cartões em carregamento
 */
@Composable
fun QuickCardSkeleton() {
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(110.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        repeat(2) { index ->
            val visualIndex = 1 - index
            val offsetX = (visualIndex * 20).dp
            val offsetY = (visualIndex * -12).dp
            val rotation = 10f - (visualIndex * 4f)
            
            Box(
                modifier = Modifier
                    .offset(x = -offsetX, y = offsetY)
                    .width(120.dp)
                    .aspectRatio(1.586f)
                    .rotate(rotation)
                    .clip(RoundedCornerShape(10.dp))
                    .shimmerEffect()
            )
        }
    }
}

// ─────────────────────────────────────────
// QUICK CARD STACK (ELITE)
// ─────────────────────────────────────────
@Composable
fun QuickCardStack(cards: List<CartaoItem>, onClick: (CartaoItem) -> Unit) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(110.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        val displayedCards = cards.take(3)
        displayedCards.asReversed().forEachIndexed { index, item ->
            val visualIndex = (displayedCards.size - 1) - index
            val offsetX = (visualIndex * 20).dp
            val offsetY = (visualIndex * -12).dp
            val rotation = 10f - (visualIndex * 4f)
            
            Box(
                modifier = Modifier
                    .offset(x = -offsetX, y = offsetY)
                    .width(120.dp)
                    .aspectRatio(1.586f)
                    .rotate(rotation)
                    .shadow(
                        elevation = if (visualIndex == 0) 12.dp else 4.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .clickable { onClick(item) }
            ) {
                CardMiniContent(item)
            }
        }
    }
}

@Composable
fun CardMiniContent(item: CartaoItem) {
    val sessionManager: SessionManager = koinInject()
    val style = sessionManager.getCardStyle(item.idControle) ?: item.tipo
    val lowerTipo = style.lowercase()
    
    val backgroundResource = when {
        lowerTipo.contains("kids") -> Res.drawable.card_kids
        lowerTipo.contains("teen") -> Res.drawable.card_teen
        else -> Res.drawable.card_titular
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                    )
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                Text(
                    text = formatCardName(if (item.dep == "S") item.nomeDependente ?: "" else item.nomeCliente),
                    color = Color.White,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    letterSpacing = 0.3.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.dtValidade,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 6.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = if (item.dep == "S") "DEP" else "TITULAR",
                            color = Color.White,
                            fontSize = 5.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickCardDialog(item: CartaoItem, onDismiss: () -> Unit) {
    val sessionManager: SessionManager = koinInject()
    val style = sessionManager.getCardStyle(item.idControle) ?: item.tipo
    val lowerTipo = style.lowercase()
    
    val backgroundResource = when {
        lowerTipo.contains("kids") -> Res.drawable.card_kids
        lowerTipo.contains("teen") -> Res.drawable.card_teen
        else -> Res.drawable.card_titular
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.586f)
                        .shadow(24.dp, RoundedCornerShape(20.dp))
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(backgroundResource),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillBounds
                            )
                            
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                            )
                                        )
                                        .padding(horizontal = 24.dp, vertical = 16.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val rawName = if (item.dep == "S") item.nomeDependente ?: "" else item.nomeCliente
                                            Text(
                                                text = formatCardName(rawName),
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 1.sp
                                            )
                                            Surface(
                                                color = Amber500,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = if (item.dep == "S") "DEPENDENTE" else "TITULAR",
                                                    color = Color.Black,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        
                                        Spacer(Modifier.height(4.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "CONTRATO: ${item.idContrato ?: ""}",
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "VALIDADE: ${item.dtValidade}",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("FECHAR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SearchBarRow(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar parceiro ou serviço...", fontSize = 14.sp, color = Slate500) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Forest600) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, null, tint = Slate500)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Slate800,
                unfocusedTextColor = Slate800
            ),
            singleLine = true
        )
    }
}


// ─────────────────────────────────────────
// FILTRO CIDADES (ELITE)
// ─────────────────────────────────────────
@Composable
fun CityFilterRow(
    cities: List<String>,
    selectedCity: String,
    onCitySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(cities) { city ->
            val isSelected = selectedCity == city
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCitySelected(city) },
                color = if (isSelected) Amber500 else Forest600.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp),
                border = if (!isSelected) BorderStroke(1.dp, Forest600.copy(alpha = 0.1f)) else null
            ) {
                Text(
                    city,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Forest600,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// FILTRO CATEGORIAS (ELITE)
// ─────────────────────────────────────────
@Composable
fun BenefitsCategoryFilterRow(
    categories: List<BenefitsCategory>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { cat ->
            val isSelected = selectedCategory == cat.label
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCategorySelected(cat.label) },
                color = if (isSelected) cat.color else Color.White,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (isSelected) cat.color else Slate200)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        cat.icon,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) Color.White else cat.color
                    )
                    Text(
                        cat.label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Slate800
                    )
                }
            }
        }
    }
}


// ─────────────────────────────────────────
// SECTION HEADER
// ─────────────────────────────────────────
@Composable
fun BenefitsSectionHeader(title: String, subtitle: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate800)
            Text(subtitle, fontSize = 11.sp, color = Slate500)
        }
        Surface(color = Forest600.copy(alpha = 0.1f), shape = CircleShape) {
            Text("$count", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Forest600,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
        }
    }
}

// ─────────────────────────────────────────
// PARTNER CARD (ELITE)
// ─────────────────────────────────────────
@Composable
fun AnimatedPartnerCard(index: Int, partner: Partner, shouldAnimate: Boolean, searchQuery: String = "") {
    if (!shouldAnimate) {
        PartnerCard(partner, searchQuery)
        return
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(partner.id) {
        kotlinx.coroutines.delay((index % 10 * 30).milliseconds)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
            initialOffsetY = { 40 },
            animationSpec = tween(400)
        )
    ) {
        PartnerCard(partner, searchQuery)
    }
}

/**
 * Skeleton para carregamento inicial ultra-rápido da lista
 */
@Composable
fun PartnerSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.width(150.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.width(100.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            }
        }
    }
}

@Composable
fun PartnerCard(partner: Partner, searchQuery: String = "") {
    val uriHandler = LocalUriHandler.current
    var expanded by remember { mutableStateOf(false) }

    val categoryColor = remember(partner.category) {
        benefitsCategories.find { it.label == partner.category }?.color ?: Forest600
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)), // Senior UX: Expansão suave
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape     = RoundedCornerShape(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(categoryColor.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(partner.icon, null, tint = categoryColor, modifier = Modifier.size(28.dp))
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = highlightText(partner.name, searchQuery, categoryColor),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate800,
                            modifier = Modifier.weight(1f),
                            maxLines = if (expanded) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            color = categoryColor.copy(alpha = 0.08f),
                            shape = CircleShape,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                partner.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(6.dp))
                    
                    Surface(
                        color = categoryColor.copy(alpha = 0.04f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = highlightText(partner.discount, searchQuery, categoryColor),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = categoryColor,
                            maxLines = if (expanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    if (partner.id != "p116") {
                        Spacer(Modifier.height(6.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                null,
                                tint = Slate500,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                partner.address,
                                fontSize = 11.sp,
                                color = Slate500,
                                maxLines = if (expanded) Int.MAX_VALUE else 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Slate500
                    )
                }
            }

            if (expanded) {
                Column {
                    HorizontalDivider(color = Slate200.copy(alpha = 0.5f), thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PartnerActionButton(
                            icon = Icons.Default.Phone,
                            label = "Ligar",
                            color = categoryColor,
                            onClick = {
                                val clean = partner.phone.replace(Regex("[^0-9]"), "")
                                uriHandler.openUri("tel:$clean")
                            }
                        )

                        if (partner.whatsapp.isNotBlank()) {
                            PartnerActionButton(
                                icon = Icons.AutoMirrored.Filled.Chat,
                                label = "Whats",
                                color = WhatsAppGreen,
                                isWhatsApp = true,
                                onClick = {
                                    val clean = partner.whatsapp.replace(Regex("[^0-9]"), "")
                                    val msg = "Olá! Sou associado da Pax Rio Verde e gostaria de saber mais sobre os descontos."
                                    uriHandler.openUri("https://wa.me/55$clean?text=${urlEncode(msg)}")
                                }
                            )
                        }

                        if (partner.id != "p116") {
                            PartnerActionButton(
                                icon = Icons.Default.Map,
                                label = "Mapa",
                                color = Slate500,
                                isOutlined = true,
                                onClick = {
                                    uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=${urlEncode("${partner.name} ${partner.address}")}")
                                }
                            )
                        }
                        
                        PartnerActionButton(
                            icon = Icons.Default.Share,
                            label = "Enviar",
                            color = Slate500,
                            isOutlined = true,
                            onClick = {
                                val mapsUrl = "https://www.google.com/maps/search/?api=1&query=${urlEncode("${partner.name} ${partner.address}")}"
                                val shareMsg = """
                                    Olha esse desconto para os associados da Pax Rio Verde!
                                    
                                    🌟 *${partner.name}*
                                    🎁 *Desconto:* ${partner.discount.replace("\n", " ")}
                                    📍 *Endereço:* ${partner.address}
                                    🗺️ *Ver no mapa:* $mapsUrl
                                    
                                    Apresente seu cartão Pax Rio Verde e economize!
                                """.trimIndent()
                                shareText(shareMsg, "Compartilhar Parceiro")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PartnerActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    isOutlined: Boolean = false,
    isWhatsApp: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isOutlined) Color.Transparent else color,
        border = if (isOutlined) BorderStroke(1.dp, Slate200) else null,
        contentColor = if (isOutlined) Slate800 else Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isWhatsApp) {
                Icon(
                    painter = painterResource(Res.drawable.ic_whatsapp_social),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            } else {
                Icon(icon, null, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(6.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}


// ─────────────────────────────────────────
// EMPTY STATE
// ─────────────────────────────────────────
@Composable
fun BenefitsEmptyState(category: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("\uD83C\uDF3F", fontSize = 40.sp)
        Text(
            text = "Nenhum parceiro em \"$category\" ainda",
            fontSize = 15.sp, fontWeight = FontWeight.Bold,
            color = Slate800, textAlign = TextAlign.Center
        )
        Text(
            text = "Toque em \"Todos\" para ver os parceiros disponíveis.",
            fontSize = 13.sp, color = Slate500, textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────
// LEGAL DISCLAIMER
// ─────────────────────────────────────────
@Composable
fun BenefitsLegalDisclaimer() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Default.Info, null, tint = Slate500, modifier = Modifier.size(14.dp))
        Text(
            text = "Descontos sujeitos a alteração sem aviso prévio. Confirme as condições com o parceiro antes de utilizar.",
            fontSize = 12.sp, color = Slate500, lineHeight = 18.sp
        )
    }
}
