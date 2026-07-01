package com.example.paxrioverde.ui.virtualcard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paxrioverde.api.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.*

val WalletDarkBg = Color(0xFF13211c)
val BrandLightGreen = Color(0xFF6fad2b)
val WalletCardBg = Color(0xFF1E1E1E)
val TextWhite = Color(0xFFE3E3E3)
val TextGray = Color(0xFF8E8E93)
val ExpiredRed = Color(0xFFFF5252)

// Mapa para lembrar o estilo escolhido pelo usuário nesta sessão
private val cardStyleOverrides = mutableStateMapOf<Int, String>()

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
    onCardGenerated: () -> Unit = {},
    onNavigateToFinance: () -> Unit = {},
    viewModel: VirtualCardViewModel = viewModel { VirtualCardViewModel() }
) {
    val cartoesList = WalletCache.cartoesList
    val isPreloading = WalletCache.isPreloading
    
    val uiState by viewModel.uiState.collectAsState()

    var showGerarDialog by remember { mutableStateOf(false) }
    var expandedCard by remember { mutableStateOf<CartaoItem?>(null) }

    LaunchedEffect(idcliente) {
        if (idcliente != 0) {
            WalletCache.preLoad(idcliente)
        }
    }

    val pagerState = rememberPagerState(pageCount = { cartoesList.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = WalletDarkBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = TextWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Carteira", fontSize = 24.sp, color = TextWhite)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Área do Cartão com design estilo Carrossel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SEUS CARTÕES",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Deslize para ver todos os seus cartões",
                    color = TextGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (isPreloading && cartoesList.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BrandLightGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Buscando cartões...", color = TextWhite, fontSize = 14.sp)
                    }
                } else if (cartoesList.isEmpty()) {
                    Text("Nenhum cartão ativo.", color = TextGray)
                } else {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 48.dp),
                        pageSpacing = 16.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val item = cartoesList[page]
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                ).absoluteValue

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.586f)
                                .clickable { expandedCard = item }
                                .graphicsLayer {
                                    val scale = lerp(1f, 0.85f, pageOffset.coerceIn(0f, 1f))
                                    scaleX = scale
                                    scaleY = scale
                                    alpha = lerp(1f, 0.5f, pageOffset.coerceIn(0f, 1f))
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Design do Cartão com Imagem Local
                                CardContent(item = item)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Indicadores (Dots) estilo o anexo
                    Row(
                        modifier = Modifier.height(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(cartoesList.size) { iteration ->
                            val isSelected = pagerState.currentPage == iteration
                            val color = if (isSelected) BrandLightGreen else Color.DarkGray
                            val size = if (isSelected) 8.dp else 6.dp
                            
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                                    .size(size)
                            )
                        }
                    }
                }
            }

            // Lista de cartões com peso para ocupar o resto da tela
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(WalletCardBg)
                    .padding(24.dp)
            ) {
                Button(
                    onClick = { showGerarDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandLightGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Gerar Novo Cartão", fontWeight = FontWeight.Bold)
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
                    itemsIndexed(cartoesList) { index, card ->
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

    if (showGerarDialog) {
        GerarCartaoDialog(
            onDismiss = { 
                showGerarDialog = false
                viewModel.resetState()
            },
            idcliente = idcliente,
            idcontrato = idcontrato,
            idconvenio = idconvenio,
            idmensalidade = idmensalidade,
            idcaixa = idcaixa,
            idfilial = idfilial,
            dtvencimento = dtvencimento,
            valorCartao = valorCartao,
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
fun CardContent(item: CartaoItem) {
    // Verifica se temos um estilo salvo localmente para este ID, senão usa o tipo do backend
    val style = cardStyleOverrides[item.idControle] ?: item.tipo
    val lowerTipo = style.lowercase()
    val isKids = lowerTipo.contains("kids")
    val isTeen = lowerTipo.contains("teen")

    val parentesco = if (item.dep == "S") {
        WalletCache.dependentesList.find { it.nomeDependente == item.nomeDependente }?.parentesco ?: "DEPENDENTE"
    } else {
        "TITULAR"
    }

    val backgroundResource = when {
        isKids -> Res.drawable.card_kids
        isTeen -> Res.drawable.card_teen
        else -> Res.drawable.card_titular
    }

    // Valores otimizados para descer as informações e liberar o fundo
    val paddingHoriz = if (isKids) 32.dp else 26.dp
    val paddingVert = if (isKids) 12.dp else 10.dp
    val nomeSize = if (isKids) 11.sp else 13.sp
    val infoSize = if (isKids) 9.sp else 11.sp

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

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
                Text(
                    text = (if (item.dep == "S") item.nomeDependente ?: "" else item.nomeCliente).uppercase(),
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.586f)
                ) {
                    CardContent(item = item)
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
                    color = BrandLightGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandLightGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(200.dp).height(50.dp)
                ) {
                    Text("FECHAR", fontWeight = FontWeight.Bold)
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
    idcontrato: Int,
    idconvenio: Int,
    idmensalidade: Int,
    idcaixa: Int,
    idfilial: Int,
    dtvencimento: String,
    valorCartao: String? = null,
    onSuccess: () -> Unit = {},
    onNavigateToFinance: () -> Unit = {},
    viewModel: VirtualCardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var isTitular by remember { mutableStateOf(true) }
    val dependentesList = WalletCache.dependentesList
    var selectedDependente by remember { mutableStateOf<DependenteItem?>(null) }
    var expanded by remember { mutableStateOf(false) }
    
    val estilos = listOf("Adulto", "Teen", "Kids")
    var selectedEstilo by remember { mutableStateOf(estilos[0]) }
    var expandedEstilo by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    val valorFormatado = remember(valorCartao) {
        val valor = if (valorCartao.isNullOrEmpty() || valorCartao == "0,00" || valorCartao == "0.00") "5,00" else valorCartao
        if (valor.contains("R$")) valor else "R$ $valor"
    }

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
                        Text("Gerar Novo Cartão", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BrandLightGreen, modifier = Modifier.padding(bottom = 16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isTitular = true }) {
                            RadioButton(selected = isTitular, onClick = { isTitular = true }, colors = RadioButtonDefaults.colors(selectedColor = BrandLightGreen))
                            Text("Titular", color = Color.Black)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isTitular = false }) {
                            RadioButton(selected = !isTitular, onClick = { isTitular = false }, colors = RadioButtonDefaults.colors(selectedColor = BrandLightGreen))
                            Text("Dependente", color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!isTitular) {
                            Text("Estilo do Cartão", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                            ExposedDropdownMenuBox(expanded = expandedEstilo, onExpandedChange = { expandedEstilo = !expandedEstilo }) {
                                OutlinedTextField(
                                    value = selectedEstilo,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstilo) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandLightGreen, unfocusedBorderColor = Color.LightGray, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
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
                            Spacer(modifier = Modifier.height(16.dp))
                            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                                OutlinedTextField(
                                    value = selectedDependente?.nomeDependente ?: "Selecione o dependente",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandLightGreen, unfocusedBorderColor = Color.LightGray, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
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
                            LaunchedEffect(Unit) { selectedEstilo = "Adulto" }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Custo Adicional: $valorFormatado", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { 
                                if (isTitular || selectedDependente != null) {
                                    viewModel.gerarCartaoPix(
                                        idcaixa = idcaixa,
                                        idcliente = idcliente,
                                        tipo = if (isTitular) "titular" else "dependente",
                                        nomeDependente = if (isTitular) "" else selectedDependente?.nomeDependente ?: "",
                                        estiloSelecionado = selectedEstilo
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandLightGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("GERAR COM PIX", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = onDismiss) { Text("Fechar", color = Color.Gray) }
                    }

                    is VirtualCardState.Loading -> {
                        CircularProgressIndicator(color = BrandLightGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Processando...", color = Color.Black)
                    }

                    is VirtualCardState.PixGenerated -> {
                        Text("Pagamento PIX", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandLightGreen)
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
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(state.pixCode))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandLightGreen)
                        ) {
                            Text("COPIAR CÓDIGO")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aguardando pagamento...", color = Color.Gray, fontSize = 12.sp)
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), color = BrandLightGreen)
                        TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Red) }
                    }

                    is VirtualCardState.Success -> {
                        Icon(Icons.Default.CheckCircle, null, tint = BrandLightGreen, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sucesso!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BrandLightGreen)
                        Text("Cartão gerado com sucesso.", color = Color.Black)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        state.newCard?.let { card ->
                            // Update style override if needed
                            LaunchedEffect(card.idControle) {
                                cardStyleOverrides[card.idControle] = selectedEstilo
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
                            colors = ButtonDefaults.buttonColors(containerColor = BrandLightGreen)
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
                            colors = ButtonDefaults.buttonColors(containerColor = BrandLightGreen)
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isExpired) Icons.Default.Warning else Icons.Default.CreditCard,
                contentDescription = null,
                tint = if (isExpired) ExpiredRed else BrandLightGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(if (item.dep == "S") item.nomeDependente ?: "" else item.nomeCliente, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Estilo: ${item.tipo}", color = TextGray, fontSize = 12.sp)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text("Validade", color = TextGray, fontSize = 10.sp)
            Text(
                text = item.dtValidade,
                color = if (isExpired) ExpiredRed else TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
