package com.example.paxrioverde

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.paxrioverde.api.ApiService
import com.example.paxrioverde.api.LoginResponse
import com.example.paxrioverde.api.WalletCache
import com.example.paxrioverde.ui.benefits.BenefitsScreen
import com.example.paxrioverde.ui.biometrics.BiometricsScreen
import com.example.paxrioverde.ui.components.AppDrawer
import com.example.paxrioverde.ui.contact.FaleConoscoScreen
import com.example.paxrioverde.ui.dashboard.DashboardScreen
import com.example.paxrioverde.ui.finance.FinanceScreen
import com.example.paxrioverde.ui.laboratorio.ExamesLaboratoriaisScreen
import com.example.paxrioverde.ui.login.FirstAccessScreen
import com.example.paxrioverde.ui.login.LoginScreen
import com.example.paxrioverde.ui.notifications.NotificationsScreen
import com.example.paxrioverde.ui.pet.MundoPetScreen
import com.example.paxrioverde.ui.plans.PlansScreen
import com.example.paxrioverde.ui.refer.ReferFriendScreen
import com.example.paxrioverde.ui.saude.MedSaudeScreen
import com.example.paxrioverde.ui.splash.SplashScreen
import com.example.paxrioverde.ui.theme.AppGrupoUniversoTheme
import com.example.paxrioverde.ui.virtualcard.VirtualCardScreen
import com.example.paxrioverde.util.CommonBackHandler
import com.example.paxrioverde.util.SessionManager
import kotlinx.coroutines.launch

enum class Screen {
    Splash,
    Login,
    FirstAccess,
    Dashboard,
    Benefits,
    Biometrics,
    Contact,
    Finance,
    Notifications,
    Pet,
    Plans,
    Referral,
    VirtualCard,
    Laboratorio,
    MedSaude
}

