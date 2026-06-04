package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PalaceGold,
    secondary = ImperialRed,
    tertiary = JadeGreen,
    background = CalligraphyInk,
    surface = CharcoalGrey,
    onBackground = PaperBg,
    onSurface = PaperBg
)

private val LightColorScheme = lightColorScheme(
    primary = ImperialRed,
    secondary = PalaceGold,
    tertiary = JadeGreen,
    background = PaperBg,
    surface = SoftParchment,
    onPrimary = PaperBg,
    onSecondary = CalligraphyInk,
    onBackground = CalligraphyInk,
    onSurface = CalligraphyInk
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep customizable but default to style-cohesive light parchment theme in standard setups
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
