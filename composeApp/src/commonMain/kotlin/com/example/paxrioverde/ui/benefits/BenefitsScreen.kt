package com.example.paxrioverde.ui.benefits

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.util.urlEncode
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.ic_whatsapp_social

// ─────────────────────────────────────────────
// DESIGN TOKENS
// ─────────────────────────────────────────────
private val Forest800 = Color(0xFF1E3A2F)
private val Forest600 = Color(0xFF386641)
private val Forest50  = Color(0xFFF0F7F1)
private val Amber500  = Color(0xFFD97706)
private val Amber50   = Color(0xFFFFFBEB)
private val Slate800  = Color(0xFF1E293B)
private val Slate500  = Color(0xFF64748B)
private val Slate200  = Color(0xFFE2E8F0)
private val CardWhite = Color(0xFFFFFFFF)
private val WhatsAppGreen = Color(0xFF25D366)

// ─────────────────────────────────────────────
// MODELO DE DADOS
// ─────────────────────────────────────────────
data class Partner(
    val name: String,
    val discount: String,
    val address: String,
    val phone: String,
    val whatsapp: String = "",
    val icon: ImageVector,
    val category: String,
    val city: String = "Rio Verde"
)

data class BenefitsCategory(
    val label: String,
    val icon: ImageVector
)

// ─────────────────────────────────────────────
// CATEGORIAS
// ─────────────────────────────────────────────
val benefitsCategories = listOf(
    BenefitsCategory("Todos",        Icons.Default.GridView),
    BenefitsCategory("ABA",          Icons.Default.Extension),
    BenefitsCategory("Saude",        Icons.Default.HealthAndSafety),
    BenefitsCategory("Farmacias",    Icons.Default.MedicalServices),
    BenefitsCategory("Laboratorios", Icons.Default.Science),
    BenefitsCategory("Oticas",       Icons.Default.RemoveRedEye),
    BenefitsCategory("Clinicas",     Icons.Default.LocalHospital),
    BenefitsCategory("Psicologia",   Icons.Default.Psychology),
    BenefitsCategory("Fisioterapia", Icons.Default.SelfImprovement),
    BenefitsCategory("Educacao",     Icons.Default.School),
    BenefitsCategory("Alimentacao",  Icons.Default.Restaurant),
    BenefitsCategory("lazer",       Icons.Default.Movie),
    BenefitsCategory("Estetica",     Icons.Default.Spa),
    BenefitsCategory("Floricultura", Icons.Default.LocalFlorist),
    BenefitsCategory("Roupas",       Icons.Default.Checkroom),
    BenefitsCategory("Fitness",      Icons.Default.FitnessCenter),
    BenefitsCategory("Gás",       Icons.Default.Category),
)

