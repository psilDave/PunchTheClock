package com.psildave.punchtheclock.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.psildave.punchtheclock.shared.constants.IntentConstants
import com.psildave.punchtheclock.ui.screens.AlarmScreen
import com.psildave.punchtheclock.ui.theme.PunchTheClockTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity that displays a full-screen alarm UI using Compose.
 */
@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    private val viewModel: PunchClockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show over lock screen and wake up device
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        val time = intent.getStringExtra(IntentConstants.EXTRA_PUNCH_TIME) ?: ""
        val label = intent.getStringExtra(IntentConstants.EXTRA_PUNCH_LABEL) ?: ""

        setContent {
            PunchTheClockTheme {
                AlarmScreen(
                    time = time,
                    label = label,
                    onPunchClick = {
                        // The action is already handled by MainActivity if we finish and launch it,
                        // or we can handle it here and finish.
                        finish()
                    }
                )
            }
        }
    }
}
