package com.theboxx.app.ui.navigation

import com.theboxx.app.ui.screen.Screen

sealed interface NavigationEvent {

    data class SetCurrentScreen(val screen: Screen): NavigationEvent

    data class SetAppSearchEnabled(val enabled: Boolean = true): NavigationEvent

}