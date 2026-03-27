package com.example.paxrioverde.ui.virtualcard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VirtualCardScreen(onBack: () -> Unit, idcliente: Int = 0) {
    val cartoesList = WalletCache.cartoesList
    val isPreloading = WalletCache.isPreloading
    
    var showGerarDialog by remember { mutableStateOf(false) }
    var expandedCard by remember { mutableStateOf<CartaoItem?>(null) }

    LaunchedEffect(idcliente) {
        if (idcliente != 0) {
            WalletCache.preLoad(idcliente)
        }
    }

    val pagerState = rememberPagerState(pageCount = { cartoesList.size })

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
            // Área do Cartão com altura fixa proporcional
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f),
                contentAlignment = Alignment.Center
            ) {
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
                        contentPadding = PaddingValues(horizontal = 32.dp),
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
                    items(cartoesList) { card ->
                        ActiveCardRow(item = card)
                    }
                }
            }
        }
    }

    if (showGerarDialog) {
        GerarCartaoDialog(onDismiss = { showGerarDialog = false }, idcliente = idcliente)
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
    val isKids = item.tipo.contains("KIDS", ignoreCase = true)
    
    val parentesco = if (item.dep == "S") {
        WalletCache.dependentesList.find { it.nomeDependente == item.nomeDependente }?.parentesco ?: "DEPENDENTE"
    } else {
        "TITULAR"
    }

    val backgroundResource = when {
        isKids -> Res.drawable.card_kids
        item.tipo.contains("TEEN", ignoreCase = true) -> Res.drawable.card_teen
        else -> Res.drawable.card_titular
    }

    // Valores dinâmicos para evitar sobreposição no Kids e melhorar legibilidade
    val paddingHoriz = if (isKids) 28.dp else 22.dp
    val paddingVert = if (isKids) 22.dp else 18.dp
    val nomeSize = if (isKids) 12.sp else 14.sp
    val infoSize = if (isKids) 10.sp else 12.sp

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
                .padding(horizontal = paddingHoriz, vertical = paddingVert),
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
                    color = Color.Black,
                    fontSize = nomeSize,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = parentesco ?: "",
                    color = Color.Black,
                    fontSize = infoSize,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(if (isKids) 2.dp else 4.dp))

            // Linha 2: Contrato e Validade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Contrato: ${item.idContrato ?: ""}",
                    color = Color.Black,
                    fontSize = infoSize,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = item.dtValidade,
                    color = Color.Black,
                    fontSize = if (isKids) 12.sp else 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
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
fun GerarCartaoDialog(onDismiss: () -> Unit, idcliente: Int) {
    var isTitular by remember { mutableStateOf(true) }
    val dependentesList = WalletCache.dependentesList
    var selectedDependente by remember { mutableStateOf<DependenteItem?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    var showConfirmacao by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { 
                showResultDialog = false
                if (isSuccess) onDismiss()
            },
            title = { Text(if (isSuccess) "Sucesso" else "Atenção", fontWeight = FontWeight.Bold) },
            text = { Text(resultMessage) },
            confirmButton = {
                Button(onClick = { 
                    showResultDialog = false
                    if (isSuccess) onDismiss()
                }) { Text("OK") }
            }
        )
    }

    if (showConfirmacao) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) showConfirmacao = false },
            title = { Text("Confirmação", fontWeight = FontWeight.Bold) },
            text = { Text("Deseja gerar um novo cartão?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                val tipo = if (isTitular) "titular" else "dependente"
                                val nomeDep = if (isTitular) "" else selectedDependente?.nomeDependente ?: ""
                                val response = ApiService.gerarCartao(idcliente, tipo, nomeDep)
                                
                                isSuccess = response.success
                                resultMessage = response.message
                                if (isSuccess) WalletCache.preLoad(idcliente, forceRefresh = true)
                                showConfirmacao = false
                                showResultDialog = true
                            } catch (e: Exception) {
                                isSuccess = false
                                resultMessage = "Erro de conexão."
                                showConfirmacao = false
                                showResultDialog = true
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("Sim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmacao = false }, enabled = !isLoading) { Text("Não") }
            }
        )
    }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Gerar Novo Cartão", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BrandLightGreen, modifier = Modifier.padding(bottom = 16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isTitular = true }) {
                    RadioButton(selected = isTitular, onClick = { isTitular = true })
                    Text("Titular", color = Color.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isTitular = false }) {
                    RadioButton(selected = !isTitular, onClick = { isTitular = false })
                    Text("Dependente", color = Color.Black)
                }

                if (!isTitular) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Gray, RoundedCornerShape(8.dp)).clickable { expanded = true }.padding(12.dp)) {
                        Text(selectedDependente?.nomeDependente ?: "Selecionar Dependente", color = Color.Black)
                    }
                    if (expanded) {
                        Dialog(onDismissRequest = { expanded = false }) {
                            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                LazyColumn(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                    if (dependentesList.isEmpty()) {
                                        item { Text("Nenhum dependente encontrado", color = Color.Gray, modifier = Modifier.padding(8.dp)) }
                                    } else {
                                        items(dependentesList) { dep ->
                                            Text(dep.nomeDependente ?: "Sem Nome", color = Color.Black, modifier = Modifier.fillMaxWidth().clickable {
                                                selectedDependente = dep
                                                expanded = false
                                            }.padding(12.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { 
                        if (!isTitular && selectedDependente == null) {
                            // Poderia mostrar um Toast aqui
                        } else {
                            showConfirmacao = true 
                        }
                    }, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandLightGreen)
                ) {
                    Text("GERAR CARTÃO", color = Color.White)
                }
                TextButton(onClick = onDismiss) { Text("Fechar", color = Color.Gray) }
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
