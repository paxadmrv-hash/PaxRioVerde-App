package com.example.paxrioverde.ui.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*

@Composable
fun SpeciesOption(text: String, isSelected: Boolean) {
    val bgColor = if (isSelected) BrandGreen else Color.Transparent
    val textColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

fun calculateHumanAge(species: String, porte: String, years: Int, months: Int): Int {
    if (years <= 0 && months < 6) return 0
    return when (species) {
        "Gato" -> when (years) { 0 -> 10; 1 -> 15; 2 -> 24; else -> 24 + (years - 2) * 4 }
        "Cão" -> when (porte) {
            "Pequeno" -> when (years) { 0 -> 15; 1 -> 20; 2 -> 24; else -> 24 + (years - 2) * 4 }
            "Médio" -> when (years) { 0 -> 15; 1 -> 18; 2 -> 23; else -> 23 + (years - 2) * 5 }
            else -> when (years) { 0 -> 12; 1 -> 16; 2 -> 21; else -> 21 + (years - 2) * 6 }
        }
        else -> 0
    }
}

fun isValidDate(day: Int, month: Int, year: Int): Boolean {
    return try {
        val date = LocalDate(year, month, day)
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        
        // Verifica se a data é futura
        if (date > today) return false
        
        // Verifica se o pet tem mais de 30 anos (data muito antiga)
        if (year < (today.year - 30)) return false
        
        true
    } catch (e: Exception) {
        false
    }
}

fun getAgeFromDate(birthDate: String): Pair<Int, Int> {
    return try {
        val parts = birthDate.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
        
        val birth = LocalDate(year, month, day)
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        
        var years = today.year - birth.year
        var months = today.monthNumber - birth.monthNumber
        
        if (months < 0 || (months == 0 && today.dayOfMonth < birth.dayOfMonth)) {
            years--
            months += 12
        }
        
        // Se a data for futura ou inválida, retorna 0,0
        if (years < 0) Pair(0, 0) else Pair(years, months)
    } catch (e: Exception) {
        Pair(0, 0)
    }
}

/**
 * Converte ByteArray para String Base64 (Multiplatform)
 */
expect fun ByteArray.toBase64(): String
