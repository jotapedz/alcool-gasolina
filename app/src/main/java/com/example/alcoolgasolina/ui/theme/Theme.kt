package com.example.alcoolgasolina.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorError = Color(0xFFB3261E)

private val DarkColorScheme = darkColorScheme(
    primary = TealAccent,
    onPrimary = NightBlue,
    primaryContainer = PetrolBlue,
    onPrimaryContainer = Mist,
    secondary = WarmAmber,
    onSecondary = NightBlue,
    secondaryContainer = DeepSurface,
    onSecondaryContainer = Mist,
    background = NightBlue,
    onBackground = Mist,
    surface = DeepSurface,
    onSurface = Mist,
    surfaceVariant = PetrolBlue,
    onSurfaceVariant = PaleMint,
    error = ColorError,
    onError = Mist
)

private val LightColorScheme = lightColorScheme(
    primary = PetrolBlue,
    onPrimary = Mist,
    primaryContainer = PaleMint,
    onPrimaryContainer = NightBlue,
    secondary = WarmAmber,
    onSecondary = NightBlue,
    secondaryContainer = Color(0xFFFFF0C8),
    onSecondaryContainer = NightBlue,
    background = Mist,
    onBackground = NightBlue,
    surface = Color(0xFFFFFFFF),
    onSurface = NightBlue,
    surfaceVariant = Color(0xFFDCE7EE),
    onSurfaceVariant = Slate,
    error = ColorError,
    onError = Mist
)

@Composable
fun AlcoolGasolinaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
