package com.psildave.punchtheclock.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.psildave.punchtheclock.shared.model.PunchType


private val LightColorScheme = lightColorScheme(
    primary = md_light_primary,
    onPrimary = md_light_onPrimary,
    primaryContainer = md_light_primaryContainer,
    onPrimaryContainer = md_light_onPrimaryContainer,
    secondary = md_light_secondary,
    onSecondary = md_light_onSecondary,
    secondaryContainer = md_light_secondaryContainer,
    onSecondaryContainer = md_light_onSecondaryContainer,
    tertiary = md_light_tertiary,
    onTertiary = md_light_onTertiary,
    tertiaryContainer = md_light_tertiaryContainer,
    onTertiaryContainer = md_light_onTertiaryContainer,
    error = md_light_error,
    onError = md_light_onError,
    errorContainer = md_light_errorContainer,
    onErrorContainer = md_light_onErrorContainer,
    background = md_light_background,
    onBackground = md_light_onBackground,
    surface = md_light_surface,
    onSurface = md_light_onSurface,
    surfaceVariant = md_light_surfaceContainerHigh,
    onSurfaceVariant = md_light_onSurfaceVariant,
    surfaceTint = md_light_primary,
    inverseSurface = md_light_inverseSurface,
    inverseOnSurface = md_light_inverseOnSurface,
    inversePrimary = md_light_inversePrimary,
    outline = md_light_outline,
    outlineVariant = md_light_outlineVariant,
    scrim = md_light_scrim,
)
private val DarkColorScheme = darkColorScheme(
    primary = md_dark_primary,
    onPrimary = md_dark_onPrimary,
    primaryContainer = md_dark_primaryContainer,
    onPrimaryContainer = md_dark_onPrimaryContainer,
    secondary = md_dark_secondary,
    onSecondary = md_dark_onSecondary,
    secondaryContainer = md_dark_secondaryContainer,
    onSecondaryContainer = md_dark_onSecondaryContainer,
    tertiary = md_dark_tertiary,
    onTertiary = md_dark_onTertiary,
    tertiaryContainer = md_dark_tertiaryContainer,
    onTertiaryContainer = md_dark_onTertiaryContainer,
    error = md_dark_error,
    onError = md_dark_onError,
    errorContainer = md_dark_errorContainer,
    onErrorContainer = md_dark_onErrorContainer,
    background = md_dark_background,
    onBackground = md_dark_onBackground,
    surface = md_dark_surface,
    onSurface = md_dark_onSurface,
    surfaceVariant = md_dark_surfaceContainerHigh,
    onSurfaceVariant = md_dark_onSurfaceVariant,
    surfaceTint = md_dark_primary,
    inverseSurface = md_dark_inverseSurface,
    inverseOnSurface = md_dark_inverseOnSurface,
    inversePrimary = md_dark_inversePrimary,
    outline = md_dark_outline,
    outlineVariant = md_dark_outlineVariant,
    scrim = md_dark_scrim,
)

data class PunchColors(
    val clockIn: Color,
    val clockInContainer: Color,
    val onClockIn: Color,
    val lunch: Color,
    val lunchContainer: Color,
    val onLunch: Color,
    val clockOut: Color,
    val clockOutContainer: Color,
    val onClockOut: Color,
    val manual: Color,
    val manualContainer: Color,
    val onManual: Color,
)

private val DefaultPunchColors = PunchColors(
    clockIn = ClockIn,
    clockInContainer = ClockInContainer,
    onClockIn = OnClockIn,
    lunch = Lunch,
    lunchContainer = LunchContainer,
    onLunch = OnLunch,
    clockOut = ClockOut,
    clockOutContainer = ClockOutContainer,
    onClockOut = OnClockOut,
    manual = Manual,
    manualContainer = ManualContainer,
    onManual = OnManual,
)
val LocalPunchColors = staticCompositionLocalOf { DefaultPunchColors }


@Composable
fun PunchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // Pinta a status bar com a cor de surface do tema e ajusta ícones
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    CompositionLocalProvider(LocalPunchColors provides DefaultPunchColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PunchTypography,
            shapes = PunchShapes,
            content = content,
        )
    }
}

@Composable
fun getPunchColor(punchType: String): Color {
    val punchColors = LocalPunchColors.current
    return when (punchType) {
        PunchType.CLOCK_IN.name -> punchColors.clockIn
        PunchType.LUNCH.name -> punchColors.lunch
        PunchType.CLOCK_OUT.name -> punchColors.clockOut
        else -> punchColors.manual
    }
}