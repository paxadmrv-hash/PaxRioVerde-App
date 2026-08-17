package com.example.paxrioverde.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.ui.components.bounceClick
import com.example.paxrioverde.ui.theme.PaxDesignSystem
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

// Senior Theme Integration: Centralizado no PaxDesignSystem
private val BrandGreen = PaxDesignSystem.Colors.BrandGreen
private val BrandLightGreen = Color(0xFFE8F5E9)
private val TextDark = PaxDesignSystem.Colors.TextDark
private val TextGray = PaxDesignSystem.Colors.TextSecondary
private val NotificationRed = PaxDesignSystem.Colors.Error
private val NotificationBlue = Color(0xFF1976D2)

//MODELO
@Serializable
data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

@Serializable
enum class NotificationType {
    PAYMENT, PROMO, SYSTEM, ALERT
}

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = koinViewModel()
) {
    val notifications = viewModel.notifications
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = PaxDesignSystem.Colors.Background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = TextDark)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notificações",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (notifications.isNotEmpty()) {
                        TextButton(
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.limparTudo() 
                            }
                        ) {
                            Text("Limpar tudo", color = BrandGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(notifications, key = { it.id }) { notification ->
                    NotificationCard(
                        item = notification,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.marcarComoLida(notification.id) 
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem, onClick: () -> Unit) {
    val icon = when (item.type) {
        NotificationType.PAYMENT -> Icons.Outlined.CheckCircle
        NotificationType.PROMO -> Icons.Outlined.Celebration
        NotificationType.ALERT -> Icons.Outlined.WarningAmber
        NotificationType.SYSTEM -> Icons.Outlined.Info
    }

    val iconColor = when (item.type) {
        NotificationType.PAYMENT -> BrandGreen
        NotificationType.PROMO -> NotificationBlue
        NotificationType.ALERT -> NotificationRed
        NotificationType.SYSTEM -> TextGray
    }

    val bgColor = if (item.isRead) Color.White else BrandLightGreen.copy(alpha = 0.5f)

    Card(
        shape = PaxDesignSystem.Shapes.Medium,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(if (item.isRead) 0.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontWeight = if (item.isRead) FontWeight.Bold else FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    Text(
                        text = item.time,
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.message,
                    fontSize = 14.sp,
                    color = if (item.isRead) TextGray else TextDark.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }

            if (!item.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(BrandGreen)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            color = BrandGreen.copy(alpha = 0.05f),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = BrandGreen.copy(alpha = 0.2f),
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Tudo em dia!",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark
        )
        Text(
            text = "Você não possui novas notificações.",
            fontSize = 15.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp).padding(top = 8.dp)
        )
    }
}
