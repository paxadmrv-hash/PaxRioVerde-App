package com.example.paxrioverde.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.painterResource
import paxrioverde.composeapp.generated.resources.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WhatsNewModal(
    onDismiss: () -> Unit
) {
    val slides = listOf(
        WhatsNewSlide(
            title = "Gestão de Dependentes",
            description = "Acesso liberado! Seu dependente já pode aproveitar o aplicativo.Visualize e gerencie seus dependentes com facilidade diretamente pelo app.",
            icon = Icons.Default.Groups,
            color = Color(0xFF386641)
        ),
        WhatsNewSlide(
            title = "Pagamentos e Mensalidades",
            description = "Acesse seus boletos e histórico de pagamentos de forma rápida, segura e sem complicações.",
            icon = Icons.Default.AccountBalanceWallet,
            color = Color(0xFF2E7D32)
        ),
        WhatsNewSlide(
            title = "Clube de Vantagens",
            description = "Descontos exclusivos em nossa rede de parceiros: farmácias, clínicas, lazer e muito mais.",
            icon = Icons.Default.AutoAwesome,
            color = Color(0xFFF9A825)
        ),
        WhatsNewSlide(
            title = "Cartão Virtual",
            description = "Sua carteira digital sempre disponível. Personalize o estilo e compartilhe seu cartão quando precisar.",
            icon = Icons.Default.Contactless,
            color = Color(0xFF1565C0)
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                // Header com botão Pular
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (pagerState.currentPage < slides.size - 1) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text(
                                "Pular",
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Conteúdo Principal (Pager)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) { page ->
                    val slide = slides[page]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Ícone com fundo circular
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                                .background(slide.color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = slide.icon,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = slide.color
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        Text(
                            text = slide.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center,
                            lineHeight = 34.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = slide.description,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                }

                // Footer com Indicador e Botão
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Indicador de Página (Dots)
                    Row(
                        modifier = Modifier
                            .padding(bottom = 32.dp)
                            .height(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(slides.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color(0xFF386641) else Color(0xFFE5E7EB)
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(if (pagerState.currentPage == iteration) 12.dp else 8.dp)
                            )
                        }
                    }

                    // Botão de Ação
                    Button(
                        onClick = {
                            if (pagerState.currentPage < slides.size - 1) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF386641)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (pagerState.currentPage < slides.size - 1) "Próximo" else "Começar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

data class WhatsNewSlide(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)
