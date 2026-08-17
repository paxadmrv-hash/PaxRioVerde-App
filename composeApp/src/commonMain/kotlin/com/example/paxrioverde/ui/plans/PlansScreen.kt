package com.example.paxrioverde.ui.plans

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.viewmodel.koinViewModel
import com.example.paxrioverde.api.DependenteItem
import com.example.paxrioverde.domain.model.PlanStatus
import com.example.paxrioverde.util.urlEncode
import kotlinx.coroutines.delay
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
    valorMensalidade: String = "150,00",
    viewModel: PlansViewModel = koinViewModel()
) {
    val dependentList = viewModel.walletCache.dependentesList
    val uiState by viewModel.uiState.collectAsState()
    val isLoadingCache = viewModel.walletCache.isPreloading
    val isLoadingStatus = uiState.isLoading
    val isLoading = isLoadingCache || isLoadingStatus

    var showAccessModal by remember { mutableStateOf(false) }
    var selectedDependent by remember { mutableStateOf<DependenteItem?>(null) }
    
    LaunchedEffect(idcliente) {
        viewModel.loadData(idcliente)
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
                BentoCardHero(userPlano, valorMensalidade, uiState.planStatus)
            }

            item {
                SectionTitleWidget("O que está incluso")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    IncludedItemCard(
                        title = "Assistência Funerária",
                        desc = "A PAX RIO VERDE oferece assistência funerária 24h e serviços conforme o plano contratado.",
                        expandableContent = "O plano cobre urna mortuária; higienização e acondicionamento do ente querido; enfeites e acessórios; translado e carro funerário dentro do limite do plano e itens de recepção (café, chá, copos). Assistência funerária 24 horas.",
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
                        ExcludedItemRow("Roupas para o seu ente querido")
                        HorizontalDivider(color = SoftGrayBg, modifier = Modifier.padding(vertical = 8.dp))
                        ExcludedItemRow("Reconstituição Facial")
                    }
                }
            }

            item {
                SectionTitleWidget("Dependentes Cadastrados")
                Text(
                    text = "Abaixo, os dependentes ativos do seu plano:",
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

            items(
                items = dependentList,
                key = { it.nomeDependente ?: it.hashCode() }
            ) { dependent ->
                DependentSoftCard(
                    dependent = dependent,
                    onGrantAccess = {
                        selectedDependent = dependent
                        showAccessModal = true
                    }
                )
            }
        }

        if (showAccessModal && selectedDependent != null) {
            AccessModal(
                idcliente = idcliente,
                dependent = selectedDependent!!,
                onDismiss = { showAccessModal = false },
                onConfirm = { id, cpf, nome, callback ->
                    viewModel.atualizarCpfDependente(id, cpf, nome, callback)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessModal(
    idcliente: Int,
    dependent: DependenteItem,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String, (Boolean, String) -> Unit) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var cpf by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isSuccess) {
                val isPolicyError = isError && message.contains("2 anos", ignoreCase = true)

                // CABEÇALHO INSTRUTIVO
                Text(
                    text = "Liberar entrada de",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = dependent.nomeDependente ?: "Dependente",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isPolicyError) {
                    // UI SENIOR: Em erro de política, removemos o formulário para focar na solução
                    PolicyErrorView(
                        message = message,
                        onContactSupport = {
                            val msg = "Olá, gostaria de atualizar o CPF do meu dependente ${dependent.nomeDependente}, mas o app informa o prazo de 2 anos."
                            uriHandler.openUri("https://wa.me/556436214500?text=${urlEncode(msg)}")
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("VOLTAR", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        color = PrimaryLimeGreen.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Security,
                                null,
                                tint = PrimaryLimeGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Vincule o CPF do seu dependente para liberar o acesso individual e seguro.",
                                fontSize = 15.sp,
                                color = TextDark,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // CAMPO CPF GRANDE E CLARO
                    OutlinedTextField(
                        value = cpf,
                        onValueChange = { if (it.length <= 11) cpf = it.filter { c -> c.isDigit() } },
                        label = { Text("CPF do Dependente", fontSize = 16.sp) },
                        placeholder = { Text("000.000.000-00") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = CpfVisualTransformation(),
                        singleLine = true,
                        isError = isError,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryLimeGreen,
                            focusedLabelColor = PrimaryLimeGreen,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )

                    if (isError) {
                        Text(
                            text = message,
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Verifique o número com atenção antes de confirmar.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // BOTÃO DE CONFIRMAÇÃO MAXIMIZADO
                    Button(
                        onClick = {
                            if (cpf.length == 11) {
                                isLoading = true
                                isError = false
                                onConfirm(idcliente, cpf, dependent.nomeDependente ?: "") { success, msg ->
                                    isLoading = false
                                    message = msg
                                    if (success) {
                                        isSuccess = true
                                        scope.launch {
                                            delay(10000)
                                            onDismiss()
                                        }
                                    } else {
                                        isError = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryLimeGreen,
                            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp,
                            disabledElevation = 0.dp
                        ),
                        enabled = cpf.length == 11 && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "LIBERAR ACESSO AGORA",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "CANCELAR",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // ESTADO DE SUCESSO VISUAL "CHECK-IN MODERNO"
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(100.dp)
                    ) {
                        // Círculo de pulso em background
                        Surface(
                            shape = CircleShape,
                            color = PrimaryLimeGreen.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxSize()
                        ) {}
                        
                        Icon(
                            Icons.Default.Verified,
                            null,
                            tint = PrimaryLimeGreen,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Verificação Concluída",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "IDENTIDADE VINCULADA",
                            color = PrimaryLimeGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Text(
                        text = buildAnnotatedString {
                            append("O acesso para ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(dependent.nomeDependente ?: "seu dependente")
                            }
                            append(" foi configurado. Agora, peça para ele(a) baixar o app e clicar em ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = PrimaryLimeGreen)) {
                                append("'PRIMEIRO ACESSO'")
                            }
                            append(" para criar uma senha.")
                        },
                        fontSize = 15.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 20.dp).padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val msg = "Olá! Já liberei seu acesso no aplicativo da Pax Rio Verde. Agora é só baixar o app e clicar em 'PRIMEIRO ACESSO' para criar sua senha e entrar!"
                            uriHandler.openUri("https://wa.me/?text=${urlEncode(msg)}")
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // Cor oficial WhatsApp
                    ) {
                        Icon(Icons.Default.Share, null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("AVISAR PELO WHATSAPP", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Feedback de proteção/confiança
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.alpha(0.6f)
                    ) {
                        Icon(
                            Icons.Default.Lock, 
                            null, 
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Protegido por PAX Security",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PolicyErrorView(
    message: String,
    onContactSupport: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)), // Amber 50
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFFFD54F)), // Amber 300
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFFF57C00), // Orange 700
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Aviso de Política",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFE65100)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = message,
                fontSize = 14.sp,
                color = TextDark.copy(alpha = 0.8f),
                lineHeight = 20.sp,
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = onContactSupport,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.SupportAgent, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("FALAR COM O ESCRITÓRIO", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// FORMATADOR DE CPF (REUTILIZADO)
class CpfVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 11) text.text.substring(0..10) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 2 || i == 5) out += "."
            if (i == 8) out += "-"
        }

        val cpfOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 8) return offset + 2
                if (offset <= 11) return offset + 3
                return 14
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 11) return offset - 2
                if (offset <= 14) return offset - 3
                return 11
            }
        }
        return TransformedText(AnnotatedString(out), cpfOffsetTranslator)
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
fun BentoCardHero(userPlano: String, valorMensalidade: String, planStatus: PlanStatus) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
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
            Text(
                text = "Mensalidade: R$ $valorMensalidade",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = planStatus.label.uppercase(),
                    color = planStatus.color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun IncludedItemCard(
    title: String,
    desc: String,
    icon: ImageVector,
    expandableContent: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .then(
                if (expandableContent != null) Modifier.clickable { expanded = !expanded }
                else Modifier
            )
            .padding(16.dp)
            .animateContentSize()
    ) {
        Row(
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

                if (expandableContent != null && !expanded) {
                    Text(
                        text = "Saiba mais",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLimeGreen,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = PrimaryLimeGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        if (expanded && expandableContent != null) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SoftGrayBg)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = expandableContent,
                fontSize = 12.sp,
                color = TextDark,
                lineHeight = 16.sp
            )
        }
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
fun DependentSoftCard(
    dependent: DependenteItem,
    onGrantAccess: () -> Unit
) {
    val nome = dependent.nomeDependente ?: "Dependente"
    val hasCpf = !dependent.cpf.isNullOrBlank() && 
                dependent.cpf.filter { it.isDigit() }.length >= 11

    // Senior UI: Card dinâmico que muda de estilo conforme o status
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .animateContentSize() // Animação de entrada do conteúdo extra
            // Borda lateral verde suave para liberados
            .then(
                if (hasCpf) Modifier.border(
                    BorderStroke(2.dp, PrimaryLimeGreen.copy(alpha = 0.2f)),
                    RoundedCornerShape(20.dp)
                ) else Modifier
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar com iniciais e animação de cor
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (hasCpf) PrimaryLimeGreen.copy(alpha = 0.1f) else SoftGrayBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nome.firstOrNull()?.toString() ?: "?",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = if (hasCpf) PrimaryLimeGreen else TextDark
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
                
                // Exibição de CPF Mascarado
                if (hasCpf) {
                    val rawCpf = dependent.cpf!!.filter { it.isDigit() }
                    if (rawCpf.length >= 9) {
                        val maskedCpf = "***.${rawCpf.substring(3, 6)}.${rawCpf.substring(6, 9)}-**"
                        Text(
                            text = "CPF: $maskedCpf",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Badge de Parentesco
                    Surface(
                        color = if (hasCpf) PrimaryLimeGreen.copy(alpha = 0.05f) else Color.LightGray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = (dependent.parentesco ?: "Dependente").uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasCpf) PrimaryLimeGreen.copy(alpha = 0.8f) else Color.Gray,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge de Status com cor vibrante para sucesso
                    Surface(
                        color = if (hasCpf) PrimaryLimeGreen else Color.Gray.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp),
                        shadowElevation = if (hasCpf) 2.dp else 0.dp
                    ) {
                        Text(
                            text = if (hasCpf) "LIBERADO" else "PENDENTE",
                            color = if (hasCpf) Color.White else Color.Gray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            
            // Checkmark Icon Animado (Aparece apenas quando liberado)
            if (hasCpf) {
                Icon(
                    Icons.Default.Verified,
                    null,
                    tint = PrimaryLimeGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        if (!hasCpf) {
            Spacer(modifier = Modifier.height(16.dp))
            // BOTÃO DE ACESSO PARA IDOSOS
            Button(
                onClick = onGrantAccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceWhite,
                    contentColor = PrimaryLimeGreen
                ),
                border = BorderStroke(1.5.dp, PrimaryLimeGreen.copy(alpha = 0.4f)),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Icon(
                    Icons.Outlined.AppRegistration,
                    null,
                    tint = PrimaryLimeGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "LIBERAR ACESSO",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
