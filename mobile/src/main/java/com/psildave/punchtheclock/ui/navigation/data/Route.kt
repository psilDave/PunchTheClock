package com.psildave.punchtheclock.ui.navigation.data

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object Home : Route()

    @Serializable
    data object History : Route()

    @Serializable
    data object Settings : Route()
}