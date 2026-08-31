package com.psildave.punchtheclock.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.psildave.punchtheclock.ui.theme.PunchTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point of the mobile application.
 *
 * Sets up the Compose UI content and initializes the root [PunchTheClockApp].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PunchTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val punchViewModel: PunchViewModel = hiltViewModel()
                    val settingsViewModel: SettingsViewModel = hiltViewModel()
                    PunchTheClockApp(punchViewModel = punchViewModel, settingsViewModel)
                }
            }
        }
    }
}