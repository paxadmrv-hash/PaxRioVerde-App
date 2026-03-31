package com.example.paxrioverde.ui.pet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.example.paxrioverde.api.PetItem
import com.example.paxrioverde.util.SessionManager
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.pet_photo_placeholder
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

val BrandGreen = Color(0xFF386641)
val BrandGreenDark = Color(0xFF254D2E)
val LightBg = Color(0xFFF5F5F5)
val TextDark = Color(0xFF1F2937)

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
        name = "Clínica Veterinária Rio Verde",
        discount = "• 10% à vista (pix ou dinheiro), 5% no débito e 3% no crédito NAS CONSULTAS E CIRURGIAS\n\n• 7% à vista (pix ou dinheiro), 5% no débito e 3% no crédito NAS MEDICAÇÕES.",
        address = "Rua Rio Verde N°240 - Vila Maria",
        phone = "(64) 99255-4900",
        icon = Icons.Default.LocalHospital
    )
)

val petBenefits = listOf(PetBenefit("Urna Pet", "Incluso no Plano (1 por Plano).", Icons.Default.Inventory2))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MundoPetScreen(onBack: () -> Unit, idcliente: Int, idcontrato: Int, idconvenio: Int) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sessionManager = remember { SessionManager() }

    var petsList by remember { mutableStateOf<List<PetItem>>(emptyList()) }
    var selectedPetIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    var showPetDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editBreed by remember { mutableStateOf("") }
    var editBirthDate by remember { mutableStateOf("") }
    var editPhotoBase64 by remember { mutableStateOf("") }

    fun loadPetsLocally() {
        try {
            val json = sessionManager.getSavedPetsJson()
            if (json.isNotEmpty()) {
                petsList = Json.decodeFromString<List<PetItem>>(json)
            }
        } catch (e: Exception) {
            println("PET_LOG: Erro ao carregar JSON: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    fun savePetsLocally(newList: List<PetItem>) {
        try {
            val json = Json.encodeToString(newList)
            sessionManager.savePetsJson(json)
            petsList = newList
        } catch (e: Exception) {
            println("PET_LOG: Erro ao salvar JSON: ${e.message}")
        }
    }

    LaunchedEffect(Unit) { loadPetsLocally() }

    val imagePicker = rememberImagePickerLauncher { bytes ->
        if (bytes != null) {
            editPhotoBase64 = bytes.toBase64()
        }
    }

    if (showPetDialog) {
        AlertDialog(
            onDismissRequest = { if (!isSaving) showPetDialog = false },
            title = { Text(if (isEditing) "Editar Pet" else "Cadastrar Novo Pet", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray).clickable(enabled = !isSaving) { imagePicker.launch() }.align(Alignment.CenterHorizontally)) {
                        if (editPhotoBase64.isNotEmpty()) {
                            AsyncImageWrapper(
                                uri = if (editPhotoBase64.startsWith("http")) editPhotoBase64 else "data:image/jpeg;base64,$editPhotoBase64",
                                placeholder = Res.drawable.pet_photo_placeholder,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.align(Alignment.Center), tint = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Nome") }, enabled = !isSaving, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editBreed, onValueChange = { editBreed = it }, label = { Text("Raça") }, enabled = !isSaving, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBirthDate,
                        onValueChange = { input -> editBirthDate = input.filter { it.isDigit() }.take(8) },
                        label = { Text("Nascimento (DD/MM/AAAA)") },
                        enabled = !isSaving,
                        visualTransformation = DateVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editBirthDate.length < 8) {
                            scope.launch { snackbarHostState.showSnackbar("Data incompleta") }
                            return@Button
                        }

                        val day = editBirthDate.substring(0, 2).toIntOrNull() ?: 0
                        val month = editBirthDate.substring(2, 4).toIntOrNull() ?: 0
                        val year = editBirthDate.substring(4, 8).toIntOrNull() ?: 0

                        if (!isValidDate(day, month, year)) {
                            scope.launch { snackbarHostState.showSnackbar("Data de nascimento inválida") }
                            return@Button
                        }

                        isSaving = true
                        val formattedDate = "${editBirthDate.substring(0,2)}/${editBirthDate.substring(2,4)}/${editBirthDate.substring(4,8)}"
                        val petData = PetItem(
                            idpet = if (isEditing) petsList[selectedPetIndex].idpet else (petsList.size + 1).toString(),
                            nome = editName, raca = editBreed, dtnascimento = formattedDate, foto = editPhotoBase64
                        )
                        val updatedList = if (isEditing) petsList.map { if (it.idpet == petData.idpet) petData else it } else petsList + petData
                        savePetsLocally(updatedList)
                        isSaving = false; showPetDialog = false
                        scope.launch { snackbarHostState.showSnackbar("Pet salvo com sucesso!") }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen), enabled = !isSaving
                ) {
                    if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Salvar")
                }
            },
            dismissButton = { TextButton(onClick = { showPetDialog = false }, enabled = !isSaving) { Text("Cancelar", color = Color.Gray) } },
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = LightBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(colors = listOf(BrandGreen, BrandGreenDark)),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                isEditing = false
                                editName = ""
                                editBreed = ""
                                editBirthDate = ""
                                editPhotoBase64 = ""
                                showPetDialog = true
                            },
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

                    Spacer(modifier = Modifier.height(20.dp))

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
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandGreen) }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                if (petsList.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(24.dp).clickable {
                            isEditing = false; editName = ""; editBreed = ""; editBirthDate = ""; editPhotoBase64 = ""; showPetDialog = true
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(BrandGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(40.dp), tint = BrandGreen)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Adicionar meu Pet", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text("Toque aqui para cadastrar seu companheiro!", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    val currentPet = petsList.getOrNull(selectedPetIndex)
                    if (currentPet != null) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            PetProfileCard(
                                pet = currentPet, pets = petsList, selectedIndex = selectedPetIndex,
                                onSelect = { selectedPetIndex = it },
                                onEdit = {
                                    isEditing = true; editName = currentPet.nome ?: ""; editBreed = currentPet.raca ?: ""
                                    editBirthDate = currentPet.dtnascimento?.replace("/", "") ?: ""; editPhotoBase64 = currentPet.foto ?: ""; showPetDialog = true
                                }
                            )
                            PlanCoverageCard(benefits = petBenefits)
                            Text(text = "Parceiros & Descontos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
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
                            Surface(shape = RoundedCornerShape(50), color = if (selectedIndex == index) BrandGreen else Color(0xFFF0F0F0)) {
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
                    Text(pet.nome ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Edit, null, tint = BrandGreen, modifier = Modifier.size(18.dp))
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
            Text("Nascimento", fontWeight = FontWeight.Bold, color = BrandGreen, fontSize = 14.sp)
            Text(pet.dtnascimento ?: "--/--/----", fontSize = 14.sp, color = Color.Gray)
            Text("$ageYears anos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        Box(modifier = Modifier.height(50.dp).width(1.dp).background(Color.LightGray))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Idade Humana", fontWeight = FontWeight.Bold, color = BrandGreen, fontSize = 14.sp)
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
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(BrandGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(partner.icon, null, tint = BrandGreen, modifier = Modifier.size(24.dp)) }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(partner.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
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
                    Icon(Icons.Default.Phone, null, tint = BrandGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ligar: ${partner.phone}", fontSize = 14.sp, color = BrandGreen, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFE8F5E9)).clickable { val clean = partner.phone.replace(Regex("[^0-9]"), ""); try { uriHandler.openUri("https://api.whatsapp.com/send?phone=55$clean") } catch (_: Exception) {} }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Chat, null, tint = BrandGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("WhatsApp: ${partner.phone}", fontSize = 14.sp, color = BrandGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PlanCoverageCard(benefits: List<PetBenefit>) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Cobertura do Plano", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(bottom = 12.dp))
            benefits.forEach { benefit ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(BrandGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(benefit.icon, null, tint = BrandGreen, modifier = Modifier.size(20.dp)) }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(benefit.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        Text(benefit.description, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
expect fun AsyncImageWrapper(uri: String?, placeholder: org.jetbrains.compose.resources.DrawableResource, modifier: Modifier)
