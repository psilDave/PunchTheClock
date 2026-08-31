package com.psildave.punchtheclock.ui.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.shared.model.Reminder
import com.psildave.punchtheclock.ui.SettingsViewModel
import com.psildave.punchtheclock.ui.theme.ShapeCard
import com.psildave.punchtheclock.ui.utils.titleRes
import java.util.Calendar
import java.util.Locale

/**
 * Screen for managing user profile and punch reminders.
 *
 * @param settingsViewModel ViewModel to manage settings state and actions.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val userName by settingsViewModel.userName.collectAsState()
    val remindersList by settingsViewModel.reminders.collectAsState()

    var showNameDialog by remember { mutableStateOf(false) }
    var reminderToEdit by remember { mutableStateOf<Reminder?>(null) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                Text(
                    text = stringResource(R.string.settings_profile),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )

                SettingsCard {
                    SettingsRow(
                        icon = Icons.Outlined.Person,
                        title = stringResource(R.string.settings_your_name),
                        subtitle = userName,
                        onClick = { showNameDialog = true }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.settings_reminders_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )
            }

            items(remindersList) { reminder ->
                ReminderItemCard(
                    time = reminder.time,
                    label = reminder.label,
                    isEnabled = reminder.isEnabled,
                    selectedDays = reminder.daysOfWeek ?: listOf(2, 3, 4, 5, 6),
                    onTimeClick = {
                        val parts = reminder.time.split(":")
                        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
                        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                val formatted = String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d",
                                    selectedHour,
                                    selectedMinute
                                )
                                settingsViewModel.updateReminder(reminder.copy(time = formatted))
                            },
                            hour,
                            minute,
                            true
                        ).show()
                    },
                    onLabelClick = { reminderToEdit = reminder },
                    onRemoveClick = { settingsViewModel.removeReminder(reminder.id) },
                    onToggle = { newState ->
                        settingsViewModel.updateReminder(reminder.copy(isEnabled = newState))
                    },
                    onDaysChange = { newDays ->
                        settingsViewModel.updateReminder(reminder.copy(daysOfWeek = newDays))
                    },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            item {
                AddReminderButton(
                    onClick = { settingsViewModel.addReminder() }
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        settingsViewModel.forceSyncToWatch()
                        Toast.makeText(
                            context,
                            context.getString(R.string.settings_sync_success),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.settings_sync_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    if (showNameDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text(stringResource(R.string.settings_edit_name)) },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    settingsViewModel.saveUserName(tempName)
                    showNameDialog = false
                }) {
                    Text(stringResource(R.string.settings_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }


    reminderToEdit?.let { reminder ->
        var tempLabel by remember { mutableStateOf(reminder.label) }
        var tempPunchType by remember { mutableStateOf(reminder.punchType) }
        var expandedDropdown by remember { mutableStateOf(false) }

        val availablePunchTypes = listOf(
            PunchType.CLOCK_IN.name,
            PunchType.LUNCH.name,
            PunchType.CLOCK_OUT.name,
            PunchType.OTHER.name
        )

        AlertDialog(
            onDismissRequest = { reminderToEdit = null },
            title = { Text(stringResource(R.string.settings_edit_reminder)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempLabel,
                        onValueChange = { tempLabel = it },
                        singleLine = true,
                        label = { Text(stringResource(R.string.settings_reminder_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = stringResource(PunchType.fromName(tempPunchType).titleRes),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.settings_action_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expandedDropdown = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = stringResource(R.string.settings_select_type)
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            availablePunchTypes.forEach { typeName ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(PunchType.fromName(typeName).titleRes)) },
                                    onClick = {
                                        tempPunchType = typeName
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    settingsViewModel.updateReminder(
                        reminder.copy(
                            label = tempLabel,
                            punchType = tempPunchType
                        )
                    )
                    reminderToEdit = null
                }) {
                    Text(stringResource(R.string.settings_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { reminderToEdit = null }) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }
}

/**
 * A stylized card container for settings groups.
 *
 * @param content The composable content to be displayed inside the card.
 */
@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = ShapeCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

/**
 * A row within a settings card representing a single configuration option.
 *
 * @param icon The icon to represent the setting.
 * @param title The main title of the setting.
 * @param subtitle The current value or description of the setting.
 * @param onClick Callback when the setting row is clicked.
 */
@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = stringResource(R.string.settings_edit_icon_desc),
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * A card representing an individual reminder item with toggle and edit actions.
 *
 * @param time The scheduled time for the reminder.
 * @param label The descriptive label of the reminder.
 * @param isEnabled Whether the reminder is currently active.
 * @param selectedDays List of days (Calendar.SUNDAY to Calendar.SATURDAY) when active.
 * @param onTimeClick Callback when the time badge is clicked.
 * @param onLabelClick Callback when the label is clicked.
 * @param onRemoveClick Callback when the remove button is clicked.
 * @param onToggle Callback when the enable/disable switch is toggled.
 * @param onDaysChange Callback when the selection of days changes.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun ReminderItemCard(
    time: String,
    label: String,
    isEnabled: Boolean,
    selectedDays: List<Int>?,
    onTimeClick: () -> Unit,
    onLabelClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDaysChange: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {

    val cardBackgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val timeBadgeColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val primaryColor = MaterialTheme.colorScheme.primary

    val daysToShow = selectedDays ?: listOf(2, 3, 4, 5, 6)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBackgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(timeBadgeColor)
                    .clickable { onTimeClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLabelClick() }
                    .padding(vertical = 4.dp)
            )

            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.settings_remove_reminder),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Toggle
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = primaryColor,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        DaysOfWeekSelector(
            selectedDays = daysToShow,
            onDaysChange = onDaysChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Component for selecting days of the week for a reminder.
 */
@Composable
fun DaysOfWeekSelector(
    selectedDays: List<Int>,
    onDaysChange: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        Calendar.SUNDAY to stringResource(R.string.day_sunday_short),
        Calendar.MONDAY to stringResource(R.string.day_monday_short),
        Calendar.TUESDAY to stringResource(R.string.day_tuesday_short),
        Calendar.WEDNESDAY to stringResource(R.string.day_wednesday_short),
        Calendar.THURSDAY to stringResource(R.string.day_thursday_short),
        Calendar.FRIDAY to stringResource(R.string.day_friday_short),
        Calendar.SATURDAY to stringResource(R.string.day_saturday_short)
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { (day, label) ->
            val isSelected = selectedDays.contains(day)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
                    )
                    .clickable {
                        val newList = if (isSelected) {
                            selectedDays.filter { it != day }
                        } else {
                            selectedDays + day
                        }
                        onDaysChange(newList.sorted())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = formatDaysList(selectedDays),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(60.dp)
        )
    }
}

@Composable
private fun formatDaysList(days: List<Int>): String {
    if (days.isEmpty()) return ""
    if (days.size == 7) return stringResource(R.string.days_all)
    if (days.size == 5 && !days.contains(Calendar.SATURDAY) && !days.contains(Calendar.SUNDAY)) {
        return stringResource(R.string.days_mon_to_fri)
    }
    return ""
}

/**
 * A button to trigger the addition of a new reminder.
 *
 * @param onClick Callback when the button is clicked.
 * @param modifier Modifier for layout customization.
 */
@Composable
fun AddReminderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = stringResource(R.string.settings_add_reminder))
    }
}