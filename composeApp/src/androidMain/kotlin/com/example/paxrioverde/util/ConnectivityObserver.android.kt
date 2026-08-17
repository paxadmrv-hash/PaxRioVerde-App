package com.example.paxrioverde.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.paxrioverde.AndroidContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class AndroidConnectivityObserver : ConnectivityObserver {
    
    private val context: Context = AndroidContext.get()
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val status: Flow<ConnectivityObserver.Status> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                PaxLogger.d("Rede Disponível", "Observer")
                trySend(ConnectivityObserver.Status.Available)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                PaxLogger.d("Perdendo Conexão", "Observer")
                trySend(ConnectivityObserver.Status.Losing)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                PaxLogger.d("Rede Perdida", "Observer")
                trySend(ConnectivityObserver.Status.Lost)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                PaxLogger.d("Rede Indisponível", "Observer")
                trySend(ConnectivityObserver.Status.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) // Garante que há internet real
            .build()

        connectivityManager.registerNetworkCallback(request, callback)
        
        // Estado inicial imediato
        val initial = if (isNetworkAvailable()) ConnectivityObserver.Status.Available else ConnectivityObserver.Status.Unavailable
        PaxLogger.d("Estado Inicial: $initial", "Observer")
        trySend(initial)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

actual fun getConnectivityObserver(): ConnectivityObserver = AndroidConnectivityObserver()
