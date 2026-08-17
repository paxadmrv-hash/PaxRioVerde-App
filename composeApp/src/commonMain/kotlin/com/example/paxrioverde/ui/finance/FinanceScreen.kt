package com.example.paxrioverde.ui.finance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import com.example.paxrioverde.api.MensalidadeItem
import com.example.paxrioverde.ui.components.*
import com.example.paxrioverde.ui.theme.PaxDesignSystem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.*

private fun parseDate(dateStr: String): LocalDate? {
    return try {
        val parts = dateStr.split("/")
        if (parts.size == 3) {
            LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
        } else null
    } catch (_: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    onBackClick: () -> Unit,
    idcliente: Int,
    idcaixa: Int = 0,
    valorProxMens: String = "0,00",
    vencProxMens: String = "--/--/----",
    showBoletoButton: Boolean = true,
    viewModel: FinanceViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current
    val fontScale = LocalDensity.current.fontScale

    LaunchedEffect(idcliente) {
        viewModel.loadMensalidades(idcliente)
    }

    val years = remember(uiState.anosData) { viewModel.getYears() }
    val historyInvoices = remember(uiState.selectedYear, uiState.anosData, uiState.oldestUnpaid) { 
        viewModel.getHistoryInvoices() 
    }

    val totalValor = remember(uiState.selectedMensalidade, uiState.oldestUnpaid, valorProxMens) {
        uiState.selectedMensalidade?.valormensalidade ?: uiState.oldestUnpaid?.valormensalidade ?: valorProxMens
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PaxDesignSystem.Colors.Background
    ) { padding ->
        var isRefreshing by remember { mutableStateOf(false) }
        val pullToRefreshState = rememberPullToRefreshState()

        LaunchedEffect(uiState.isLoading) {
            if (!uiState.isLoading) {
                scope.launch {
                    delay(500L)
                    isRefreshing = false
                }
            }
        }

        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadMensalidades(idcliente)
            },
            modifier = Modifier.fillMaxSize(),
            indicator = {
                Box(modifier = Modifier.fillMaxWidth().statusBarsPadding(), contentAlignment = Alignment.TopCenter) {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        containerColor = Color.White,
                        color = PaxDesignSystem.Colors.BrandGreen
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                PaxScreenHeader(onBackClick = onBackClick) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mensalidade Selecionada",
                        color = PaxDesignSystem.Colors.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "R$ $totalValor",
                        color = PaxDesignSystem.Colors.White,
                        fontSize = if (fontScale > 1.3) 28.sp else 38.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Vencimento: ${uiState.selectedMensalidade?.dtvencimento ?: uiState.oldestUnpaid?.dtvencimento ?: vencProxMens}",
                        color = PaxDesignSystem.Colors.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    if (uiState.isGeneratingPayment) {
                        Box(modifier = Modifier.fillMaxWidth().height(110.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PaxDesignSystem.Colors.White)
                        }
                    } else {
                        if (fontScale > 1.3) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                PaymentActionCard(
                                    icon = Icons.Outlined.QrCode2, 
                                    title = "Pagar com Pix", 
                                    modifier = Modifier.fillMaxWidth().bounceClick(), 
                                    onClick = { 
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.gerarPix(idcaixa, totalValor) {} 
                                    }
                                )
                                val showBoleto = uiState.selectedMensalidade?.boleto ?: uiState.oldestUnpaid?.boleto ?: showBoletoButton
                                if (showBoleto) {
                                    PaymentActionCard(
                                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                                        title = "Boleto Bancário", 
                                        modifier = Modifier.fillMaxWidth().bounceClick(), 
                                        onClick = { 
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.gerarBoleto(totalValor) {} 
                                        }
                                    )
                                }
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                                PaymentActionCard(
                                    icon = Icons.Outlined.QrCode2, 
                                    title = "Pagar com Pix", 
                                    modifier = Modifier.weight(1f).bounceClick(), 
                                    onClick = { 
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.gerarPix(idcaixa, totalValor) {} 
                                    }
                                )
                                val showBoleto = uiState.selectedMensalidade?.boleto ?: uiState.oldestUnpaid?.boleto ?: showBoletoButton
                                if (showBoleto) {
                                    PaymentActionCard(
                                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                                        title = "Boleto", 
                                        modifier = Modifier.weight(1f).bounceClick(), 
                                        onClick = { 
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.gerarBoleto(totalValor) {} 
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        DiscountBanner(fontScale)
                    }
                }

                if (uiState.isLoading && !isRefreshing) {
                    LazyColumn(
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(5) { HistoryInvoiceSkeleton() }
                    }
                } else if (uiState.errorMessage != null) {
                    PaxErrorView(message = uiState.errorMessage!!, onRetry = { viewModel.loadMensalidades(idcliente) })
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            HistoryHeader(
                                years = years,
                                selectedYear = uiState.selectedYear,
                                onYearSelected = { viewModel.selectYear(it) }
                            )
                        }

                        items(historyInvoices) { item ->
                            val unpaidList = historyInvoices.filter { !it.pago }.sortedBy { parseDate(item.dtvencimento) }
                            val isLocked = unpaidList.size >= 2 && item.idmensalidade != unpaidList[0].idmensalidade
                            val isFirstUnpaid = unpaidList.take(1).any { it.idmensalidade == item.idmensalidade }
                            
                            val canSelect = item.pago || isFirstUnpaid

                            HistoryInvoiceItem(
                                item = item,
                                isSelected = uiState.selectedMensalidade?.idmensalidade == item.idmensalidade,
                                enabled = canSelect,
                                isLocked = isLocked,
                                onClick = {
                                    if (!item.pago && canSelect) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.selectMensalidade(item)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    val pixCode = uiState.pixCode
    if (uiState.showPixDialog && pixCode != null) {
        PixDialog(
            pixCode = pixCode, 
            onDismiss = { viewModel.dismissPixDialog() },
            onCopy = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                scope.launch { snackbarHostState.showSnackbar("Código PIX copiado!") }
            }
        )
    }

    val barCode = uiState.barCode
    if (uiState.showBoletoDialog && barCode != null) {
        BoletoDialog(
            barCode = barCode,
            onDismiss = { viewModel.dismissBoletoDialog() },
            onCopy = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                scope.launch { snackbarHostState.showSnackbar("Linha digitável copiada!") }
            }
        )
    }
}

@Composable
private fun DiscountBanner(fontScale: Float) {
    Surface(
        color = Color.White.copy(alpha = 0.2f), // Aumentando levemente a opacidade para destaque
        shape = PaxDesignSystem.Shapes.Button,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = if (fontScale > 1.3) 8.dp else 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = PaxDesignSystem.Colors.White,
                modifier = Modifier.size(if (fontScale > 1.3) 16.dp else 20.dp) // Ícone maior
            )
            Spacer(modifier = Modifier.width(if (fontScale > 1.3) 6.dp else 10.dp))
            Text(
                text = "Pague até o vencimento e ganhe 5% de desconto na mensalidade.",
                color = PaxDesignSystem.Colors.White,
                fontSize = if (fontScale > 1.3) 12.sp else 14.sp, // Fonte maior (padrão Senior de legibilidade)
                fontWeight = FontWeight.ExtraBold, // Peso extra para destaque
                textAlign = TextAlign.Center,
                lineHeight = if (fontScale > 1.3) 16.sp else 18.sp
            )
        }
    }
}

@Composable
fun HistoryHeader(years: List<Int>, selectedYear: Int, onYearSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.History, null, tint = PaxDesignSystem.Colors.TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Histórico", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.TextPrimary)
        }
        if (years.isNotEmpty()) {
            YearDropdownSelector(years = years, selectedYear = selectedYear, onYearSelected = onYearSelected)
        }
    }
}

@Composable
fun YearDropdownSelector(years: List<Int>, selectedYear: Int, onYearSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(modifier = Modifier.clip(RoundedCornerShape(50)).background(PaxDesignSystem.Colors.Surface).border(1.dp, PaxDesignSystem.Colors.BrandGreen.copy(alpha = 0.2f), RoundedCornerShape(50)).clickable { expanded = true }.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = selectedYear.toString(), color = PaxDesignSystem.Colors.BrandGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Selecionar ano", tint = PaxDesignSystem.Colors.BrandGreen, modifier = Modifier.size(20.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(PaxDesignSystem.Colors.Surface)) {
            years.forEach { year ->
                DropdownMenuItem(text = { Text(text = year.toString()) }, onClick = { onYearSelected(year); expanded = false })
            }
        }
    }
}

@Composable
fun HistoryInvoiceSkeleton() {
    Card(
        shape = PaxDesignSystem.Shapes.Medium,
        colors = CardDefaults.cardColors(containerColor = PaxDesignSystem.Colors.Surface),
        modifier = Modifier.fillMaxWidth().height(80.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).shimmerEffect())
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.width(120.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.width(80.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            }
            Box(modifier = Modifier.width(60.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        }
    }
}

@Composable
fun HistoryInvoiceItem(
    item: MensalidadeItem, 
    isSelected: Boolean, 
    enabled: Boolean = true,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    val statusColor = if (item.pago) PaxDesignSystem.Colors.Success else if (isLocked) Color.Gray else if (isSelected) PaxDesignSystem.Colors.BrandGreen else if (enabled) PaxDesignSystem.Colors.Error else Color.Gray
    
    Card(
        shape = PaxDesignSystem.Shapes.Medium,
        colors = CardDefaults.cardColors(containerColor = if (isSelected) PaxDesignSystem.Colors.BrandGreen.copy(alpha = 0.05f) else PaxDesignSystem.Colors.Surface),
        border = if (isSelected) BorderStroke(2.dp, PaxDesignSystem.Colors.BrandGreen) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = (enabled || item.pago) && !isLocked) { onClick() }
            .alpha(if (!item.pago && (!enabled || isLocked)) 0.5f else 1f)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (item.pago) Icons.Default.CheckCircle else if (isLocked) Icons.Default.Lock else if (enabled) Icons.Default.PendingActions else Icons.Default.Lock, 
                            contentDescription = null, 
                            tint = statusColor, 
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Mensalidade", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PaxDesignSystem.Colors.TextPrimary)
                        Text(text = if (item.pago) "Pago em ${item.dtpagamento}" else "Vencimento ${item.dtvencimento}", fontSize = 12.sp, color = PaxDesignSystem.Colors.TextSecondary)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "R$ ${item.valormensalidade}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PaxDesignSystem.Colors.TextPrimary)
                    if (!item.pago) {
                        Text(
                            text = if (isLocked) "BLOQUEADO" else if (enabled) "PENDENTE" else "BLOQUEADO", 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = statusColor
                        )
                    }
                }
            }
            
            if (isLocked) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info, 
                        contentDescription = null, 
                        tint = PaxDesignSystem.Colors.TextSecondary, 
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Será liberada após o pagamento da pendente",
                        fontSize = 11.sp,
                        color = PaxDesignSystem.Colors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentActionCard(
    icon: ImageVector, 
    title: String, 
    modifier: Modifier = Modifier, 
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp), 
        colors = CardDefaults.cardColors(containerColor = PaxDesignSystem.Colors.Surface), 
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), 
        modifier = modifier.height(110.dp).clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp), 
            verticalArrangement = Arrangement.Center, 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PaxDesignSystem.Colors.BrandGreen, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.TextPrimary, textAlign = TextAlign.Center)
            if (subtitle != null) {
                Text(
                    text = subtitle, 
                    fontSize = 10.sp, 
                    color = PaxDesignSystem.Colors.BrandGreen, 
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PixDialog(pixCode: String, onDismiss: () -> Unit, onCopy: () -> Unit) {
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = PaxDesignSystem.Shapes.Card, colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.QrCodeScanner, null, tint = PaxDesignSystem.Colors.BrandGreen, modifier = Modifier.size(64.dp))
                Text("PIX Gerado!", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                Text("Copie o código abaixo para pagar no seu banco.", fontSize = 13.sp, color = PaxDesignSystem.Colors.TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Surface(color = PaxDesignSystem.Colors.Background, shape = PaxDesignSystem.Shapes.Small, modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                    Text(pixCode, fontSize = 11.sp, modifier = Modifier.padding(12.dp), maxLines = 5)
                }
                Button(
                    onClick = { 
                        clipboardManager.setText(AnnotatedString(pixCode))
                        onCopy()
                    }, 
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = PaxDesignSystem.Colors.BrandGreen)
                ) {
                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Copiar código PIX")
                }
                TextButton(onClick = onDismiss) { Text("Fechar", color = Color.Gray) }
            }
        }
    }
}

@Composable
fun BoletoDialog(barCode: String, onDismiss: () -> Unit, onCopy: () -> Unit) {
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = PaxDesignSystem.Shapes.Card, colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Description, null, tint = PaxDesignSystem.Colors.BrandGreen, modifier = Modifier.size(64.dp))
                Text("Boleto Disponível", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                Surface(color = PaxDesignSystem.Colors.Background, shape = PaxDesignSystem.Shapes.Small, modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                    Text(barCode, fontSize = 12.sp, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
                }
                Button(
                    onClick = { 
                        clipboardManager.setText(AnnotatedString(barCode))
                        onCopy()
                    }, 
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = PaxDesignSystem.Colors.BrandGreen)
                ) {
                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Copiar linha digitável")
                }
                TextButton(onClick = onDismiss) { Text("Fechar", color = Color.Gray) }
            }
        }
    }
}
