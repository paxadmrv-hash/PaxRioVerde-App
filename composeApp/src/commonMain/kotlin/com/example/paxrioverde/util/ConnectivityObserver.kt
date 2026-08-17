package com.example.paxrioverde.util

import kotlinx.coroutines.flow.Flow

/**
 * ConnectivityObserver: Monitor de rede multiplataforma (KMP).
 * Senior Infrastructure: Permite que o app reaja em tempo real à perda de conexão.
 */
interface ConnectivityObserver {
    val status: Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}

expect fun getConnectivityObserver(): ConnectivityObserver
