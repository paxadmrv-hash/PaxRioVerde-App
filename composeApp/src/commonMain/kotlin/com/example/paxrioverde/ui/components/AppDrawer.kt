package com.example.paxrioverde.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.Screen

// CORES
val BrandLime = Color(0xFF386641)
val SoftBackground = Color(0xFFF8F9FA)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF2D3436)
val TextGray = Color(0xFF636E72)

data class DrawerMenuItem(val icon: ImageVector, val text: String, val screen: Screen)

@Composable
fun AppDrawer(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit,
    closeDrawer: () -> Unit
) {
    val drawerItems = remember {
        listOf(
            DrawerMenuItem(Icons.Default.Home, "Início", Screen.Dashboard),
            DrawerMenuItem(Icons.Default.DateRange, "Boletos", Screen.Finance),
            DrawerMenuItem(Icons.Default.Description, "Meu Plano", Screen.Plans),
            DrawerMenuItem(Icons.Default.Fingerprint, "Entrar com Digital", Screen.Biometrics),
            DrawerMenuItem(Icons.Default.Call, "Fale Conosco", Screen.Contact)
        )
    }

    ModalDrawerSheet(
        drawerContainerColor = Color.Transparent,
        drawerTonalElevation = 0.dp,
        modifier = Modifier
            .width(320.dp)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(SoftBackground, RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // CABEÇALHO
            Surface(
                color = SurfaceWhite,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Surface(
                        color = BrandLime.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = BrandLime,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "PAX RIO VERDE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Bem-vindo(a)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextGray
                    )
                }
            }

            // NAVEGAÇÃO
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                item {
                    Text(
                        text = "Menu Principal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }
                items(drawerItems) { item ->
                    val isSelected = currentScreen == item.screen

                    Surface(
                        onClick = {
                            onNavigate(item.screen)
                            closeDrawer()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) SurfaceWhite else Color.Transparent,
                        shadowElevation = if (isSelected) 4.dp else 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = BrandLime,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = item.text,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextDark else TextGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // RODAPÉ E SAIR
            Surface(
                onClick = {
                    onLogout()
                    closeDrawer()
                },
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Sair da conta",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}
