package com.example.paxrioverde.util

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.Network.nw_path_status_unsatisfied
import platform.darwin.dispatch_get_main_queue

class IosConnectivityObserver : ConnectivityObserver {
    override val status: Flow<ConnectivityObserver.Status> = callbackFlow {
        val monitor = nw_path_monitor_create()
        
        nw_path_monitor_set_update_handler(monitor) { path ->
            val status = when (nw_path_get_status(path)) {
                nw_path_status_satisfied -> ConnectivityObserver.Status.Available
                nw_path_status_unsatisfied -> ConnectivityObserver.Status.Unavailable
                else -> ConnectivityObserver.Status.Lost
            }
            launch { send(status) }
        }
        
        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)
        
        awaitClose {
            nw_path_monitor_cancel(monitor)
        }
    }.distinctUntilChanged()
}

actual fun getConnectivityObserver(): ConnectivityObserver = IosConnectivityObserver()
