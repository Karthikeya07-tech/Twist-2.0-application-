package com.example.twist20.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.twist20.ui.theme.Black
import com.example.twist20.ui.theme.Hairline
import com.example.twist20.ui.theme.Red
import com.example.twist20.ui.theme.White

@Composable
fun RedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Red,
            contentColor = White,
            disabledContainerColor = Red.copy(alpha = 0.45f),
            disabledContentColor = White.copy(alpha = 0.8f),
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
fun WhiteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Hairline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = Black,
            disabledContentColor = Black.copy(alpha = 0.4f),
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}
