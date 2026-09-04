package com.example.paxrioverde.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.api.LoginResponse
import com.example.paxrioverde.ui.components.bounceClick
import com.example.paxrioverde.ui.theme.PaxDesignSystem
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.Res
import paxrioverde.composeapp.generated.resources.bg_login_family

@Composable
fun ProfileSelectionScreen(
    profiles: List<LoginResponse>,
    onProfileSelected: (LoginResponse) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PaxDesignSystem.Colors.BrandGreen,
                        PaxDesignSystem.Colors.Background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Escolha seu Perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Encontramos mais de um vínculo para este CPF. Selecione qual deseja acessar agora:",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(profiles) { profile ->
                    ProfileCard(
                        profile = profile,
                        onClick = { onProfileSelected(profile) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileCard(
    profile: LoginResponse,
    onClick: () -> Unit
) {
    val isDependent = profile.dependente == "S"
    
    Card(
        onClick = onClick,
        shape = PaxDesignSystem.Shapes.Card,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (isDependent) Color(0xFFE3F2FD) else PaxDesignSystem.Colors.BrandGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = if (isDependent) Icons.Default.SupervisedUserCircle else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (isDependent) Color(0xFF1976D2) else PaxDesignSystem.Colors.BrandGreen,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.nomecliente ?: "Usuário",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaxDesignSystem.Colors.TextDark
                )
                
                Text(
                    text = if (isDependent) "Perfil Dependente" else "Perfil Titular",
                    fontSize = 14.sp,
                    color = if (isDependent) Color(0xFF1976D2) else PaxDesignSystem.Colors.BrandGreen,
                    fontWeight = FontWeight.Medium
                )
                
                if (!profile.plano.isNullOrEmpty()) {
                    Text(
                        text = profile.plano,
                        fontSize = 12.sp,
                        color = PaxDesignSystem.Colors.TextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = PaxDesignSystem.Colors.TextSecondary.copy(alpha = 0.5f)
            )
        }
    }
}
