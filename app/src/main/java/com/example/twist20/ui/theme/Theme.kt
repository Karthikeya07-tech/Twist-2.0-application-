package com.example.twist20.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TwistColors = lightColorScheme(
    primary = Red,
    onPrimary = White,
    primaryContainer = RedDark,
    onPrimaryContainer = White,
    secondary = Black,
    onSecondary = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    surfaceVariant = Field,
    onSurfaceVariant = Muted,
    outline = Hairline,
    error = Red,
)

@Composable
fun TwistTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = false
            insets.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = TwistColors,
        typography = Typography,
        content = content,
    )
}
