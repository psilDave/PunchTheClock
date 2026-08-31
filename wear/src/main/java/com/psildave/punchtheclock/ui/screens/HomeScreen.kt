package com.psildave.punchtheclock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.data.PunchClockUiState
import com.psildave.punchtheclock.ui.theme.PunchTheClockTheme
import com.psildave.punchtheclock.ui.theme.WearShapeChip
import com.psildave.punchtheclock.ui.theme.WearShapePunchButton
import com.psildave.punchtheclock.ui.utils.titleRes

/**
 * Main screen for the Wear OS application, allowing the user to punch the clock.
 *
 * @param state The current UI state containing punch type, current time, and next action text.
 * @param onPunchClick Callback triggered when the main punch button is clicked.
 * @param onChangeTypeClick Callback triggered when the user clicks to change the punch type.
 */
@Composable
fun HomeScreen(
    state: PunchClockUiState,
    onPunchClick: () -> Unit,
    onChangeTypeClick: () -> Unit
) {
    val typeColor = state.type.color

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.currentTime,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onChangeTypeClick,
            shape = WearShapeChip,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.height(32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(typeColor, CircleShape)
                )
                Text(
                    text = stringResource(state.type.titleRes),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    painter = painterResource(R.drawable.arrow_down),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onPunchClick,
            shape = WearShapePunchButton,
            colors = ButtonDefaults.buttonColors(
                containerColor = typeColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = WearShapePunchButton,
                    spotColor = typeColor.copy(alpha = 0.25f),
                    ambientColor = typeColor.copy(alpha = 0.25f)
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(R.drawable.clock),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = stringResource(state.type.titleRes),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = state.nextAction,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * Preview for the [HomeScreen] Composable.
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun HomeScreenPreview() {
    PunchTheClockTheme {
        HomeScreen(
            state = PunchClockUiState(
                PunchType.LUNCH,
                "08:59 AM",
                stringResource(
                    R.string.home_next_action_label,
                    stringResource(PunchType.CLOCK_OUT.titleRes)
                )
            ),
            onPunchClick = { },
            onChangeTypeClick = { }
        )
    }
}
