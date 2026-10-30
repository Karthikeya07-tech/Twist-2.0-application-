package com.example.twist20.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

object LocalWifi {
    /**
     * Android will often send HTTP over cellular when the robot AP has no internet.
     * Prefer the current Wi‑Fi network so 192.168.4.1 stays reachable.
     */
    fun network(context: Context): Network? {
        val cm = context.getSystemService(ConnectivityManager::class.java) ?: return null
        @Suppress("DEPRECATION")
        cm.allNetworks.firstOrNull { network ->
            val caps = cm.getNetworkCapabilities(network)
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        }?.let { return it }

        val active = cm.activeNetwork ?: return null
        val caps = cm.getNetworkCapabilities(active)
        return if (caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) active else null
    }
}
