package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SheBizPrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF381E8A),
    onPrimaryContainer = SheBizLavender,
    secondary = SheBizCoral,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF781E38),
    onSecondaryContainer = SheBizBlush,
    tertiary = SheBizGold,
    onTertiary = SheBizDark,
    background = SheBizDarkBackground,
    onBackground = Color.White,
    surface = SheBizDarkSurface,
    onSurface = Color.White,
    surfaceVariant = SheBizDarkSurface,
    onSurfaceVariant = SheBizDarkBody,
    outline = SheBizDarkBorder,
)

private val LightColorScheme = lightColorScheme(
    primary = SheBizPrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = SheBizLavender,
    onPrimaryContainer = SheBizPrimaryPurple,
    secondary = SheBizCoral,
    onSecondary = Color.White,
    secondaryContainer = SheBizBlush,
    onSecondaryContainer = SheBizCoral,
    tertiary = SheBizGold,
    onTertiary = SheBizDark,
    background = SheBizBackground,
    onBackground = SheBizHeading,
    surface = SheBizSurface,
    onSurface = SheBizHeading,
    surfaceVariant = SheBizSurfaceVariant,
    onSurfaceVariant = SheBizBody,
    outline = SheBizBorder,
)

@Composable
fun SheBizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
