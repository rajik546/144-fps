package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberGamingColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = CardSurfaceVariant,
    onPrimaryContainer = NeonCyan,
    secondary = NeonCyan,
    onSecondary = VoidBlack,
    secondaryContainer = CardSurfaceVariant,
    onSecondaryContainer = TextWhite,
    tertiary = CyberPink,
    onTertiary = Color.White,
    background = VoidBlack,
    onBackground = TextWhite,
    surface = CardSurface,
    onSurface = TextWhite,
    surfaceVariant = CardSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = CardBorder,
    outlineVariant = CardBorderGlow,
    error = CyberPink,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberGamingColorScheme,
        typography = Typography,
        content = content
    )
}
