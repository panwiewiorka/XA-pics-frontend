package xapics.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = BG,
    secondary = GrayMedium,
    tertiary = GrayDark,
    background = BG,
    onBackground = AlmostWhite,
    error = MyError,
    surface = BG,
    surfaceTint = GrayLight,
    onSurface = AlmostWhite,
    surfaceVariant = GrayDark,

    primaryContainer = Color.Black.copy(alpha = 0.4f),
    onPrimaryContainer = Color.Green,
    onSecondary = Color.Magenta,
    secondaryContainer = Color.Yellow,
    onSecondaryContainer = Color.Blue,
    onTertiary = Color.Green,
    tertiaryContainer = Color.Blue,
    onTertiaryContainer = Color.White, // TODO remove all unnecessary
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = BG,
    secondary = GrayLight,
    tertiary = GrayLighter,
    background = AlmostWhite,
    onBackground = BG,
    error = MyError,
    surface = AlmostWhite,
    surfaceTint = GrayMedium,
    onSurface = BG,
    surfaceVariant = GrayLighter,

    primaryContainer = Color.White.copy(alpha = 0.4f),
    onPrimaryContainer = Color.Cyan,
    onSecondary = Color.Magenta,
    secondaryContainer = Color.Yellow,
    onSecondaryContainer = Color.Blue,
    onTertiary = Color.Green,
    tertiaryContainer = Color.Cyan,
    onTertiaryContainer = Color.Cyan, // TODO remove all unnecessary
)

@Composable
fun XAPicsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}