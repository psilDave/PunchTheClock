package com.psildave.punchtheclock.ui.navigation.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.psildave.punchtheclock.R

data class TopLevelRoute<T : Any>(
    @StringRes val nameRes: Int,
    val route: T,
    val icon: ImageVector
)

val bottomNavigationItems = listOf(
    TopLevelRoute(R.string.nav_home, Route.Home, Icons.Default.Home),
    TopLevelRoute(R.string.nav_history, Route.History, Icons.AutoMirrored.Filled.List),
    TopLevelRoute(R.string.nav_settings, Route.Settings, Icons.Default.Settings)
)
