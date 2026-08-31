package com.psildave.punchtheclock.ui.screens

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.ui.theme.LocalWearPunchColors
import com.psildave.punchtheclock.ui.theme.PunchTheClockTheme

/**
 * Screen displayed while the application is fetching the current GPS coordinates.
 *
 * Includes a pulsing animation and a progress indicator.
 */
@Composable
fun LocationLoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val punchColors = LocalWearPunchColors.current

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .background(
                        color = punchColors.glow,
                        shape = CircleShape
                    )
            )
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                indicatorColor = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainer,
                strokeWidth = 4.dp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.getting_location),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * Preview for the [LocationLoadingScreen].
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LocationLoadingScreenPreview() {
    PunchTheClockTheme {
        LocationLoadingScreen()
    }
}