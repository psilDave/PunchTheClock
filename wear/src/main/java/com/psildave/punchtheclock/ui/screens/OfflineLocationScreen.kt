package com.psildave.punchtheclock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.psildave.punchtheclock.ui.theme.PunchTheClockTheme
import com.psildave.punchtheclock.ui.theme.WearShapeChip

/**
 * Screen displayed when GPS location cannot be acquired.
 *
 * Informs the user about the lack of signal and provides an option (placeholder) to save offline.
 */
@Composable
fun OfflineLocationScreen(
    punchType: String,
    punchLabel: String,
    onSaveOffline: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .background(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.alert),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.no_gps_signal),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.location_confirm_error),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onSaveOffline(punchType, punchLabel) },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = WearShapeChip,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text(
                text = stringResource(R.string.save_offline),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Preview for the [OfflineLocationScreen].
 */
@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun OfflineLocationScreenPreview() {
    PunchTheClockTheme {
        OfflineLocationScreen(
            punchType = "",
            punchLabel = "",
            onSaveOffline = { _, _ -> }
        )
    }
}