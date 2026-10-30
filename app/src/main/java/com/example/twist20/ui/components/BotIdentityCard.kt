package com.example.twist20.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.twist20.R
import com.example.twist20.data.BotInfo
import com.example.twist20.ui.theme.Black
import com.example.twist20.ui.theme.Hairline
import com.example.twist20.ui.theme.Muted
import com.example.twist20.ui.theme.Red
import com.example.twist20.ui.theme.White

@Composable
fun BotIdentityCard(
    info: BotInfo,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(White)
            .border(1.dp, Hairline, RoundedCornerShape(20.dp))
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Red),
                )
                Text(
                    text = stringResource(R.string.live).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Black,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            StageChip(label = info.stageLabel)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = info.displayName,
            style = MaterialTheme.typography.titleLarge,
            color = Black,
        )

        val unprovisioned = stringResource(R.string.unprovisioned)
        val subtitle = buildString {
            append(info.type.ifBlank { "Twist" })
            if (!info.provisioned) {
                append("  ·  ")
                append(unprovisioned)
            }
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = Hairline,
        )

        SpecRow(stringResource(R.string.label_firmware), info.firmware.dash())
        SpecRow(stringResource(R.string.label_mac), info.mac.dash())
        SpecRow(stringResource(R.string.label_type), info.type.dash())
        SpecRow(stringResource(R.string.label_stage), info.stageLabel)
        SpecRow(stringResource(R.string.label_ssid), info.ssid.dash())
        SpecRow(stringResource(R.string.label_ip), info.ip.dash())
        if (info.serial.isNotBlank()) {
            SpecRow(stringResource(R.string.label_serial), info.serial)
        }
    }
}

@Composable
private fun StageChip(label: String) {
    Text(
        text = label.uppercase(),
        color = White,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.4.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Red)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Muted,
        )
        Text(
            text = value,
            color = Black,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun String.dash(): String = ifBlank { "—" }
