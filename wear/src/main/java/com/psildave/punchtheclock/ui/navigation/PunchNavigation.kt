package com.psildave.punchtheclock.ui.navigation

/**
 * Routes for the Wear OS application navigation.
 */
object PunchRoutes {
    const val HOME = "home"
    const val SELECT_TYPE = "select_type"
    const val FETCHING_LOCATION = "fetching_location"
    const val SUCCESS = "success/{time}/{type}"
    const val NO_LOCATION = "no_location"

    /**
     * Helper to build the success route with parameters.
     */
    fun successRoute(time: String, type: String) = "success/$time/$type"
}
