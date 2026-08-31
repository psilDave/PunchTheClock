package com.psildave.punchtheclock.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.psildave.punchtheclock.shared.constants.IntentConstants
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.theme.PunchTheClockTheme
import com.psildave.punchtheclock.ui.utils.titleRes
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the Wear OS application.
 *
 * Handles incoming intents (e.g., from notifications) and sets up the root UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * ViewModel that manages the app logic and state.
     */
    private val viewModel: PunchClockViewModel by viewModels()

    /**
     * Initializes the activity, processes the intent, and sets the Compose content.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        setContent {
            PunchTheClockTheme(viewModel.selectedType) {
                PunchTheClockApp(viewModel = viewModel)
            }
        }
    }

    /**
     * Processes new intents when the activity is already running.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    /**
     * Extracts punch information from the provided intent extras.
     *
     * @param intent The intent to process.
     */
    private fun handleIntent(intent: Intent?) {
        val typeString = intent?.getStringExtra(IntentConstants.EXTRA_PUNCH_TYPE)
        val labelString = intent?.getStringExtra(IntentConstants.EXTRA_PUNCH_LABEL)

        if (typeString == null && labelString == null) return

        val punchType = try {
            if (typeString != null) PunchType.valueOf(typeString) else PunchType.CLOCK_IN
        } catch (_: Exception) {
            PunchType.OTHER
        }

        val punchLabel = labelString ?: getString(punchType.titleRes)

        // Update the ViewModel with the new intent data.
        viewModel.updateFromIntent(punchType, punchLabel)
    }
}
