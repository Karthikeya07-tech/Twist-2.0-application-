package com.example.twist20.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.twist20.ui.theme.Black
import com.example.twist20.ui.theme.Red

@Composable
fun QuadrupedMark(
    modifier: Modifier = Modifier,
    pulse: Float = 1f,
) {
    Canvas(modifier = modifier.size(120.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 5f * pulse, cap = StrokeCap.Round)

        drawRoundRect(
            color = Red.copy(alpha = 0.12f * pulse),
            topLeft = Offset(w * 0.22f, h * 0.38f),
            size = Size(w * 0.56f, h * 0.22f),
            cornerRadius = CornerRadius(18f, 18f),
        )
        drawRoundRect(
            color = Black,
            topLeft = Offset(w * 0.22f, h * 0.38f),
            size = Size(w * 0.56f, h * 0.22f),
            cornerRadius = CornerRadius(18f, 18f),
            style = stroke,
        )
        drawRoundRect(
            color = Black,
            topLeft = Offset(w * 0.68f, h * 0.30f),
            size = Size(w * 0.18f, h * 0.16f),
            cornerRadius = CornerRadius(12f, 12f),
            style = stroke,
        )

        val legs = listOf(
            Offset(w * 0.30f, h * 0.60f) to Offset(w * 0.24f, h * 0.82f),
            Offset(w * 0.40f, h * 0.60f) to Offset(w * 0.42f, h * 0.84f),
            Offset(w * 0.58f, h * 0.60f) to Offset(w * 0.56f, h * 0.84f),
            Offset(w * 0.68f, h * 0.60f) to Offset(w * 0.74f, h * 0.82f),
        )
        legs.forEach { (start, end) ->
            drawLine(Black, start, end, strokeWidth = 5f * pulse, cap = StrokeCap.Round)
        }
    }
}
