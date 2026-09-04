package com.example.paxrioverde

import com.example.paxrioverde.ui.benefits.BenefitsScreen
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paxrioverde.api.LoginResponse
import com.example.paxrioverde.api.WalletCache
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.repository.AuthRepository
import com.example.paxrioverde.ui.biometrics.BiometricsScreen
import com.example.paxrioverde.ui.components.AppDrawer
import com.example.paxrioverde.ui.components.PaxSystemOverlays
import com.example.paxrioverde.ui.contact.FaleConoscoScreen
import com.example.paxrioverde.ui.dashboard.DashboardScreen
import com.example.paxrioverde.ui.finance.FinanceScreen
import com.example.paxrioverde.ui.laboratorio.ExamesLaboratoriaisScreen
import com.example.paxrioverde.ui.login.FirstAccessScreen
import com.example.paxrioverde.ui.login.ForgotPasswordScreen
import com.example.paxrioverde.ui.login.LoginScreen
import com.example.paxrioverde.ui.login.ProfileSelectionScreen
import com.example.paxrioverde.ui.notifications.NotificationsScreen
import com.example.paxrioverde.ui.pet.MundoPetScreen
import com.example.paxrioverde.ui.plans.PlansScreen
import com.example.paxrioverde.ui.refer.ReferFriendScreen
import com.example.paxrioverde.ui.saude.MedSaudeScreen
import com.example.paxrioverde.ui.splash.SplashScreen
import com.example.paxrioverde.ui.theme.AppGrupoUniversoTheme
import com.example.paxrioverde.ui.virtualcard.VirtualCardScreen
import com.example.paxrioverde.util.*
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

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
    MedSaude,
    ForgotPassword,
    ProfileSelection
}

