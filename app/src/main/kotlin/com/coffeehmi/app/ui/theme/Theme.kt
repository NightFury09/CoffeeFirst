package com.coffeehmi.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DeepEspresso,
    secondary = Caramel,
    onSecondary = DeepEspresso,
    tertiary = Latte,
    background = DeepEspresso,
    surface = Espresso,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = Color(0x1AFFFFFF), // Used for glassmorphism
    onSurfaceVariant = TextSecondary,
    outline = GlassStroke
)

private val LightColorScheme = lightColorScheme(
    primary = Espresso,
    onPrimary = Foam,
    secondary = Caramel,
    onSecondary = Espresso,
    tertiary = Latte,
    background = Foam,
    surface = Color.White,
    onBackground = Color(0xFF1B110F),
    onSurface = Color(0xFF1B110F),
    surfaceVariant = Color(0x0D000000),
    onSurfaceVariant = Color(0xFF757575),
    outline = Color(0x33000000)
)

@Composable
fun CoffeeHMITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
