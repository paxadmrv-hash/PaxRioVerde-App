package com.example.paxrioverde.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// CORES
val BrandGreen = Color(0xFF386641)
val BrandLightGreen = Color(0xFFE8F5E9)
val TextDark = Color(0xFF1F2937)
val TextGray = Color(0xFF6B7280)
val NotificationRed = Color(0xFFD32F2F)
val NotificationBlue = Color(0xFF1976D2)

//MODELO
data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    PAYMENT, PROMO, SYSTEM, ALERT
}

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = viewModel { NotificationsViewModel() }
) {
    val notifications = viewModel.notifications

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
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
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.limparNotificacoes() }) {
                        Text("Limpar", color = BrandGreen, fontWeight = FontWeight.Bold)
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
                items(notifications) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
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

    val bgColor = if (item.isRead) Color.White else BrandLightGreen.copy(alpha = 0.3f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    Text(
                        text = item.time,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.message,
                    fontSize = 14.sp,
                    color = TextGray,
                    lineHeight = 20.sp
                )
            }

            if (!item.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NotificationRed)
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
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhuma notificação",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray
        )
        Text(
            text = "Você está em dia com tudo!",
            fontSize = 14.sp,
            color = Color.LightGray
        )
    }
}
