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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.paxrioverde.api.AnoItem
import com.example.paxrioverde.api.ApiService
import com.example.paxrioverde.api.MensalidadeItem
import com.example.paxrioverde.ui.notifications.NotificationCenter
import com.example.paxrioverde.ui.notifications.NotificationType
import com.example.paxrioverde.util.AppConstants
import kotlinx.coroutines.launch

// CORES
val BrandGreen = Color(0xFF386641)
val BrandGreenDark = Color(0xFF1B5E20)
val SurfaceWhite = Color(0xFFFFFFFF)
val BackgroundGray = Color(0xFFF0F2F5)
val TextPrimary = Color(0xFF101820)
val TextSecondary = Color(0xFF606C76)

@Composable
fun FinanceScreen(
    onBackClick: () -> Unit,
    idcliente: Int,
    idcaixa: Int = 0,
    valorProxMens: String = "0,00",
    vencProxMens: String = "--/--/----",
    showBoletoButton: Boolean = true
) {
    var selectedMensalidade by remember { mutableStateOf<MensalidadeItem?>(null) }
    
    var anosData by remember { mutableStateOf<List<AnoItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(idcliente) {
        if (idcliente != 0) {
            try {
                val response = ApiService.getMensalidades(idcliente)
                if (response.success) {
                    anosData = response.anos ?: emptyList()
                } else {
                    errorMessage = response.message ?: "Erro ao carregar faturas"
                }
            } catch (e: Exception) {
                errorMessage = "Erro de conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    val years = remember(anosData) { anosData.map { it.ano }.distinct().sortedDescending() }
    var selectedYear by remember(years) { mutableStateOf(years.firstOrNull() ?: 2024) }
    
    val historyInvoices = remember(selectedYear, anosData) {
        anosData.find { it.ano == selectedYear }?.mensalidades ?: emptyList()
    }

    var showPixDialog by remember { mutableStateOf(false) }
    var pixCode by remember { mutableStateOf("") }
    var isGeneratingPayment by remember { mutableStateOf(false) }
    
    var showBoletoDialog by remember { mutableStateOf(false) }
    var barCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .navigationBarsPadding()
    ) {
        FinanceHeader(
            onBackClick = onBackClick,
            valor = selectedMensalidade?.valormensalidade ?: valorProxMens,
            vencimento = selectedMensalidade?.dtvencimento ?: vencProxMens,
            onPixClick = { 
                val mens = selectedMensalidade ?: historyInvoices.firstOrNull { !it.pago }
                if (mens != null) {
                    scope.launch {
                        isGeneratingPayment = true
                        try {
                            // Usando o novo método gerarPix com todos os parâmetros necessários
                            val pixResponse = ApiService.gerarPix(
                                idcaixa = idcaixa,
                                idcontrato = mens.idcontrato,
                                idconvenio = mens.idconvenio,
                                dtvencimento = mens.dtvencimento,
                                idmensalidade = mens.idmensalidade
                            )
                            pixCode = pixResponse.pixCode
                            showPixDialog = true
                            
                            // Notificação de geração de pagamento
                            NotificationCenter.addNotification(
                                title = "PIX Gerado",
                                message = "O código para a mensalidade de R$ ${mens.valormensalidade} foi gerado com sucesso.",
                                type = NotificationType.PAYMENT
                            )
                        } catch (e: Exception) {
                            errorMessage = "Erro ao gerar PIX: ${e.message}"
                        } finally {
                            isGeneratingPayment = false
                        }
                    }
                }
            },
            onBoletoClick = { 
                val mens = selectedMensalidade ?: historyInvoices.firstOrNull { !it.pago }
                if (mens != null) {
                    scope.launch {
                        isGeneratingPayment = true
                        try {
                            val boletoResponse = ApiService.getBoleto(
                                idcontrato = mens.idcontrato,
                                idconvenio = mens.idconvenio,
                                idmensalidade = mens.idmensalidade
                            )
                            if (boletoResponse.success) {
                                barCode = boletoResponse.codigoBarra ?: "Boleto disponível no PDF"
                                showBoletoDialog = true
                                
                                // Notificação de geração de boleto
                                NotificationCenter.addNotification(
                                    title = "Boleto Gerado",
                                    message = "A linha digitável da mensalidade de R$ ${mens.valormensalidade} já está disponível.",
                                    type = NotificationType.PAYMENT
                                )
                            } else {
                                errorMessage = boletoResponse.mesAno ?: "Erro ao carregar boleto"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Erro ao carregar boleto: ${e.message}"
                        } finally {
                            isGeneratingPayment = false
                        }
                    }
                }
            },
            showBoleto = selectedMensalidade?.boleto ?: showBoletoButton,
            isProcessing = isGeneratingPayment
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
                    Button(onClick = { 
                        isLoading = true
                        errorMessage = null
                        // Tentar recarregar
                    }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Tentar Novamente")
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    HistoryHeader(
                        years = years,
                        selectedYear = selectedYear,
                        onYearSelected = { selectedYear = it }
                    )
                }

                items(historyInvoices) { item ->
                    HistoryInvoiceItem(
                        item = item,
                        isSelected = selectedMensalidade?.idmensalidade == item.idmensalidade,
                        onClick = {
                            if (!item.pago) {
                                selectedMensalidade = item
                            }
                        }
                    )
                }
            }
        }
    }

    if (showPixDialog) {
        PixDialog(pixCode = pixCode, onDismiss = { showPixDialog = false })
    }

    if (showBoletoDialog) {
        BoletoDialog(
            barCode = barCode,
            onDismiss = { showBoletoDialog = false }
        )
    }
}

@Composable
fun FinanceHeader(
    onBackClick: () -> Unit, 
    valor: String, 
    vencimento: String, 
    onPixClick: () -> Unit,
    onBoletoClick: () -> Unit,
    showBoleto: Boolean,
    isProcessing: Boolean
) {
    Box(
        modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(BrandGreen, BrandGreenDark))).clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
    ) {
        Column(modifier = Modifier.statusBarsPadding().padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)) {
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp).size(48.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = SurfaceWhite)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Mensalidade Selecionada", color = SurfaceWhite.copy(alpha = 0.8f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "R$ $valor", color = SurfaceWhite, fontSize = 38.sp, fontWeight = FontWeight.Bold)
            Text(text = "Vencimento: $vencimento", color = SurfaceWhite.copy(alpha = 0.9f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(32.dp))

            if (isProcessing) {
                Box(modifier = Modifier.fillMaxWidth().height(110.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    PaymentActionCard(
                        icon = Icons.Outlined.QrCode2, 
                        title = "Pagar com Pix", 
                        modifier = Modifier.weight(1f), 
                        onClick = onPixClick
                    )
                    if (showBoleto) {
                        PaymentActionCard(
                            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                            title = "Boleto", 
                            modifier = Modifier.weight(1f), 
                            onClick = onBoletoClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryHeader(years: List<Int>, selectedYear: Int, onYearSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.History, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Histórico", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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
        Row(modifier = Modifier.clip(RoundedCornerShape(50)).background(SurfaceWhite).border(1.dp, BrandGreen.copy(alpha = 0.2f), RoundedCornerShape(50)).clickable { expanded = true }.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = selectedYear.toString(), color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Selecionar ano", tint = BrandGreen, modifier = Modifier.size(20.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(SurfaceWhite)) {
            years.forEach { year ->
                DropdownMenuItem(text = { Text(text = year.toString()) }, onClick = { onYearSelected(year); expanded = false })
            }
        }
    }
}

@Composable
fun HistoryInvoiceItem(item: MensalidadeItem, isSelected: Boolean, onClick: () -> Unit) {
    val statusColor = if (item.pago) BrandGreen else if (isSelected) BrandGreen else Color(0xFFD32F2F)
    
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) BrandGreen.copy(alpha = 0.05f) else SurfaceWhite),
        border = if (isSelected) BorderStroke(2.dp, BrandGreen) else null,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (item.pago) Icons.Default.CheckCircle else Icons.Default.PendingActions, 
                        contentDescription = null, 
                        tint = statusColor, 
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Mensalidade", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Text(text = if (item.pago) "Pago em ${item.dtpagamento}" else "Vencimento ${item.dtvencimento}", fontSize = 12.sp, color = TextSecondary)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "R$ ${item.valormensalidade}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                if (!item.pago) {
                    Text(text = "PENDENTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                }
            }
        }
    }
}

@Composable
fun PaymentActionCard(icon: ImageVector, title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), modifier = modifier.height(100.dp).clickable { onClick() }) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun PixDialog(pixCode: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.QrCodeScanner, null, tint = BrandGreen, modifier = Modifier.size(64.dp))
                Text("PIX Gerado!", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                Text("Copie o código abaixo para pagar no seu banco.", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Surface(color = BackgroundGray, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                    Text(pixCode, fontSize = 11.sp, modifier = Modifier.padding(12.dp), maxLines = 5)
                }
                Button(onClick = { /* Lógica de copiar para o clipboard pode ser adicionada */ }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)) {
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
fun BoletoDialog(barCode: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Description, null, tint = BrandGreen, modifier = Modifier.size(64.dp))
                Text("Boleto Disponível", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                Surface(color = BackgroundGray, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                    Text(barCode, fontSize = 12.sp, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
                }
                Button(onClick = { /* Lógica de copiar */ }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)) {
                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Copiar linha digitável")
                }
                TextButton(onClick = onDismiss) { Text("Fechar", color = Color.Gray) }
            }
        }
    }
}
