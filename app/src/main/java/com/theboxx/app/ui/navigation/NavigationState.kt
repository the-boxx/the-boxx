package com.theboxx.app.ui.navigation

import com.theboxx.app.ui.screen.Screen

data class NavigationState(
    val currentScreen: Screen = NavigationScreens.Status,
    val isAppSearchEnabled: Boolean = false
)