// ─────────────────────────────────────────────
// PARCEIROS REAIS
// ─────────────────────────────────────────────
val realPartners: List<Partner> = listOf(
    // TERAPIA ABA
    Partner("Clínica Bambinos Espaço Terapêutico Infantil", "Descontos Especiais em Terapias\nAtendimento: Fonoaudiologia ABA, Psicologia ABA, T.O. e Psico-pedagogia",
        "Rua Abel Pereira de Castro N° 1351 - Setor Central, Rio Verde",
        "6484037105", "6484037105", Icons.Default.Extension, "ABA"),
    Partner("Clínica LeveMente", "35% em consultas / 25% em pacotes de terapias\nAtendimento: Psicologia ABA, Fonoaudiologia ABA, Neuropsicologia, Nutrição ABA e convencional, e Psico-pedagogia",
        "Rua Quinca Honorio N° 118 - Morada do Sol, Rio Verde",
        "6484037105", "6484037105", Icons.Default.Extension, "ABA"),

    // ACADEMIA
    Partner("Atletica Academia", "15% a vista na mensalidade",
        "R. Jeronimo Martins, 536 - Pq. Bandeirantes, Rio Verde",
        "6498112-8792", "6498112-8792", Icons.Default.FitnessCenter, "Fitness"),

    // ALIMENTACAO
    Partner("Montana Grill", "Ate 15% no cardapio",
        "Buriti Shopping, Rio Verde",
        "6499906-0990", "6499906-0990", Icons.Default.Restaurant, "Alimentacao"),
    Partner("Picanha's Bar e Restaurante", "15% no rodizio",
        "R. Sebastiao Freitas da Silva, 48 - Vila Amalia, Rio Verde",
        "6430510033", "6499271-1184", Icons.Default.Restaurant, "Alimentacao"),
    Partner("Quero Acai - Centro", "10% no cardapio (exceto promocoes)",
        "R. Major Oscar Campos, 511 - Centro, Rio Verde",
        "6436213860", "", Icons.Default.Restaurant, "Alimentacao"),
    Partner("Quero Acai - Morada do Sol", "10% no cardapio (exceto promocoes)",
        "R. Rosolino Campos, 637 - Morada do Sol, Rio Verde",
        "6436133375", "6499225-8879", Icons.Default.Restaurant, "Alimentacao"),

    // ACUPUNTURA
    Partner("Dr. Washington Fabio Pacheco", "R\$90,00 por sessao",
        "R. Rui Barbosa, 1261 - Centro (Clinica Solares), Rio Verde",
        "556484037105", "", Icons.Default.HealthAndSafety, "Saude"),

    // LAZER
    Partner("Cine A", "Ingresso por R\$10,00",
        "Av. Presidente Vargas, 1470 - Jardim Goias (Shopping Rio Verde), Rio Verde",
        "6436124216", "", Icons.Default.Movie, "Cinema"),

    // LAZER
    Partner("Alp Jump", "15% nas sessoes e 20% nas meias",
        "R. Honorio Leao, Qd. 77, Lt. 05 - Setor Morada do Sol, Rio Verde",
        "6499232-3052", "6499232-3052", Icons.Default.SportsEsports, "Outros"),

    // CURSOS
    Partner("Instituto Voce", "20% nos treinamentos",
        "R. 55, 1326 - Setor Aeroporto, Rio Verde",
        "6239425456", "6299962-6522", Icons.Default.School, "Educacao"),
    Partner("Excellent Global", "40% nas mensalidades",
        "R. Almiro de Moraes, 554 - Setor Central, Rio Verde",
        "6430510015", "6499995-8013", Icons.Default.School, "Educacao"),
    Partner("Unidombosco - Cursos EAD", "20% nas mensalidades EAD",
        "Rio Verde - GO",
        "6436021693", "", Icons.Default.School, "Educacao"),
    Partner("Centro Universitario UNIBRAS / AESGO", "10% nas mensalidades",
        "R. Doze de Outubro, 42 - Jardim Adriana, Rio Verde",
        "6435129932", "", Icons.Default.School, "Educacao"),

    // FARMACIAS
    Partner("A Terapeutica Farmacia de Manipulacao", "20% a vista (exceto industrializados)",
        "R. Nizo Jaime de Gusmao, 1226 - Centro, Rio Verde",
        "6436131313", "", Icons.Default.MedicalServices, "Farmacias"),
    Partner("Droga Nunes", "17% originais / 40% genericos / 15% perfumaria",
        "R. 06, esq. 05, 159 - Bairro Promissao, Rio Verde",
        "6498406-0533", "6498406-0533", Icons.Default.MedicalServices, "Farmacias"),
    Partner("Drogaria Saude Center", "13% eticos / 15% demais departamentos",
        "Av. Presidente Vargas, 310 - Setor Central, Rio Verde",
        "6498406-0533", "", Icons.Default.MedicalServices, "Farmacias"),
    Partner("Drogaria Uniao", "16% a vista em dinheiro (exceto promocoes)",
        "R. 11, 420 - Bairro Promissao, Rio Verde",
        "6436216161", "6498455-7398", Icons.Default.MedicalServices, "Farmacias"),
    Partner("Farmacia Artesanal", "25% de desconto",
        "Av. Barrinha, Qd. 03, Lt. 04, 536 - Jd. Bela Vista, Rio Verde",
        "6436121076", "6499937-2244", Icons.Default.MedicalServices, "Farmacias"),
    Partner("Drogaria Cristo Redentor", "Generico 20-80% / Eticos 20% / Perfumaria 20%",
        "R. Vitoria, Qd. 35, Lt. 32 - Dom Miguel, Rio Verde",
        "6436211062", "6498441-7929", Icons.Default.MedicalServices, "Farmacias"),

    // EQUIPAMENTOS HOSPITALARES
    Partner("Med Shop", "5% a 15% a vista",
        "R. Prof. Joaquim Pedro esq. R. Rui Barbosa, 55 - Centro, Rio Verde",
        "6436215155", "6498180-5156", Icons.Default.LocalHospital, "Saude"),
    Partner("R & T Equipamentos", "5% a 15% a vista",
        "R. Rosulino Ferreira Guimaraes, 800 - Centro, Rio Verde",
        "6436214896", "6498180-4896", Icons.Default.LocalHospital, "Saude"),
    Partner("LOCCENTER", "15% no aluguel de produtos hospitalares",
        "R. Augusta Bastos, 730 - Centro, Rio Verde",
        "6436133782", "6499277-9718", Icons.Default.LocalHospital, "Saude"),

    // ESCOLAS
    Partner("Colegio Educar", "30% nas mensalidades",
        "R. Doze de Outubro, 42 - Jardim Adriana, Rio Verde",
        "6436242638", "", Icons.Default.School, "Educacao"),

    // ESTETICA
    Partner("Solaris Conceito em Estetica", "15% a 70% a vista/pix/debito",
        "R. Rui Barbosa, 1261 - Centro, Rio Verde",
        "6432127473", "6432127473", Icons.Default.Spa, "Estetica"),

    // FISIOTERAPIA
    Partner("Andressa Pamplona Felipe", "10% nas sessoes (Fisioterapia Obstetrica)",
        "R. Nizo Jaime de Gusmao, 369, Rio Verde",
        "6499292-7760", "6499292-7760", Icons.Default.SelfImprovement, "Fisioterapia"),
    Partner("Cristiane Silva Viana - Fisiovita", "40% a vista",
        "R. 72, 500 - Bairro Popular, Rio Verde",
        "6436222978", "", Icons.Default.SelfImprovement, "Fisioterapia"),
    Partner("Thaynara Goncalves da Silva", "20% consultas e sessoes (Neurologica)",
        "Atendimento domiciliar, Rio Verde",
        "6499322-1446", "6499322-1446", Icons.Default.SelfImprovement, "Fisioterapia"),
    Partner("Eucimar Vieira - Clinica Fisio Rio", "40% a vista",
        "R. Ana Mota, 813 - Santo Agostinho, Rio Verde",
        "6436134954", "", Icons.Default.SelfImprovement, "Fisioterapia"),

    // FONOAUDIOLOGIA
    Partner("Larissa Carvalho Ferreira - Clinica Benesse", "Consulte valores com o parceiro",
        "R. Nivaldo Ribeiro, 72 (2 andar) - Setor Central, Rio Verde",
        "6436021010", "6499858289", Icons.Default.RecordVoiceOver, "Clinicas"),
    Partner("Clinica Bambinos - Fonoaudiologia", "Consulte valores com o parceiro",
        "R. Abel Pereira de Castro, 1351 - Setor Central, Rio Verde",
        "6499305-2884", "6499305-2884", Icons.Default.RecordVoiceOver, "Clinicas"),

    // FLORICULTURA
    Partner("Adonai Floricultura", "15% a vista",
        "R. Wolney da Costa Martins, 1374 - Residencial Veneza, Rio Verde",
        "6499226-5869", "6499226-5869", Icons.Default.LocalFlorist, "Floricultura"),
    Partner("Elli Flores", "10% a vista",
        "R. Major Oscar Campos, 435 - Jardim Marconal, Rio Verde",
        "6436130110", "6499675-0110", Icons.Default.LocalFlorist, "Floricultura"),

    // CLINICAS
    Partner("Clinica Otovive", "22% consultas / 9% a 50% exames",
        "R. Rosulino Ferreira Guimaraes, 840 - Centro, Rio Verde",
        "556484037105", "", Icons.Default.LocalHospital, "Clinicas"),
    Partner("Clinica Radiologica", "Descontos em exames de imagem",
        "R. Major Oscar Campos, 159 - Setor Central, Rio Verde",
        "556484037105", "", Icons.Default.LocalHospital, "Clinicas"),
    Partner("Day Medical Center", "Descontos em exames de imagem",
        "Alameda Barrinha, 1800 - Vila Modelo, Rio Verde",
        "6430515800", "", Icons.Default.LocalHospital, "Clinicas"),
    Partner("Hospital do Cancer de Rio Verde", "Desconto em Mamografia, Raio X, Ultrassom e Fisioterapia",
        "R. Tiradentes, 822 - Bairro Santo Agostinho, Rio Verde",
        "6436122400", "", Icons.Default.LocalHospital, "Clinicas"),
    Partner("MedSaude Clinica Odontologica", "Precos acessiveis para associados Pax",
        "Rio Verde - GO",
        "6436203110", "6499338-6180", Icons.Default.LocalHospital, "Clinicas"),

    // LABORATORIOS
    Partner("Laboratorio Rio Verde", "10% a 70% de desconto",
        "R. Joaquim Vaz do Nascimento, 154 - Centro (Pax Rio Verde), Rio Verde",
        "6436130831", "", Icons.Default.Science, "Laboratorios"),
    Partner("Laboratorio Labortest", "50% de desconto",
        "R. Rosulino Ferreira Guimaraes, 730 - Centro, Rio Verde",
        "6436111000", "6499116025", Icons.Default.Science, "Laboratorios"),

    // NUTRICIONISTAS
    Partner("Ana Carolina Muniz Ferreira", "35% na consulta (inclui bioimpedancia)",
        "R. Almiro de Moraes, 490 - Centro (Girassol Alimentos), Rio Verde",
        "6499328-9792", "6499328-9792", Icons.Default.HealthAndSafety, "Saude"),
    Partner("Mariana Batista P. Gomes - Nutricionista", "66% na consulta",
        "R. Nizo Jaime de Gusmao, 369 - Vila Amalia, Rio Verde",
        "6436027300", "", Icons.Default.HealthAndSafety, "Saude"),

    // OTICAS
    Partner("Otica Laiz - Centro", "30% a vista / credito / crediario",
        "R. Prof. Jeronimo Ferreira Sobrinho, 576 - Centro, Rio Verde",
        "6436235878", "", Icons.Default.RemoveRedEye, "Oticas"),
    Partner("Bella Otica", "35% a vista / debito / credito",
        "Av. Presidente Vargas, 271 - Centro, Rio Verde",
        "6436133296", "6498406-6287", Icons.Default.RemoveRedEye, "Oticas"),
    Partner("Otica 99", "15% em todas as lentes",
        "Av. Presidente Vargas, 444 - Jardim Goias, Rio Verde",
        "6499950-9990", "6499950-9990", Icons.Default.RemoveRedEye, "Oticas"),

    // PSICOLOGIA
    Partner("Alisson Luiz Quiste - Clinica Ativamente", "Dependencia Quimica / Saude Mental / TCC",
        "R. Pau-Brasil, Qd. 23, Lt. 553 - Res. Gameleira, Rio Verde",
        "6430513639", "", Icons.Default.Psychology, "Psicologia"),
    Partner("Mayra Miranda Castro - Clinica Ativamente", "TCC em Esquemas / Neuropsicologia",
        "R. Pau-Brasil, Qd. 23, Lt. 553 - Res. Gameleira, Rio Verde",
        "6430509977", "", Icons.Default.Psychology, "Psicologia"),
    Partner("Bruna Lima Dias - Psicanalise", "Terapia de Casal, Criancas e Adolescentes (online)",
        "Atendimento online",
        "6499261-8285", "6499261-8285", Icons.Default.Psychology, "Psicologia"),
    Partner("Daguima da Costa - Clinica Contato", "Gestalt",
        "R. Joaquim Fonseca, 256 - B. Odilia, Rio Verde",
        "6436234073", "", Icons.Default.Psychology, "Psicologia"),
    Partner("Luciana da Silva Cerqueira", "Psicologia Infantil / TCC",
        "Av. Presidente Vargas, 2223 - Shopping Pop Reis, Rio Verde",
        "6496477674", "6496477674", Icons.Default.Psychology, "Psicologia"),
    Partner("Fernanda Cristina de Brito - Contato", "TCC (35 a 55 anos)",
        "R. Joaquim Fonseca, 256 - Bairro Odilia, Rio Verde",
        "6436234073", "", Icons.Default.Psychology, "Psicologia"),
    Partner("Fyama Cabral - Clinica Integra", "Terapia de Casais / Transtornos Mentais",
        "R. Nizo Jaime de Gusmao, 521 - Bairro Maristela, Rio Verde",
        "6499224-1228", "6499224-1228", Icons.Default.Psychology, "Psicologia"),
    Partner("Isabela Gonzaga Ferreira - Contato", "Gestalt",
        "R. Joaquim Fonseca, 256 - Bairro Odilia, Rio Verde",
        "6436234073", "", Icons.Default.Psychology, "Psicologia"),
    Partner("Jaqueline Paiva Souto Lima", "Psicologia Clinica / TCC",
        "R. Joaquim Fonseca, 256 - Bairro Odilia, Rio Verde",
        "6499237-4725", "6499237-4725", Icons.Default.Psychology, "Psicologia"),
    Partner("Synara Carvalho Branquinho - Contato", "Gestalt",
        "R. Joaquim Fonseca, 256 - Bairro Odilia, Rio Verde",
        "6436234073", "", Icons.Default.Psychology, "Psicologia"),
    Partner("Synnara Pereira de Souza Azevedo - Sapiens", "Adultos, Adolescentes, Criancas, Casais",
        "R. 15, 205 - Parque dos Buritis, Rio Verde",
        "6499295-7504", "6499295-7504", Icons.Default.Psychology, "Psicologia"),
    Partner("Jennifer Guimaraes de Moura - Psicologias", "TCC (35 a 55 anos)",
        "R. Rosulino Ferreira Guimaraes - Parque dos Buritis, Rio Verde",
        "6499344-7929", "6499344-7929", Icons.Default.Psychology, "Psicologia"),
    Partner("Joab Silva Souza - Clinica Solaris", "Transtornos Mentais / Ansiedade / Depressao",
        "R. Rui Barbosa, 1261 - Centro, Rio Verde",
        "6498113-3890", "6498113-3890", Icons.Default.Psychology, "Psicologia"),
    Partner("Jesse Silva Cabral - Terapia ABA", "Atendimento online",
        "Atendimento online",
        "6499289-5682", "6499289-5682", Icons.Default.Psychology, "Psicologia"),

    // ROUPAS
    Partner("Kalangotango", "16% a vista / 10% no cartao (exceto promocoes)",
        "Av. 77, 360 - Bairro Popular, Rio Verde",
        "6436021734", "", Icons.Default.Checkroom, "Roupas"),
    Partner("Kalangotango Kids", "16% a vista / 10% no cartao (exceto promocoes)",
        "Av. 77, 360 - Bairro Popular, Rio Verde",
        "6436125133", "", Icons.Default.Checkroom, "Roupas"),

    // NOVAS PARCERIAS (DOCX)
    Partner("Disk Gas", "5% em gas de cozinha",
        "R. Rio Verde, 240 - Vila Maria, Rio Verde",
        "6436121080", "", Icons.Default.LocalGasStation, "Outros"),
    Partner("Corpus Suplementos Nutricionais", "15% em produtos",
        "R. Gumercindo Ferreira, 592, Sala C - Centro, Rio Verde",
        "6499979-1070", "6499979-1070", Icons.Default.FitnessCenter, "Fitness"),
    Partner("Clinica Veterinaria Rio Verde", "10% a vista (consultas/cirurgias) 7% (medicacoes)",
        "R. Rio Verde, 240 - Vila Maria, Rio Verde",
        "6499255-4900", "6499255-4900", Icons.Default.Pets, "Clinicas"),
    Partner("Rio Imunne - Clinica de Vacinacao", "6% em vacinas",
        "R. Abel Pereira de Castro, 709 - Setor Central, Rio Verde",
        "6499252-7000", "6499252-7000", Icons.Default.LocalHospital, "Clinicas"),
    Partner("Enf. Obstetrica Jessica Batista de Lacerda", "Taping pos-parto R\$300 / Laser R\$90 por sessao",
        "R. Abel Pereira de Castro, 709 - Setor Central (ou domicilio), Rio Verde",
        "6499648-9692", "6499648-9692", Icons.Default.HealthAndSafety, "Saude"),
    Partner("Clinica Sou Coluna", "30% nas consultas (R\$175) / 20% nos tratamentos",
        "Av. Presidente Vargas, 195 - Centro, Rio Verde",
        "6499644-3222", "6499644-3222", Icons.Default.LocalHospital, "Clinicas"),

    // MONTIVIDIU
    Partner("Clinica CMD", "10% a 40% em toda a clinica",
        "R. Carlos Barromeu, 595, Qd. 17, Lt. 05 - Centro, Montividiu",
        "6436291930", "", Icons.Default.LocalHospital, "Clinicas", "Montividiu"),
    Partner("Otica Laiz - Montividiu", "30% a vista / credito / crediario",
        "R. Francisco Sales Rocha, Qd. 10, Casa 2, 660 - Centro, Montividiu",
        "6499145813", "6499145813", Icons.Default.RemoveRedEye, "Oticas", "Montividiu"),

    // APARECIDA DO RIO DOCE
    Partner("Farmacia Saude", "5% a vista in generico e similar",
        "R. Otildes Luiz, 569 - Centro, Aparecida do Rio Doce",
        "6436371197", "6498433-3080", Icons.Default.MedicalServices, "Farmacias", "Aparecida do Rio Doce"),
    Partner("Drogaria Rio Doce", "10% a vista",
        "R. Odimar Carneiro, 548 - Centro, Aparecida do Rio Doce",
        "6498425-2772", "6498425-2772", Icons.Default.MedicalServices, "Farmacias", "Aparecida do Rio Doce"),

    // SANTO ANTONIO DA BARRA
    Partner("Viver Clinica", "20% a 30% a vista",
        "Av. Brasilia, 828 - Centro, Santo Antonio da Barra",
        "6436261259", "6499325-3992", Icons.Default.LocalHospital, "Clinicas", "Santo Antonio da Barra"),
    Partner("Drogamerica", "10% a vista",
        "Av. Brasilia, Qd. 36, Lt. 09 - Centro, Santo Antonio da Barra",
        "6499306-8391", "6499306-8391", Icons.Default.MedicalServices, "Farmacias", "Santo Antonio da Barra"),
)

