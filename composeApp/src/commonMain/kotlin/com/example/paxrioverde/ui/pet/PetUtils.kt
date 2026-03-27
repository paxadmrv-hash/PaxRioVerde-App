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
    if (years == 0 && months < 6) return 0
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
        Pair(years, months)
    } catch (e: Exception) {
        Pair(0, 0)
    }
}

/**
 * Converte ByteArray para String Base64 (CommonMain)
 */
fun ByteArray.toBase64(): String {
    val table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    val output = StringBuilder()
    var i = 0
    while (i < this.size) {
        val b1 = this[i].toInt() and 0xFF
        val b2 = if (i + 1 < this.size) this[i + 1].toInt() and 0xFF else -1
        val b3 = if (i + 2 < this.size) this[i + 2].toInt() and 0xFF else -1
        
        val n = (b1 shl 16) or (if (b2 != -1) b2 shl 8 else 0) or (if (b3 != -1) b3 else 0)
        
        output.append(table[(n shr 18) and 0x3F])
        output.append(table[(n shr 12) and 0x3F])
        output.append(if (b2 != -1) table[(n shr 6) and 0x3F] else '=')
        output.append(if (b3 != -1) table[n and 0x3F] else '=')

        i += 3
    }
    return output.toString()
}
