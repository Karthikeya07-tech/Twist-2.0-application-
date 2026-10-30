package com.example.twist20.ui.connect

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.twist20.R
import com.example.twist20.data.BotInfo
import com.example.twist20.ui.components.AppHeader
import com.example.twist20.ui.components.BotIdentityCard
import com.example.twist20.ui.components.QuadrupedMark
import com.example.twist20.ui.components.RedButton
import com.example.twist20.ui.components.WhiteButton
import com.example.twist20.ui.theme.Black
import com.example.twist20.ui.theme.Muted
import com.example.twist20.ui.theme.Red
import com.example.twist20.ui.theme.White

@Composable
fun ConnectScreen(
    state: ConnectUiState,
    onConnect: () -> Unit,
    onTakeControl: (BotInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.82f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseValue",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White),
    ) {
        AppHeader {
            Text(
                text = "Twist",
                color = Red,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                letterSpacing = (-0.5).sp,
            )
            Text(
                text = stringResource(R.string.brand_line),
                color = White.copy(alpha = 0.72f),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.6.sp,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                text = stringResource(R.string.headline),
                color = White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp,
                modifier = Modifier.padding(top = 14.dp),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            when (state) {
                ConnectUiState.Idle, ConnectUiState.Connecting -> {
                    ConnectingBlock(
                        connecting = state is ConnectUiState.Connecting,
                        pulse = if (state is ConnectUiState.Connecting) pulse else 1f,
                        onConnect = onConnect,
                    )
                }

                is ConnectUiState.Error -> {
                    ConnectingBlock(
                        connecting = false,
                        pulse = 1f,
                        onConnect = onConnect,
                        errorText = stringResource(
                            if (state.kind == ErrorKind.BadResponse) {
                                R.string.error_bad_response
                            } else {
                                R.string.error_unreachable
                            },
                        ),
                    )
                }

                is ConnectUiState.Connected -> {
                    BotIdentityCard(info = state.info)
                    Spacer(Modifier.height(20.dp))
                    RedButton(
                        text = stringResource(R.string.take_control),
                        onClick = { onTakeControl(state.info) },
                    )
                    Spacer(Modifier.height(10.dp))
                    WhiteButton(
                        text = stringResource(R.string.retry),
                        onClick = onConnect,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.info_endpoint),
                color = Muted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ConnectingBlock(
    connecting: Boolean,
    pulse: Float,
    onConnect: () -> Unit,
    errorText: String? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        QuadrupedMark(pulse = pulse)
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (connecting) {
                stringResource(R.string.connecting)
            } else {
                stringResource(R.string.connect_hint)
            },
            color = Muted,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )
        if (errorText != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = errorText,
                color = Black,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.height(24.dp))
        RedButton(
            text = if (connecting) {
                stringResource(R.string.connecting)
            } else {
                stringResource(R.string.connect)
            },
            onClick = onConnect,
            enabled = !connecting,
        )
    }
}