// ─────────────────────────────────────────
// TELA PRINCIPAL
// ─────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenefitsScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    var selectedCategory by remember { mutableStateOf("Todos") }
    var selectedCity     by remember { mutableStateOf("Todas") }

    val cities = listOf("Todas", "Rio Verde", "Montividiu", "Aparecida do Rio Doce", "Santo Antonio da Barra")

    val filteredPartners: List<Partner> = remember(selectedCategory, selectedCity) {
        realPartners.filter { p ->
            val catOk  = selectedCategory == "Todos" || p.category == selectedCategory
            val cityOk = selectedCity == "Todas"    || p.city == selectedCity
            catOk && cityOk
        }
    }

    Scaffold(
        containerColor = Forest50,
        topBar = { BenefitsTopBar(onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { HeroBanner() }

            item {
                CityFilterRow(
                    cities = cities,
                    selectedCity = selectedCity,
                    onCitySelected = { selectedCity = it }
                )
            }

            item {
                BenefitsCategoryFilterRow(
                    categories = benefitsCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            item {
                BenefitsSectionHeader(
                    title = if (selectedCategory == "Todos") "Todos os parceiros" else selectedCategory,
                    subtitle = if (selectedCity == "Todas") "Todas as cidades" else selectedCity,
                    count = filteredPartners.size
                )
            }

            if (filteredPartners.isEmpty()) {
                item { BenefitsEmptyState(selectedCategory) }
            } else {
                items(filteredPartners) { partner ->
                    PartnerCard(partner)
                }
            }

            item { BenefitsLegalDisclaimer() }
        }
    }
}

// ─────────────────────────────────────────
// TOP BAR
// ─────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenefitsTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Clube de Vantagens",
                    fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color.White)
                Text("Pax Rio Verde",
                    fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Forest800)
    )
}

