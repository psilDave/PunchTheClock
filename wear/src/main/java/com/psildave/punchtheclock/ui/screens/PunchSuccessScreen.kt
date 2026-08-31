package com.psildave.punchtheclock.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.theme.LocalWearPunchColors
import com.psildave.punchtheclock.ui.theme.PunchTheClockTheme
import com.psildave.punchtheclock.ui.utils.titleRes

/**
 * Screen displayed after a punch is successfully recorded.
 *
 * @param time The time of the punch.
 * @param type The type of punch.
 */
@Composable
fun PunchSuccessScreen(time: String, type: PunchType) {
    val punchColors = LocalWearPunchColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = punchColors.glow,
                    ambientColor = punchColors.glow
                )
                .background(
                    color = punchColors.container,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.recorded_successfully),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$time · ${stringResource(type.titleRes)}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * Preview for the [PunchSuccessScreen].
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PunchSuccessScreenPreview() {
    PunchTheClockTheme(PunchType.CLOCK_IN) {
        PunchSuccessScreen("08:59 AM", PunchType.CLOCK_IN)
    }
}