@Composable
fun App(initialScreen: Screen? = null) {
    KoinContext {
        val authRepository = koinInject<AuthRepository>()
        val sessionManager = koinInject<SessionManager>()
        val walletCache = koinInject<WalletCache>()
        val connectivityObserver = koinInject<ConnectivityObserver>()
        val biometricAuthenticator = koinInject<BiometricAuthenticator>()
        
        val networkStatus by connectivityObserver.status.collectAsState(ConnectivityObserver.Status.Available)
        val isOffline = networkStatus != ConnectivityObserver.Status.Available
        
        var wasOffline by remember { mutableStateOf(false) }
        var showBackOnline by remember { mutableStateOf(false) }

        // Senior Note: Uso de rememberSaveable para evitar perda de navegação em rotação.
        var navigationStack by androidx.compose.runtime.saveable.rememberSaveable(
            saver = androidx.compose.runtime.saveable.listSaver<MutableState<List<Screen>>, String>(
                save = { state -> state.value.map { it.name } },
                restore = { names -> mutableStateOf(names.map { Screen.valueOf(it) }) },
            )
        ) { mutableStateOf(listOf(Screen.Splash)) }
        
        val currentScreen = navigationStack.last()
        
        var isAuthenticating by remember { mutableStateOf(false) }

        var userData by androidx.compose.runtime.saveable.rememberSaveable(
            saver = androidx.compose.runtime.saveable.Saver<MutableState<LoginResponse?>, String>(
                save = { if (it.value != null) kotlinx.serialization.json.Json.encodeToString(LoginResponse.serializer(), it.value!!) else null },
                restore = { mutableStateOf(kotlinx.serialization.json.Json.decodeFromString(LoginResponse.serializer(), it)) }
            )
        ) { mutableStateOf<LoginResponse?>(null) }

        var availableProfiles by rememberSaveable(
            saver = Saver<MutableState<List<LoginResponse>>, String>(
                save = { Json.encodeToString(ListSerializer(LoginResponse.serializer()), it.value) },
                restore = { mutableStateOf(Json.decodeFromString(ListSerializer(LoginResponse.serializer()), it)) }
            )
        ) { mutableStateOf<List<LoginResponse>>(emptyList()) }

        var showRootWarning by remember { mutableStateOf(false) }
        
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

        LaunchedEffect(networkStatus) {
            PaxLogger.d("Status de Conexão: $networkStatus | isOffline: $isOffline", "App")
            
            if (isOffline) {
                wasOffline = true
                showBackOnline = false
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            } else if (wasOffline) {
                // Retornou ao estado online
                showBackOnline = true
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                kotlinx.coroutines.delay(4000L) // Exibe por 4 segundos
                showBackOnline = false
                wasOffline = false
            }
        }

        LaunchedEffect(Unit) {
            if (isDeviceRooted()) {
                showRootWarning = true
            }
        }

        fun navigateTo(screen: Screen, clearStack: Boolean = false) {
            if (clearStack) {
                navigationStack = listOf(screen)
            } else {
                if (navigationStack.lastOrNull() != screen) {
                    navigationStack += screen
                }
            }
        }

        fun refreshUserData(onComplete: () -> Unit = {}) {
            val savedCpf = sessionManager.getSavedCpf()
            val savedPass = sessionManager.getSavedPassword()
            
            if (savedCpf.isNotEmpty() && savedPass.isNotEmpty()) {
                isAuthenticating = true
                val isBiometricEnabled = sessionManager.isBiometricEnabled()
                
                if (isBiometricEnabled && biometricAuthenticator.canAuthenticate()) {
                    biometricAuthenticator.authenticate(
                        title = "Autenticação Necessária",
                        subtitle = "Use sua biometria para acessar sua conta Pax Rio Verde",
                        onSuccess = {
                            scope.launch {
                                try {
                                    val result = authRepository.login(savedCpf, savedPass, true)
                                    if (result is NetworkResult.Success) {
                                        availableProfiles = result.data
                                        // Se já temos um userData, tentamos manter o mesmo perfil
                                        val currentId = userData?.idcliente
                                        userData = if (currentId != null) {
                                            result.data.find { it.idcliente == currentId } ?: result.data.firstOrNull()
                                        } else {
                                            result.data.firstOrNull()
                                        }
                                    }
                                } catch (e: Exception) {
                                    PaxLogger.e("Erro ao atualizar dados", e, "App")
                                } finally {
                                    isAuthenticating = false
                                    onComplete()
                                }
                            }
                        },
                        onError = { error ->
                            PaxLogger.e("Erro biometria auto-login: $error", null, "App")
                            isAuthenticating = false
                            onComplete()
                            navigateTo(Screen.Login, clearStack = true)
                        }
                    )
                } else {
                    scope.launch {
                        try {
                            val result = authRepository.login(savedCpf, savedPass, true)
                            if (result is NetworkResult.Success) {
                                availableProfiles = result.data
                                // Se já temos um userData, tentamos manter o mesmo perfil
                                val currentId = userData?.idcliente
                                userData = if (currentId != null) {
                                    result.data.find { it.idcliente == currentId } ?: result.data.firstOrNull()
                                } else {
                                    result.data.firstOrNull()
                                }
                            }
                        } catch (e: Exception) {
                            PaxLogger.e("Erro ao atualizar dados", e, "App")
                        } finally {
                            isAuthenticating = false
                            onComplete()
                        }
                    }
                }
            } else {
                onComplete()
            }
        }

        // Senior Note: Removido LaunchedEffect de auto-login automático para evitar bypass de biometria.
        // O auto-login agora é disparado pelo callback onFinished do SplashScreen.


        fun goBack() {
            if (navigationStack.size > 1) {
                navigationStack = navigationStack.dropLast(1)
            }
        }

        CommonBackHandler(enabled = navigationStack.size > 1) {
            goBack()
        }

        LaunchedEffect(currentScreen) {
            val isSensivel = currentScreen in listOf(
                Screen.Login,
                Screen.Finance,
                Screen.Biometrics,
                Screen.ForgotPassword
            )
            setScreenSecurity(isSensivel)
        }

        AppGrupoUniversoTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                // CAMADA DE CONTEÚDO
                when (currentScreen) {
                    Screen.Splash -> SplashScreen(
                        isLoading = isAuthenticating,
                        onFinished = {
                            initialScreen?.let {
                                if (it != Screen.Splash) {
                                    navigateTo(it, clearStack = true)
                                    return@SplashScreen
                                }
                            }

                            if (sessionManager.isRememberMeEnabled()) {
                                refreshUserData {
                                    navigateTo(Screen.Dashboard, clearStack = true)
                                }
                            } else {
                                navigateTo(Screen.Login, clearStack = true)
                            }
                        }
                    )
                    Screen.Login -> LoginScreen(
                        onLoginSuccess = { response ->
                            userData = response
                            navigateTo(Screen.Dashboard, clearStack = true)
                        },
                        onProfileSelection = { profiles ->
                            availableProfiles = profiles
                            navigateTo(Screen.ProfileSelection)
                        },
                        onFirstAccessClick = { navigateTo(Screen.FirstAccess) },
                        onForgotPasswordClick = { navigateTo(Screen.ForgotPassword) }
                    )
                    Screen.FirstAccess -> FirstAccessScreen(onBack = { goBack() })
                    Screen.ForgotPassword -> ForgotPasswordScreen(onBack = { goBack() })
                    else -> {
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = {
                                AppDrawer(
                                    currentScreen = currentScreen,
                                    isDependent = userData?.dependente == "S",
                                    hasMultipleProfiles = availableProfiles.size > 1,
                                    onNavigate = { screen ->
                                        if (screen == Screen.Login) {
                                            userData = null
                                            availableProfiles = emptyList()
                                            walletCache.clear()
                                            sessionManager.clearAllCache()
                                            navigateTo(Screen.Login, clearStack = true)
                                        } else {
                                            navigateTo(screen)
                                        }
                                        scope.launch { drawerState.close() }
                                    },
                                    onSwitchProfile = {
                                        navigateTo(Screen.ProfileSelection)
                                    },
                                    onLogout = {
                                        userData = null
                                        availableProfiles = emptyList()
                                        walletCache.clear()
                                        sessionManager.clearAllCache()
                                        navigateTo(Screen.Login, clearStack = true)
                                    },
                                    closeDrawer = { scope.launch { drawerState.close() } }
                                )
                            }
                        ) {
                            Surface(color = MaterialTheme.colorScheme.background) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AnimatedContent(
                                        targetState = currentScreen,
                                        transitionSpec = {
                                            (fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                                                initialOffsetX = { 40 },
                                                animationSpec = tween(300)
                                            )) togetherWith fadeOut(animationSpec = tween(300))
                                        }
                                    ) { screen ->
                                        when (screen) {
                                            Screen.Dashboard -> DashboardScreen(
                                                userData = userData,
                                                valorCartao = userData?.valorcartao,
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
                                                onOpenDrawer = { scope.launch { drawerState.open() } },
                                                onRefresh = { callback -> refreshUserData { callback() } }
                                            )
                                            Screen.Benefits -> BenefitsScreen(
                                                onBack = { goBack() },
                                                idcliente = userData?.idcliente ?: 0,
                                                isDependent = userData?.dependente == "S",
                                                userName = userData?.nomecliente
                                            )
                                            Screen.Biometrics -> BiometricsScreen(
                                                onBack = { goBack() },
                                                userData = userData
                                            )
                                            Screen.Contact -> FaleConoscoScreen(onBackClick = { goBack() })
                                            Screen.Finance -> FinanceScreen(
                                                onBackClick = { goBack() },
                                                idcliente = userData?.idcliente ?: 0,
                                                idcaixa = userData?.idcaixa_pix ?: 0,
                                                valorProxMens = userData?.valormens_prox_mens ?: "0,00",
                                                vencProxMens = userData?.prox_mens ?: "--/--/----",
                                                showBoletoButton = userData?.boleto_prox_mens ?: false
                                            )
                                            Screen.Notifications -> NotificationsScreen(onBack = { goBack() })
                                            Screen.Pet -> MundoPetScreen(
                                                onBack = { goBack() },
                                                idcliente = userData?.idcliente ?: 0,
                                                idcontrato = userData?.idcontrato_prox_mens ?: 0,
                                                idconvenio = userData?.idconvenio_prox_mens ?: 1
                                            )
                                            Screen.Plans -> PlansScreen(
                                                onBack = { goBack() },
                                                idcliente = userData?.idcliente ?: 0,
                                                userPlano = userData?.plano ?: "Plano",
                                                valorMensalidade = userData?.valormensalidade ?: "0,00"
                                            )
                                            Screen.Referral -> ReferFriendScreen(onBack = { goBack() })
                                            Screen.VirtualCard -> VirtualCardScreen(
                                                onBack = { goBack() },
                                                idcliente = userData?.idcliente ?: 0,
                                                idcontrato = userData?.idcontrato_prox_mens ?: 0,
                                                idconvenio = userData?.idconvenio_prox_mens ?: 0,
                                                idmensalidade = userData?.idmensalidade_prox_mens ?: 0,
                                                idcaixa = userData?.idcaixa_pix ?: 0,
                                                idfilial = userData?.idfilial ?: 0,
                                                dtvencimento = userData?.prox_mens ?: "",
                                                valorCartao = userData?.valorcartao,
                                                isDependent = userData?.dependente == "S",
                                                userName = userData?.nomecliente,
                                                userCpf = userData?.cpf,
                                                onCardGenerated = { refreshUserData() },
                                                onNavigateToFinance = { navigateTo(Screen.Finance) }
                                            )
                                            Screen.Laboratorio -> ExamesLaboratoriaisScreen(onBack = { goBack() })
                                            Screen.MedSaude -> MedSaudeScreen(onBackClick = { goBack() })
                                            Screen.ProfileSelection -> ProfileSelectionScreen(
                                                profiles = availableProfiles,
                                                onProfileSelected = { selected ->
                                                    userData = selected
                                                    // Senior Security: Atualiza o token da sessão para o perfil selecionado
                                                    selected.token?.let { sessionManager.saveAccessToken(it) }
                                                    navigateTo(Screen.Dashboard, clearStack = true)
                                                }
                                            )
                                            else -> {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // CAMADA DE SOBREPOSIÇÕES GLOBAIS (Root, Offline, Online Feedback)
                PaxSystemOverlays(
                    isOffline = isOffline,
                    showBackOnline = showBackOnline,
                    showRootWarning = showRootWarning,
                    onDismissRootWarning = { showRootWarning = false }
                )
            }
        }
    }
}
