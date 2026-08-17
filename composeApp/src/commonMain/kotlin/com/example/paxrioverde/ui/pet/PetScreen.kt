package com.example.paxrioverde.ui.pet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paxrioverde.api.PetItem
import com.example.paxrioverde.ui.components.*
import com.example.paxrioverde.ui.theme.PaxDesignSystem
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.pet_photo_placeholder

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) out += "/"
        }
        val dateOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }
        return TransformedText(AnnotatedString(out), dateOffsetTranslator)
    }
}

data class PetBenefit(val title: String, val description: String, val icon: ImageVector)
data class PetPartner(val name: String, val discount: String, val address: String, val phone: String, val icon: ImageVector)

val petPartners = listOf(
    PetPartner(
        name = "CLÍNICA VETERINÁRIA RIO VERDE",
        discount = "• 10% de desconto à vista (dinheiro ou pix), 5% (débito) e 3% (crédito) em consulta e cirurgia;\n\n• 7% de desconto à vista (dinheiro ou pix) 5% (débito) e 3% (crédito) em medicações.",
        address = "Rua Rio Verde N°240 - Vila Maria",
        phone = "(64) 99255-4900",
        icon = Icons.Default.LocalHospital
    ),
    PetPartner(
        name = "CLÍNICA VETERINÁRIA AGRO RAÇA",
        discount = "• 5% de desconto no valor total.",
        address = "Rio Verde - GO",
        phone = "(64) 99204-2313",
        icon = Icons.Default.Pets
    )
)

