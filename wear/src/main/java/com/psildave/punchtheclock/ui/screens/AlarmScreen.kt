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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.ui.theme.PunchTheClockTheme
import com.psildave.punchtheclock.ui.theme.WearShapeChip

/**
 * Full-screen alarm UI built with Compose, matching the user's rich notification design.
 */
@Composable
fun AlarmScreen(
    time: String,
    label: String,
    onPunchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon Circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.alert), // Using existing alarm-like icon
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time Text
        Text(
            text = time,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        // Title Text
        Text(
            text = label,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action Button
        Button(
            onClick = onPunchClick,
            shape = WearShapeChip,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = stringResource(R.string.punch_notification_action),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun AlarmScreenPreview() {
    PunchTheClockTheme {
        AlarmScreen(
            time = "09:00",
            label = "Hora da Entrada",
            onPunchClick = {}
        )
    }
}
