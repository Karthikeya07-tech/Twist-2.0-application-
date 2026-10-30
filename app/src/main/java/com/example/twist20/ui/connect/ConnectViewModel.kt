package com.example.twist20.ui.connect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.twist20.data.BadBotResponseException
import com.example.twist20.data.BotInfo
import com.example.twist20.data.BotRepository
import com.example.twist20.data.UnreachableBotException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

sealed interface ConnectUiState {
    data object Idle : ConnectUiState
    data object Connecting : ConnectUiState
    data class Connected(val info: BotInfo) : ConnectUiState
    data class Error(val kind: ErrorKind) : ConnectUiState
}

enum class ErrorKind { Unreachable, BadResponse }

class ConnectViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BotRepository(application)

    private val _uiState = MutableStateFlow<ConnectUiState>(ConnectUiState.Idle)
    val uiState: StateFlow<ConnectUiState> = _uiState.asStateFlow()

    init {
        connect()
    }

    fun connect() {
        viewModelScope.launch {
            _uiState.value = ConnectUiState.Connecting
            try {
                val info = repository.fetchInfo()
                _uiState.value = ConnectUiState.Connected(info)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: BadBotResponseException) {
                _uiState.value = ConnectUiState.Error(ErrorKind.BadResponse)
            } catch (_: UnreachableBotException) {
                _uiState.value = ConnectUiState.Error(ErrorKind.Unreachable)
            } catch (_: Exception) {
                _uiState.value = ConnectUiState.Error(ErrorKind.Unreachable)
            }
        }
    }
}
