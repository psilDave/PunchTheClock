package com.psildave.punchtheclock.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.data.database.PunchEntity
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.PunchViewModel
import com.psildave.punchtheclock.ui.SettingsViewModel
import com.psildave.punchtheclock.ui.data.PunchClockUiState
import com.psildave.punchtheclock.ui.theme.ShapeCard
import com.psildave.punchtheclock.ui.theme.ShapeFullPill
import com.psildave.punchtheclock.ui.theme.getPunchColor
import com.psildave.punchtheclock.ui.utils.titleRes

/**
 * Main screen of the mobile application showing user greetings, daily summary, and punch timeline.
 */
@Composable
fun HomeScreen(
    punchViewModel: PunchViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val punches by punchViewModel.allPunches.collectAsState()
    val todayState by punchViewModel.todayWorkedHoursState.collectAsState()
    val userName by settingsViewModel.userName.collectAsState()
    
    var showPunchSheet by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (isGranted) {
            showPunchSheet = true
        }
    }

    if (showPunchSheet) {
        PunchBottomSheet(
            onDismiss = { showPunchSheet = false },
            onPunchConfirmed = { punchViewModel.onPunchConfirmed(it) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = ShapeFullPill,
                icon = { Icon(Icons.Default.Schedule, null) },
                text = { Text(stringResource(R.string.punch_bt_title)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.home_hello),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = ShapeCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.home_daily_summary),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    when (val state = todayState) {
                        is PunchClockUiState.Loading -> {
                            Text(
                                text = "...",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        is PunchClockUiState.Success -> {
                            val text = if (state.isTodayEmpty) {
                                stringResource(state.statusResId)
                            } else {
                                stringResource(state.statusResId, state.totalWorkedText)
                            }
                            Text(
                                text = text,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(punches.sortedByDescending { it.timestamp }) { punch ->
                    HomeTimelineCard(punch)
                }
            }
        }
    }
}

/**
 * A card representing a single punch event in the timeline.
 */
@Composable
fun HomeTimelineCard(punch: PunchEntity) {
    val dotColor = getPunchColor(punch.punchType)
    val lineColor = MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {

        Box(
            modifier = Modifier
                .width(32.dp)
                .fillMaxHeight()
                .drawBehind {
                    drawLine(
                        color = lineColor,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 2f
                    )
                },
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(12.dp)
                    .clip(ShapeFullPill)
                    .background(dotColor)
            )
        }

        Surface(
            shape = ShapeCard,
            color = dotColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = ShapeCard,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = punch.timeString,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(PunchType.fromName(punch.punchType).titleRes),
                                color = dotColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = ShapeFullPill
                        ) {
                            Text(
                                text = stringResource(R.string.home_recorded),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = stringResource(R.string.home_location_desc),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = punch.locationAddress,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
