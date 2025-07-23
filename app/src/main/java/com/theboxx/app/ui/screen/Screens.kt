package com.theboxx.app.ui.screen

import kotlinx.serialization.Serializable
class Screens(
    val title: String,
    val screen: Any
) {

    @Serializable
    object StatusScreen

    @Serializable
    object SettingsScreen {
        @Serializable
        object Main

        @Serializable
        object Apps

        @Serializable
        object EmergencyUnlock
    }

    @Serializable
    object InfoScreen

}