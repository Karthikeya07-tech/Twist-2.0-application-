package com.example.twist20.ui.control

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.twist20.R
import com.example.twist20.data.BotInfo
import com.example.twist20.data.humanizeAction
import com.example.twist20.data.robotStateLabel
import com.example.twist20.ui.components.AppHeader
import com.example.twist20.ui.components.RedButton
import com.example.twist20.ui.components.WhiteButton
import com.example.twist20.ui.theme.Black
import com.example.twist20.ui.theme.Hairline
import com.example.twist20.ui.theme.Muted
import com.example.twist20.ui.theme.Paper
import com.example.twist20.ui.theme.Red
import com.example.twist20.ui.theme.White

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ControlScreen(
    info: BotInfo,
    state: ControlUiState,
    onBack: () -> Unit,
    onPlay: (String) -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val quickMoves = listOf(
        stringResource(R.string.move_forward) to "forward",
        stringResource(R.string.move_back) to "back",
        stringResource(R.string.move_left) to "left",
        stringResource(R.string.move_right) to "right",
        stringResource(R.string.move_diagonal) to "diagonal",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Paper),
    ) {
        AppHeader {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = White,
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = info.displayName,
                        color = White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = info.firmware.ifBlank { "—" },
                        color = White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                StateChip(state = state.robotState, lastAction = state.lastAction)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.play_action),
                color = Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.walks_loop),
                color = Muted,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
            )

            ActionDropdown(
                actions = state.actions,
                selected = state.selectedAction,
                playing = state.lastAction,
                enabled = !state.busy,
                onSelect = onPlay,
            )

            if (!state.actionsFromBot) {
                Text(
                    text = stringResource(R.string.using_builtin_actions),
                    color = Muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            state.error?.let { kind ->
                Text(
                    text = stringResource(
                        when (kind) {
                            "unknown" -> R.string.action_unknown
                            "failed" -> R.string.action_failed
                            else -> R.string.error_unreachable
                        },
                    ),
                    color = Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }

            Spacer(Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.quick_moves),
                color = Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                quickMoves.forEach { (label, command) ->
                    WhiteButton(
                        text = label,
                        onClick = { onPlay(command) },
                        enabled = !state.busy,
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            RedButton(
                text = stringResource(R.string.stop),
                onClick = onStop,
                enabled = !state.busy,
            )
        }
    }
}

@Composable
private fun StateChip(state: Int?, lastAction: String?) {
    val playing = state == 2
    val label = buildString {
        append(robotStateLabel(state))
        if (!lastAction.isNullOrBlank() && playing) {
            append(" · ")
            append(lastAction.humanizeAction())
        }
    }
    Text(
        text = label,
        color = if (playing) White else White.copy(alpha = 0.9f),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (playing) Red else White.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun ActionDropdown(
    actions: List<String>,
    selected: String?,
    playing: String?,
    enabled: Boolean,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "chevron")
    val label = selected?.humanizeAction() ?: stringResource(R.string.choose_action)

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(White)
                .border(1.dp, if (expanded) Red else Hairline, RoundedCornerShape(16.dp))
                .clickable(enabled = enabled) { expanded = true }
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = if (selected == null) Muted else Black,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = Red,
                modifier = Modifier.rotate(rotation),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .background(White),
        ) {
            actions.forEachIndexed { index, action ->
                val active = action == playing || action == selected
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (active) Red else Hairline),
                            )
                            Text(
                                text = action.humanizeAction(),
                                color = if (active) Red else Black,
                                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelect(action)
                    },
                )
                if (index != actions.lastIndex) {
                    HorizontalDivider(color = Hairline)
                }
            }
        }
    }
}