@Composable
fun App() {
    // Gerenciamento manual de pilha para suportar o "Voltar"
    var navigationStack by remember { mutableStateOf(listOf(Screen.Splash)) }
    val currentScreen = navigationStack.last()
    
    var userData by remember { mutableStateOf<LoginResponse?>(null) }
    var isAutoLoginDone by remember { mutableStateOf(false) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager() }

    // Efeito para Auto-Login quando o App abre e já tem dados salvos
    fun refreshUserData() {
        val savedCpf = sessionManager.getSavedCpf()
        val savedPass = sessionManager.getSavedPassword()
        if (savedCpf.isNotEmpty() && savedPass.isNotEmpty()) {
            scope.launch {
                try {
                    val response = ApiService.login(savedCpf, savedPass)
                    if (response.success) {
                        userData = response
                    }
                } catch (e: Exception) {
                    println("Erro ao atualizar dados: ${e.message}")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isAutoLoginDone) {
            refreshUserData()
            isAutoLoginDone = true
        }
    }

    // Função para navegar para uma nova tela (adiciona à pilha)
    fun navigateTo(screen: Screen, clearStack: Boolean = false) {
        if (clearStack) {
            navigationStack = listOf(screen)
        } else {
            if (navigationStack.lastOrNull() != screen) {
                navigationStack = navigationStack + screen
            }
        }
    }

    // Função para voltar (remove a última tela da pilha)
    fun goBack() {
        if (navigationStack.size > 1) {
            navigationStack = navigationStack.dropLast(1)
        }
    }

    // INTERCEPTADOR DO BOTÃO VOLTAR DO SISTEMA
    CommonBackHandler(enabled = navigationStack.size > 1) {
        goBack()
    }

    AppGrupoUniversoTheme {
        when (currentScreen) {
            Screen.Splash -> SplashScreen(
                onNavigateToLogin = { navigateTo(Screen.Login, clearStack = true) },
                onNavigateToDashboard = { navigateTo(Screen.Dashboard, clearStack = true) }
            )
            Screen.Login -> LoginScreen(
                onLoginSuccess = { response ->
                    userData = response
                    navigateTo(Screen.Dashboard, clearStack = true)
                },
                onFirstAccessClick = { navigateTo(Screen.FirstAccess) }
            )
            Screen.FirstAccess -> FirstAccessScreen(
                onBack = { goBack() }
            )
            else -> {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        AppDrawer(
                            currentScreen = currentScreen,
                            onNavigate = { screen ->
                                if (screen == Screen.Login) {
                                    userData = null
                                    // Removemos a limpeza automática aqui para respeitar o "Lembrar Login"
                                    navigateTo(Screen.Login, clearStack = true)
                                } else {
                                    navigateTo(screen)
                                }
                                scope.launch { drawerState.close() }
                            },
                            onLogout = {
                                userData = null
                                // Removemos a limpeza automática aqui para respeitar o "Lembrar Login"
                                navigateTo(Screen.Login, clearStack = true)
                            },
                            closeDrawer = {
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        when (currentScreen) {
                            Screen.Dashboard -> {
                                val displayValorCartao = userData?.valorcartao

                                DashboardScreen(
                                    userData = userData,
                                    valorCartao = displayValorCartao,
                                    onOpenWallet = { navigateTo(Screen.VirtualCard) },
                                    onOpenBoleto = { navigateTo(Screen.Finance) },
                                    onOpenReferral = { navigateTo(Screen.Referral) },
                                    onOpenContact = { navigateTo(Screen.Contact) },
                                    onOpenPlans = { navigateTo(Screen.Plans) },
                                    onOpenBenefits = { navigateTo(Screen.Benefits) },
                                    onOpenPet = { navigateTo(Screen.Pet) },
                                    onOpenMedSaude = { navigateTo(Screen.MedSaude) },
                                    onOpenExames = { navigateTo(Screen.Laboratorio) },
                                    onOpenNotifications = { navigateTo(Screen.Notifications) },
                                    onOpenDrawer = {
                                        scope.launch { drawerState.open() }
                                    }
                                )
                            }
                            Screen.Benefits -> {
                                BenefitsScreen(onBack = { goBack() })
                            }
                            Screen.Biometrics -> {
                                BiometricsScreen(onBack = { goBack() })
                            }
                            Screen.Contact -> {
                                FaleConoscoScreen(onBackClick = { goBack() })
                            }
                            Screen.Finance -> {
                                val displayValorCartao = userData?.valorcartao

                                FinanceScreen(
                                    onBackClick = { goBack() },
                                    idcliente = userData?.idcliente ?: 0,
                                    idcaixa = userData?.idcaixa_pix ?: 0,
                                    valorProxMens = userData?.valormens_prox_mens ?: "0,00",
                                    vencProxMens = userData?.prox_mens ?: "--/--/----",
                                    showBoletoButton = userData?.boleto_prox_mens ?: false,
                                    valorCartao = displayValorCartao
                                )
                            }
                            Screen.Notifications -> {
                                NotificationsScreen(onBack = { goBack() })
                            }
                            Screen.Pet -> {
                                MundoPetScreen(
                                    onBack = { goBack() },
                                    idcliente = userData?.idcliente ?: 0,
                                    idcontrato = userData?.idcontrato_prox_mens ?: 0,
                                    idconvenio = userData?.idconvenio_prox_mens ?: 1
                                )
                            }
                            Screen.Plans -> {
                                PlansScreen(
                                    onBack = { goBack() },
                                    idcliente = userData?.idcliente ?: 0,
                                    userPlano = userData?.plano ?: "Plano",
                                    valorMensalidade = userData?.valormensalidade ?: "0,00"
                                )
                            }
                            Screen.Referral -> {
                                ReferFriendScreen(onBack = { goBack() })
                            }
                            Screen.VirtualCard -> {
                                VirtualCardScreen(
                                    onBack = { goBack() },
                                    idcliente = userData?.idcliente ?: 0,
                                    idcontrato = userData?.idcontrato_prox_mens ?: 0,
                                    idconvenio = userData?.idconvenio_prox_mens ?: 0,
                                    idmensalidade = userData?.idmensalidade_prox_mens ?: 0,
                                    idcaixa = userData?.idcaixa_pix ?: 0,
                                    idfilial = userData?.idfilial ?: 0,
                                    dtvencimento = userData?.prox_mens ?: "",
                                    valorCartao = userData?.valorcartao,
                                    onCardGenerated = { refreshUserData() },
                                    onNavigateToFinance = { navigateTo(Screen.Finance) }
                                )
                            }
                            Screen.Laboratorio -> {
                                ExamesLaboratoriaisScreen(onBack = { goBack() })
                            }
                            Screen.MedSaude -> {
                                MedSaudeScreen(onBackClick = { goBack() })
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
