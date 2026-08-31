package com.psildave.punchtheclock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.theme.PunchTheClockTheme
import com.psildave.punchtheclock.ui.theme.WearShapeChip
import com.psildave.punchtheclock.ui.utils.titleRes

@Composable
fun SelectTypeScreen(onTypeSelected: (PunchType) -> Unit) {
    val listState = rememberScalingLazyListState()
    // Optimization: Cache entries to avoid repeated lookups
    val punchTypes = remember { PunchType.entries }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            autoCentering = AutoCenteringParams(itemIndex = 1)
        ) {
            item {
                Text(
                    text = stringResource(R.string.punch_type_selection_title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            // Optimization: Add keys for better performance during scroll
            items(punchTypes, key = { it.name }) { type ->
                Button(
                    onClick = { onTypeSelected(type) },
                    shape = WearShapeChip,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(color = type.color, shape = CircleShape)
                        )
                        Text(
                            text = stringResource(type.titleRes),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun SelectTypeScreenPreview() {
    PunchTheClockTheme {
        SelectTypeScreen(onTypeSelected = {})
    }
}
