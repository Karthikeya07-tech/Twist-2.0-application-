package com.example.twist20.data

import kotlinx.serialization.Serializable

@Serializable
data class ActionsResponse(
    val actions: List<String> = emptyList(),
)

@Serializable
data class ActionRequest(
    val action: String,
)

@Serializable
data class ActionReply(
    val ok: Boolean = false,
    val action: String? = null,
    val state: Int? = null,
    val error: String? = null,
)

val FALLBACK_ACTIONS = listOf(
    "angry",
    "excited",
    "fear",
    "greet",
    "happy",
    "jump",
    "neutral",
    "turn_left",
    "turn_right",
    "walk_backward",
    "walk_diagonal",
    "walk_forward",
    "yoga",
)

fun String.humanizeAction(): String =
    split('_')
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.replaceFirstChar { char -> char.uppercase() }
        }

fun robotStateLabel(state: Int?): String = when (state) {
    0 -> "Torque off"
    1 -> "Holding"
    2 -> "Playing"
    else -> "Idle"
}
