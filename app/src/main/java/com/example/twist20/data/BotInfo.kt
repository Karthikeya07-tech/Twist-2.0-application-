package com.example.twist20.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BotInfo(
    val protocol: Int = 0,
    val name: String = "",
    val type: String = "",
    val stage: String = "",
    @SerialName("type_id") val typeId: Int = 0,
    val firmware: String = "",
    val serial: String = "",
    val provisioned: Boolean = false,
    @SerialName("hw_rev") val hwRev: String = "",
    val mac: String = "",
    val ssid: String = "",
    val ip: String = "",
    @SerialName("ctrl_port") val ctrlPort: Int = 0,
) {
    val displayName: String
        get() = name.ifBlank { type.ifBlank { "Twist" } }

    val stageLabel: String
        get() = when (stage.lowercase()) {
            "rd" -> "R&D"
            "production" -> "Production"
            else -> stage.ifBlank { "—" }
        }

    val controlHost: String
        get() = ip.ifBlank { "192.168.4.1" }

    val controlPortOrDefault: Int
        get() = if (ctrlPort > 0) ctrlPort else 7780
}