// ─────────────────────────────────────────
// HERO BANNER
// ─────────────────────────────────────────
@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(Forest800, Forest600)))
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Column {
            Text("${realPartners.size}", fontSize = 52.sp, fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.12f))
            Text("parceiros com", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.offset(y = (-12).dp))
            Text("descontos exclusivos", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = Color.White, modifier = Modifier.offset(y = (-12).dp))
            Spacer(Modifier.height(4.dp))
            Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                Text(
                    text = "Apresente seu cartao Pax e economize",
                    fontSize = 12.sp, color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// FILTRO CIDADES
// ─────────────────────────────────────────
@Composable
fun CityFilterRow(
    cities: List<String>,
    selectedCity: String,
    onCitySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.background(Forest800.copy(alpha = 0.9f))
    ) {
        items(cities) { city ->
            val isSelected = selectedCity == city
            FilterChip(
                selected = isSelected,
                onClick = { onCitySelected(city) },
                label = {
                    Text(city, fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Amber500,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White.copy(alpha = 0.12f),
                    labelColor = Color.White
                ),
                border = null,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

// ─────────────────────────────────────────
// FILTRO CATEGORIAS
// ─────────────────────────────────────────
@Composable
fun BenefitsCategoryFilterRow(
    categories: List<BenefitsCategory>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.background(Forest800)
    ) {
        items(categories) { cat ->
            val isSelected = selectedCategory == cat.label
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(cat.label) },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(cat.icon, null, modifier = Modifier.size(14.dp))
                        Text(cat.label, fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.White,
                    selectedLabelColor = Forest600,
                    containerColor = Color.White.copy(alpha = 0.12f),
                    labelColor = Color.White
                ),
                border = null,
                shape = RoundedCornerShape(20.dp)
            )
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
// PARTNER CARD
// ─────────────────────────────────────────
@Composable
fun PartnerCard(partner: Partner) {
    val uriHandler = LocalUriHandler.current
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { expanded = !expanded },
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape     = RoundedCornerShape(14.dp)
    ) {
        Column {
            // ── Linha principal ──────────────────────
            Row(
                modifier = Modifier.padding(14.dp).height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Forest600.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(partner.icon, null, tint = Forest600, modifier = Modifier.size(26.dp))
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(partner.name, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        color = Slate800, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(3.dp))
                    Surface(color = Amber50, shape = RoundedCornerShape(5.dp)) {
                        Text(
                            text = partner.discount,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Amber500,
                            maxLines = 2, overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Slate500,
                            modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(2.dp))
                        Text(partner.address, fontSize = 11.sp, color = Slate500,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Recolher" else "Ver opcoes",
                    tint = Slate500, modifier = Modifier.size(22.dp)
                )
            }

            // ── Painel expandido ─────────────────────
            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(color = Slate200, thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ligar
                        Button(
                            onClick = {
                                val clean = partner.phone.replace(Regex("[^0-9]"), "")
                                uriHandler.openUri("tel:$clean")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Forest600),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.Phone, null, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Ligar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // WhatsApp — condicional
                        if (partner.whatsapp.isNotBlank()) {
                            Button(
                                onClick = {
                                    val clean = partner.whatsapp.replace(Regex("[^0-9]"), "")
                                    val msg = "Ola! Sou associado da Pax Rio Verde e gostaria de saber mais sobre os descontos."
                                    uriHandler.openUri("https://wa.me/55$clean?text=${urlEncode(msg)}")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_whatsapp_social),
                                    contentDescription = "WhatsApp",
                                    modifier = Modifier.size(15.dp),
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Google Maps
                        OutlinedButton(
                            onClick = {
                                uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=${urlEncode("${partner.name} ${partner.address}")}")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Forest600),
                            border = ButtonDefaults.outlinedButtonBorder,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Mapa", fontSize = 12.sp)
                        }
                    }
                }
            }
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
            text = "Toque em \"Todos\" para ver os parceiros disponiveis.",
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
            text = "Descontos sujeitos a alteracao sem aviso previo. Confirme as condicoes com o parceiro antes de utilizar. Versao 1.01.2026.",
            fontSize = 12.sp, color = Slate500, lineHeight = 18.sp
        )
    }
}
