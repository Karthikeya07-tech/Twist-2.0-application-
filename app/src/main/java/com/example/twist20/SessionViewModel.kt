package com.example.twist20

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.twist20.data.BotInfo

class SessionViewModel : ViewModel() {
    var bot by mutableStateOf<BotInfo?>(null)
        private set

    fun rememberBot(info: BotInfo) {
        bot = info
    }
}