val petBenefits = listOf(PetBenefit("Urna Pet", "Incluso no Plano (1 por Plano).", Icons.Default.Inventory2))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MundoPetScreen(
    onBack: () -> Unit,
    idcliente: Int,
    idcontrato: Int,
    idconvenio: Int,
    viewModel: PetViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(idcliente) {
        viewModel.loadPets(idcliente)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val imagePicker = rememberImagePickerLauncher { bytes ->
        if (bytes != null) {
            // Senior Note: Compressão imediata para evitar overhead de memória com Strings Base64 gigantes
            val compressed = compressImage(bytes, maxWidth = 800, maxHeight = 800)
            viewModel.onEditPhotoChange(compressed.toBase64())
        }
    }

    if (uiState.showPetDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDialog() },
            title = { Text(if (uiState.isEditing) "Editar Pet" else "Cadastrar Novo Pet", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray).clickable(enabled = !uiState.isSaving) { imagePicker.launch() }.align(Alignment.CenterHorizontally)) {
                        if (uiState.editPhotoBase64.isNotEmpty()) {
                            AsyncImageWrapper(
                                uri = if (uiState.editPhotoBase64.startsWith("http")) uiState.editPhotoBase64 else "data:image/jpeg;base64,${uiState.editPhotoBase64}",
                                placeholder = Res.drawable.pet_photo_placeholder,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.align(Alignment.Center), tint = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    PaxTextField(value = uiState.editName, onValueChange = { viewModel.onEditNameChange(it) }, label = "Nome", enabled = !uiState.isSaving)
                    Spacer(modifier = Modifier.height(8.dp))
                    PaxTextField(value = uiState.editBreed, onValueChange = { viewModel.onEditBreedChange(it) }, label = "Raça", enabled = !uiState.isSaving)
                    Spacer(modifier = Modifier.height(8.dp))
                    PaxTextField(
                        value = uiState.editBirthDate,
                        onValueChange = { viewModel.onEditBirthDateChange(it) },
                        label = "Nascimento (DD/MM/AAAA)",
                        enabled = !uiState.isSaving,
                        visualTransformation = DateVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                PaxButton(
                    text = "Salvar",
                    onClick = { viewModel.savePet(idcliente, idcontrato, idconvenio) },
                    isLoading = uiState.isSaving,
                    modifier = Modifier.padding(start = 8.dp) // Removida largura fixa de 100.dp
                )
            },
            dismissButton = { TextButton(onClick = { viewModel.onDismissDialog() }, enabled = !uiState.isSaving) { Text("Cancelar", color = Color.Gray) } },
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = PaxDesignSystem.Colors.Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PaxScreenHeader(onBackClick = onBack) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mundo Pet",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Cuidado e carinho para o seu melhor amigo!",
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    IconButton(
                        onClick = { viewModel.onShowDialog(false) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar Pet",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PaxDesignSystem.Colors.BrandGreen) }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                if (uiState.petsList.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(24.dp).clickable {
                            viewModel.onShowDialog(false)
                        },
                        shape = PaxDesignSystem.Shapes.Card,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(PaxDesignSystem.Colors.BrandGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(40.dp), tint = PaxDesignSystem.Colors.BrandGreen)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Adicionar meu Pet", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.TextDark)
                            Text("Toque aqui para cadastrar seu companheiro!", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    val currentPet = uiState.petsList.getOrNull(uiState.selectedPetIndex)
                    if (currentPet != null) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            PetProfileCard(
                                pet = currentPet, 
                                pets = uiState.petsList, 
                                selectedIndex = uiState.selectedPetIndex,
                                onSelect = { viewModel.onSelectPet(it) },
                                onEdit = { viewModel.onShowDialog(true, currentPet) }
                            )
                            PlanCoverageCard(benefits = petBenefits)
                            Text(text = "Parceiros & Descontos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.TextDark)
                        }
                        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            petPartners.forEach { partner -> PetPartnerCard(partner) }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seus dados estão protegidos pela LGPD.", fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PetProfileCard(pet: PetItem, pets: List<PetItem>, selectedIndex: Int, onSelect: (Int) -> Unit, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            ScrollableTabRow(selectedTabIndex = selectedIndex, edgePadding = 0.dp, containerColor = Color.Transparent, divider = {}, indicator = {}) {
                pets.forEachIndexed { index, p ->
                    Tab(
                        selected = selectedIndex == index, onClick = { onSelect(index) },
                        text = {
                            Surface(shape = RoundedCornerShape(50), color = if (selectedIndex == index) PaxDesignSystem.Colors.BrandGreen else Color(0xFFF0F0F0)) {
                                Text(p.nome ?: "Pet", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), color = if (selectedIndex == index) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.LightGray).clickable { onEdit() }) {
                AsyncImageWrapper(
                    uri = if (pet.foto?.startsWith("http") == true) pet.foto!! else if (pet.foto.isNullOrEmpty()) "" else "data:image/jpeg;base64,${pet.foto}",
                    placeholder = Res.drawable.pet_photo_placeholder, modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Edit, null, tint = Color.White) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onEdit() }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(pet.nome ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.TextDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Edit, null, tint = PaxDesignSystem.Colors.BrandGreen, modifier = Modifier.size(18.dp))
                }
                Text("${pet.raca ?: ""}", fontSize = 16.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(24.dp))
            DynamicAgeCalculator(pet = pet)
        }
    }
}

@Composable
fun DynamicAgeCalculator(pet: PetItem) {
    val (ageYears, ageMonths) = getAgeFromDate(pet.dtnascimento ?: "")
    val humanAge = calculateHumanAge("Cão", "Médio", ageYears, ageMonths)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Nascimento", fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.BrandGreen, fontSize = 14.sp)
            Text(pet.dtnascimento ?: "--/--/----", fontSize = 14.sp, color = Color.Gray)
            Text("$ageYears anos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        Box(modifier = Modifier.height(50.dp).width(1.dp).background(Color.LightGray))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Idade Humana", fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.BrandGreen, fontSize = 14.sp)
            Text("(Estimada)", fontSize = 12.sp, color = Color.Gray)
            Text("~ $humanAge anos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun PetPartnerCard(partner: PetPartner) {
    val uriHandler = LocalUriHandler.current
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(PaxDesignSystem.Colors.BrandGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(partner.icon, null, tint = PaxDesignSystem.Colors.BrandGreen, modifier = Modifier.size(24.dp)) }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(partner.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PaxDesignSystem.Colors.TextDark)
                    Text(text = partner.discount, color = Color.DarkGray, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFE3F2FD)).clickable { try { uriHandler.openUri("geo:0,0?q=${partner.address}") } catch (_: Exception) {} }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFF1565C0), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(partner.address, fontSize = 12.sp, color = Color(0xFF1565C0))
                }
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFE8F5E9)).clickable { try { uriHandler.openUri("tel:${partner.phone.replace(Regex("[^0-9]"), "")}") } catch (_: Exception) {} }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, null, tint = PaxDesignSystem.Colors.BrandGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ligar: ${partner.phone}", fontSize = 14.sp, color = PaxDesignSystem.Colors.BrandGreen, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFE8F5E9)).clickable { val clean = partner.phone.replace(Regex("[^0-9]"), ""); try { uriHandler.openUri("https://api.whatsapp.com/send?phone=55$clean") } catch (_: Exception) {} }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Chat, null, tint = PaxDesignSystem.Colors.BrandGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("WhatsApp: ${partner.phone}", fontSize = 14.sp, color = PaxDesignSystem.Colors.BrandGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PlanCoverageCard(benefits: List<PetBenefit>) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Cobertura do Plano", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PaxDesignSystem.Colors.TextDark, modifier = Modifier.padding(bottom = 12.dp))
            benefits.forEach { benefit ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(PaxDesignSystem.Colors.BrandGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(benefit.icon, null, tint = PaxDesignSystem.Colors.BrandGreen, modifier = Modifier.size(20.dp)) }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(benefit.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PaxDesignSystem.Colors.TextDark)
                        Text(benefit.description, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
