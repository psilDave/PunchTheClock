package com.psildave.punchtheclock.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.gms.location.LocationServices
import com.psildave.punchtheclock.R
import com.psildave.punchtheclock.shared.model.PunchType
import com.psildave.punchtheclock.ui.navigation.PunchRoutes
import com.psildave.punchtheclock.ui.screens.HomeScreen
import com.psildave.punchtheclock.ui.screens.LocationLoadingScreen
import com.psildave.punchtheclock.ui.screens.OfflineLocationScreen
import com.psildave.punchtheclock.ui.screens.PunchSuccessScreen
import com.psildave.punchtheclock.ui.screens.SelectTypeScreen
import com.psildave.punchtheclock.ui.utils.titleRes
import kotlinx.coroutines.launch

/**
 * Root Composable for the Wear OS application using Jetpack Navigation.
 */
@Composable
fun PunchTheClockApp(viewModel: PunchClockViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberSwipeDismissableNavController()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val updatedText = stringResource(R.string.status_updated)
    val offlineSavedText = stringResource(R.string.status_saved_offline)

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (isGranted) {
                navController.navigate(PunchRoutes.FETCHING_LOCATION)
                coroutineScope.launch {
                    val result = viewModel.performPunch(fusedLocationClient)
                    if (result != null) {
                        val (success, time) = result
                        if (success) {
                            navController.navigate(
                                PunchRoutes.successRoute(time, viewModel.selectedType.name)
                            ) {
                                popUpTo(PunchRoutes.HOME)
                            }
                        } else {
                            navController.navigate(PunchRoutes.NO_LOCATION) {
                                popUpTo(PunchRoutes.HOME)
                            }
                        }
                    } else {
                        navController.navigate(PunchRoutes.NO_LOCATION) {
                            popUpTo(PunchRoutes.HOME)
                        }
                    }
                }
            }
        }

    AppScaffold {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = PunchRoutes.HOME
        ) {
            composable(PunchRoutes.HOME) {
                // Collect state ONLY inside the Home screen to avoid global recompositions
                val uiState by viewModel.uiState.collectAsState()

                HomeScreen(
                    state = uiState,
                    onPunchClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onChangeTypeClick = { navController.navigate(PunchRoutes.SELECT_TYPE) }
                )
            }

            composable(PunchRoutes.SELECT_TYPE) {
                SelectTypeScreen(
                    onTypeSelected = { newType ->
                        viewModel.onTypeSelected(newType, updatedText)
                        navController.popBackStack()
                    }
                )
            }

            composable(PunchRoutes.FETCHING_LOCATION) {
                LocationLoadingScreen()
            }

            composable(PunchRoutes.SUCCESS) { backStackEntry ->
                val time = backStackEntry.arguments?.getString("time") ?: ""
                val typeName = backStackEntry.arguments?.getString("type") ?: ""
                val type = try {
                    PunchType.valueOf(typeName)
                } catch (_: Exception) {
                    PunchType.OTHER
                }
                PunchSuccessScreen(time, type)
            }

            composable(PunchRoutes.NO_LOCATION) {
                OfflineLocationScreen(
                    punchType = viewModel.selectedType.name,
                    punchLabel = stringResource(viewModel.selectedType.titleRes),
                    onSaveOffline = { type, label ->
                        viewModel.saveOffline(type, label, offlineSavedText)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
