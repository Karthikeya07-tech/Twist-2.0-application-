package com.example.twist20.ui.control

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.twist20.data.ActionFailedException
import com.example.twist20.data.ActionReply
import com.example.twist20.data.BotRepository
import com.example.twist20.data.FALLBACK_ACTIONS
import com.example.twist20.data.UnknownActionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class ControlUiState(
    val actions: List<String> = FALLBACK_ACTIONS,
    val actionsFromBot: Boolean = false,
    val selectedAction: String? = null,
    val lastAction: String? = null,
    val robotState: Int? = null,
    val busy: Boolean = false,
    val error: String? = null,
)

class ControlViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BotRepository(application)

    private val _uiState = MutableStateFlow(ControlUiState())
    val uiState: StateFlow<ControlUiState> = _uiState.asStateFlow()

    init {
        refreshActions()
    }

    fun refreshActions() {
        viewModelScope.launch {
            try {
                val actions = repository.fetchActions()
                if (actions.isNotEmpty()) {
                    _uiState.update {
                        it.copy(actions = actions, actionsFromBot = true, error = null)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { it.copy(actionsFromBot = false) }
            }
        }
    }

    fun play(name: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(busy = true, selectedAction = name, error = null)
            }
            try {
                applyReply(name, repository.playAction(name))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: UnknownActionException) {
                _uiState.update {
                    it.copy(busy = false, error = "unknown")
                }
            } catch (_: ActionFailedException) {
                _uiState.update {
                    it.copy(busy = false, error = "failed")
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(busy = false, error = "unreachable")
                }
            }
        }
    }

    fun stop() {
        viewModelScope.launch {
            _uiState.update { it.copy(busy = true, error = null) }
            try {
                val reply = repository.stop()
                _uiState.update {
                    it.copy(
                        busy = false,
                        lastAction = null,
                        robotState = reply.state ?: 1,
                        error = null,
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(busy = false, error = "unreachable")
                }
            }
        }
    }

    private fun applyReply(sent: String, reply: ActionReply) {
        _uiState.update {
            it.copy(
                busy = false,
                lastAction = reply.action ?: sent,
                robotState = reply.state,
                error = null,
            )
        }
    }
}
