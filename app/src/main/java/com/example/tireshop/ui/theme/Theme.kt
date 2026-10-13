package com.example.tireshop.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TireBlueLight,
    onPrimary = AsphalBg,
    primaryContainer = TireBlueContainer,
    onPrimaryContainer = TextPrimary,
    secondary = TireAmber,
    onSecondary = AsphalBg,
    secondaryContainer = TireAmberContainer,
    onSecondaryContainer = TextPrimary,
    background = AsphalBg,
    onBackground = TextPrimary,
    surface = SlateCard,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurface,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    error = RedLowStock,
    onError = TextPrimary
)

@Composable
fun TireShopTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = AsphalBg.toArgb()
                window.navigationBarColor = AsphalBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
