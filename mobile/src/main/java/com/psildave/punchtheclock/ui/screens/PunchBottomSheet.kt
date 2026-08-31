package com.psildave.punchtheclock.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.theme.ShapeCard
import com.psildave.punchtheclock.ui.theme.ShapeFullPill
import com.psildave.punchtheclock.ui.theme.getPunchColor
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.psildave.punchtheclock.ui.utils.titleRes
import kotlin.time.Duration.Companion.milliseconds

/**
 * Bottom sheet for recording a punch event on the mobile app.
 * Matches the design with type selection and a success state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PunchBottomSheet(
    onDismiss: () -> Unit,
    onPunchConfirmed: (PunchType) -> Unit
) {
    var selectedType by remember { mutableStateOf<PunchType?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var punchTime by remember { mutableStateOf("") }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(2000.milliseconds)
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        AnimatedContent(
            targetState = isSuccess,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "punch_flow"
        ) { success ->
            if (success) {
                PunchSuccessContent(selectedType!!, punchTime)
            } else {
                PunchSelectionContent(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it },
                    onConfirm = {
                        val type = selectedType ?: PunchType.OTHER
                        punchTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                        onPunchConfirmed(type)
                        isSuccess = true
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PunchSelectionContent(
    selectedType: PunchType?,
    onTypeSelected: (PunchType) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.punch_bt_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.punch_bt_type_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Grid of punch types
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PunchTypeCard(PunchType.CLOCK_IN, selectedType == PunchType.CLOCK_IN, Modifier.weight(1f)) { onTypeSelected(it) }
                PunchTypeCard(PunchType.LUNCH, selectedType == PunchType.LUNCH, Modifier.weight(1f)) { onTypeSelected(it) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PunchTypeCard(PunchType.CLOCK_OUT, selectedType == PunchType.CLOCK_OUT, Modifier.weight(1f)) { onTypeSelected(it) }
                PunchTypeCard(PunchType.OTHER, selectedType == PunchType.OTHER, Modifier.weight(1f)) { onTypeSelected(it) }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onConfirm,
            enabled = selectedType != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = ShapeFullPill,
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedType?.let { getPunchColor(it.name) } ?: MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (selectedType == PunchType.OTHER) stringResource(R.string.punch_bt_manual) else stringResource(R.string.punch_bt_confirm),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PunchTypeCard(
    type: PunchType,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (PunchType) -> Unit
) {
    val color = getPunchColor(type.name)
    
    Surface(
        onClick = { onClick(type) },
        shape = ShapeCard,
        color = if (isSelected) color.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
            .height(64.dp)
            .then(
                if (isSelected) Modifier.border(2.dp, color.copy(alpha = 0.5f), ShapeCard) 
                else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = stringResource(type.titleRes),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun PunchSuccessContent(type: PunchType, time: String) {
    val color = getPunchColor(type.name)
    val typeTitle = stringResource(type.titleRes)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(color)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(R.string.punch_bt_recorded, typeTitle),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = time,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
