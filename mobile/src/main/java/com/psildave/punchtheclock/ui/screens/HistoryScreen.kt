package com.psildave.punchtheclock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.data.database.PunchEntity
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.PunchViewModel
import com.psildave.punchtheclock.ui.theme.ShapeFullPill
import com.psildave.punchtheclock.ui.theme.getPunchColor
import com.psildave.punchtheclock.ui.utils.titleRes

/**
 * Screen that displays a grouped history of all punch records.
 *
 * @param punchViewModel ViewModel used to retrieve the structured history data.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun HistoryScreen(punchViewModel: PunchViewModel, modifier: Modifier = Modifier) {

    val dailyHistories by punchViewModel.historyData.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.history_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.history_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
            dailyHistories.forEach { dailyHistory ->

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dailyHistory.dateText,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = dailyHistory.totalWorkedText,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(dailyHistory.punches) { punch ->
                    HistoryRowItem(punch)
                }

            }


        }
    }
}

/**
 * A simplified list item representing a single punch entry in the history list.
 *
 * @param punch The punch record entity to display.
 */
@Composable
fun HistoryRowItem(punch: PunchEntity) {
    val color = getPunchColor(punch.punchType)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(ShapeFullPill)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(PunchType.fromName(punch.punchType).titleRes),
                color = color,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Text(
            text = punch.timeString,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
