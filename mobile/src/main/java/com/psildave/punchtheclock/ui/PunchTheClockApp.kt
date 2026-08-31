package com.psildave.punchtheclock.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.psildave.punchtheclock.ui.navigation.data.Route
import com.psildave.punchtheclock.ui.navigation.data.bottomNavigationItems
import com.psildave.punchtheclock.ui.screens.HistoryScreen
import com.psildave.punchtheclock.ui.screens.HomeScreen
import com.psildave.punchtheclock.ui.screens.SettingsScreen


/**
 * Root Composable for the mobile application.
 *
 * Sets up the [Scaffold] with a [NavigationBar] and configures the [NavHost] for screen transitions.
 *
 * @param punchViewModel ViewModel for punch-related operations.
 * @param settingsViewModel ViewModel for application settings.
 */
@Composable
fun PunchTheClockApp(punchViewModel: PunchViewModel, settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                bottomNavigationItems.forEach { topLevelRoute ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.hasRoute(topLevelRoute.route::class)
                    } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                topLevelRoute.icon,
                                contentDescription = stringResource(topLevelRoute.nameRes)
                            )
                        },
                        label = { Text(stringResource(topLevelRoute.nameRes)) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(topLevelRoute.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                }
            }
        }) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Route.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.Home> {
                HomeScreen(
                    punchViewModel = punchViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
            composable<Route.History> { HistoryScreen(punchViewModel = punchViewModel) }
            composable<Route.Settings> { SettingsScreen(settingsViewModel = settingsViewModel) }
        }

    }


}