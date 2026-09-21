package com.kasimarketplace.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = KasiNavy,
    onPrimary = Color.White,
    secondary = KasiAmber,
    onSecondary = KasiTextPrimary,
    background = KasiBackground,
    surface = KasiSurface,
    error = KasiError
)

private val DarkColors = darkColorScheme(
    primary = KasiAmber,
    onPrimary = KasiTextPrimary,
    secondary = KasiNavy,
    error = KasiError
)

@Composable
fun KasiMarketplaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = KasiTypography,
        content = content
    )
}
