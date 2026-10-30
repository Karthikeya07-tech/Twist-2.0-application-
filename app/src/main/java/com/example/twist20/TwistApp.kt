package com.example.twist20

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.twist20.ui.connect.ConnectScreen
import com.example.twist20.ui.connect.ConnectViewModel
import com.example.twist20.ui.control.ControlScreen
import com.example.twist20.ui.control.ControlViewModel
import com.example.twist20.ui.theme.TwistTheme

@Composable
fun TwistApp(
    session: SessionViewModel = viewModel(),
    connectViewModel: ConnectViewModel = viewModel(),
) {
    TwistTheme {
        val navController = rememberNavController()
        val connectState by connectViewModel.uiState.collectAsStateWithLifecycle()

        NavHost(
            navController = navController,
            startDestination = "connect",
            modifier = Modifier.fillMaxSize(),
        ) {
            composable("connect") {
                ConnectScreen(
                    state = connectState,
                    onConnect = connectViewModel::connect,
                    onTakeControl = { info ->
                        session.rememberBot(info)
                        navController.navigate("control")
                    },
                )
            }
            composable("control") {
                val bot = session.bot
                if (bot == null) {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                } else {
                    val controlViewModel: ControlViewModel = viewModel()
                    val controlState by controlViewModel.uiState.collectAsStateWithLifecycle()
                    ControlScreen(
                        info = bot,
                        state = controlState,
                        onBack = { navController.popBackStack() },
                        onPlay = controlViewModel::play,
                        onStop = controlViewModel::stop,
                    )
                }
            }
        }
    }
}
