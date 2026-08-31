package com.psildave.punchtheclock.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.psildave.punchtheclock.shared.model.PunchType

/**
 * Default color scheme for the Wear OS application based on Material 3.
 */
private val WearColorScheme = ColorScheme(
    primary = wear_primary,
    onPrimary = wear_onPrimary,
    primaryContainer = wear_primaryContainer,
    onPrimaryContainer = wear_onPrimaryContainer,
    secondary = wear_onSurfaceSecondary,
    onSecondary = wear_background,
    secondaryContainer = wear_surfaceContainerHigh,
    onSecondaryContainer = wear_onSurface,
    tertiary = wear_onSurfaceTertiary,
    onTertiary = wear_background,
    tertiaryContainer = wear_surfaceContainer,
    onTertiaryContainer = wear_onSurfaceSecondary,
    error = wear_error,
    onError = wear_onError,
    errorContainer = wear_errorContainer,
    onErrorContainer = wear_onErrorContainer,
    background = wear_background,
    onBackground = wear_onSurface,
    onSurface = wear_onSurface,
    onSurfaceVariant = wear_onSurfaceSecondary,
    outline = wear_outline,
    outlineVariant = wear_outlineVariant,
)


/**
 * Data class representing the specific colors used for a punch type in the Wear UI.
 *
 * @property kind The [PunchType] these colors belong to.
 * @property accent Primary brand/action color for this punch type.
 * @property container Background color for containers/cards.
 * @property onAccent Color for content displayed on top of the accent color.
 * @property glow Color used for glow/pulse effects.
 */
data class WearPunchColors(
    val kind: PunchType,
    val accent: Color,
    val container: Color,
    val onAccent: Color,
    val glow: Color,
) {
    companion object {
        /**
         * Provides the [WearPunchColors] configuration for a specific [PunchType].
         *
         * @param kind The type of punch.
         * @return The color configuration for the specified kind.
         */
        fun forKind(kind: PunchType) = when (kind) {
            PunchType.CLOCK_IN -> WearPunchColors(
                kind = kind,
                accent = ClockInWear,
                container = Color(0xFF002111),
                onAccent = Color(0xFF000000),
                glow = ClockInWear.copy(alpha = 0.18f),
            )

            PunchType.LUNCH -> WearPunchColors(
                kind = kind,
                accent = LunchWear,
                container = Color(0xFF2D1200),
                onAccent = Color(0xFF000000),
                glow = LunchWear.copy(alpha = 0.18f),
            )

            PunchType.CLOCK_OUT -> WearPunchColors(
                kind = kind,
                accent = ClockOutWear,
                container = Color(0xFF002D6E),
                onAccent = Color(0xFF000000),
                glow = ClockOutWear.copy(alpha = 0.18f),
            )

            PunchType.OTHER -> WearPunchColors(
                kind = kind,
                accent = OtherWear,
                container = Color(0xFF260059),
                onAccent = Color(0xFF000000),
                glow = OtherWear.copy(alpha = 0.18f),
            )
        }
    }
}

/**
 * CompositionLocal used to provide [WearPunchColors] throughout the UI tree.
 */
val LocalWearPunchColors = staticCompositionLocalOf {
    WearPunchColors.forKind(PunchType.CLOCK_IN)
}

/**
 * Main theme wrapper for the Wear OS application.
 *
 * Configures the [MaterialTheme] with a contextual color scheme based on the active [PunchType].
 *
 * @param punchType The current punch type to determine the theme's primary colors.
 * @param content The Composable content to be themed.
 */
@Composable
fun PunchTheClockTheme(
    punchType: PunchType = PunchType.CLOCK_IN,
    content: @Composable () -> Unit,
) {
    val punchColors = WearPunchColors.forKind(punchType)

    val contextualScheme = WearColorScheme.copy(
        primary = punchColors.accent,
        onPrimary = punchColors.onAccent,
        primaryContainer = punchColors.container,
        onPrimaryContainer = punchColors.accent,
    )
    CompositionLocalProvider(LocalWearPunchColors provides punchColors) {
        MaterialTheme(
            colorScheme = contextualScheme,
            typography = Typography,
            content = content,
        )
    }
}
