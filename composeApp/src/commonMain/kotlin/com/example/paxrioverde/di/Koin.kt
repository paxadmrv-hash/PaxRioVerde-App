package com.example.paxrioverde.di

import com.example.paxrioverde.api.ApiService
import com.example.paxrioverde.api.WalletCache
import com.example.paxrioverde.data.repository.AuthRepositoryImpl
import com.example.paxrioverde.data.repository.FinanceRepositoryImpl
import com.example.paxrioverde.data.repository.PetRepositoryImpl
import com.example.paxrioverde.data.repository.VirtualCardRepositoryImpl
import com.example.paxrioverde.domain.repository.AuthRepository
import com.example.paxrioverde.domain.repository.FinanceRepository
import com.example.paxrioverde.domain.repository.PetRepository
import com.example.paxrioverde.domain.repository.VirtualCardRepository
import com.example.paxrioverde.util.ConnectivityObserver
import com.example.paxrioverde.util.getConnectivityObserver
import com.example.paxrioverde.util.NotificationManager
import com.example.paxrioverde.util.SessionManager
import com.example.paxrioverde.util.BiometricAuthenticator
import com.example.paxrioverde.util.ReviewManager
import com.example.paxrioverde.ui.login.FirstAccessViewModel
import com.example.paxrioverde.ui.login.ForgotPasswordViewModel
import com.example.paxrioverde.ui.login.LoginViewModel
import com.example.paxrioverde.ui.notifications.NotificationsViewModel
import com.example.paxrioverde.ui.benefits.BenefitsViewModel
import com.example.paxrioverde.ui.dashboard.DashboardViewModel
import com.example.paxrioverde.ui.finance.FinanceViewModel
import com.example.paxrioverde.ui.pet.PetViewModel
import com.example.paxrioverde.ui.plans.PlansViewModel
import com.example.paxrioverde.ui.virtualcard.VirtualCardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.bind

/**
 * Módulo de rede: Concentra a configuração do cliente HTTP e serviços de API.
 */
val networkModule = module {
    // ApiService agora recebe SessionManager para injetar o Token no Header
    single { ApiService(get()) }
}

/**
 * Módulo de dados: Concentra Repositórios, Cache e Gerenciadores de Sessão.
 * Aqui aplicamos a Inversão de Dependência, vinculando a Interface à Implementação.
 */
val dataModule = module {
    singleOf(::SessionManager)
    singleOf(::BiometricAuthenticator)
    singleOf(::ReviewManager)
    singleOf(::WalletCache)
    singleOf(::NotificationManager)
    single { getConnectivityObserver() }
    
    // Vinculamos AuthRepository (Interface) com AuthRepositoryImpl (Concreta)
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::FinanceRepositoryImpl) { bind<FinanceRepository>() }
    singleOf(::VirtualCardRepositoryImpl) { bind<VirtualCardRepository>() }
    singleOf(::PetRepositoryImpl) { bind<PetRepository>() }
}

/**
 * Módulo de ViewModels: Definição de todas as ViewModels do aplicativo.
 * Usamos viewModelOf para injeção automática via construtor.
 */
val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::FirstAccessViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::FinanceViewModel)
    viewModelOf(::VirtualCardViewModel)
    viewModelOf(::PlansViewModel)
    viewModelOf(::PetViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::BenefitsViewModel)
}

/**
 * Módulo principal que combina todos os sub-módulos.
 */
val appModule = module {
    includes(networkModule, dataModule, viewModelModule)
}
