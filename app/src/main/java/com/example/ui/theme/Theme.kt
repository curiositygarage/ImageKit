package com.curiositygarage.imagekit.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val IosDarkColorScheme = darkColorScheme(
    primary = IosSystemBlue,
    onPrimary = IosLabelPrimary,
    primaryContainer = IosTertiarySystemBackgroundDark,
    onPrimaryContainer = IosLabelPrimary,
    secondary = IosSystemTeal,
    onSecondary = IosSystemBackgroundDark,
    secondaryContainer = IosSecondarySystemBackgroundDark,
    onSecondaryContainer = IosLabelPrimary,
    tertiary = IosSystemGreen,
    onTertiary = IosSystemBackgroundDark,
    tertiaryContainer = IosTertiarySystemBackgroundDark,
    onTertiaryContainer = IosLabelPrimary,
    background = IosSystemBackgroundDark,
    onBackground = IosLabelPrimary,
    surface = IosSecondarySystemBackgroundDark,
    onSurface = IosLabelPrimary,
    surfaceVariant = IosTertiarySystemBackgroundDark,
    onSurfaceVariant = IosLabelSecondary,
    surfaceContainer = IosSecondarySystemBackgroundDark,
    surfaceContainerHigh = IosTertiarySystemBackgroundDark,
    outline = IosSeparatorOpaque,
    outlineVariant = IosSeparator,
    error = IosSystemRed,
    onError = IosLabelPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = IosDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = IosSystemBackgroundDark.toArgb()
                window.navigationBarColor = IosSystemBackgroundDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
