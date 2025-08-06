package com.theboxx.app.ui.navigation

import com.theboxx.app.ui.screen.Screen
import kotlinx.serialization.Serializable

@Serializable
object NavigationScreens {
    @Serializable
    object Onboarding: Screen("Get Started") {
        @Serializable
        object Main: Screen(Onboarding.title)

        @Serializable
        object Accessibility: Screen("Set up Permissions")

        @Serializable
        object Nfc: Screen("Set up NFC Tag")

        @Serializable
        object Apps: Screen("Set up Apps")

        @Serializable
        object EmergencyUnlock: Screen("Last Steps")
    }

    @Serializable
    object Status: Screen("Home")

    @Serializable
    object Settings: Screen("Settings") {
        @Serializable
        object Main: Screen(Settings.title)

        @Serializable
        object Apps: Screen("Apps")

        @Serializable
        object EmergencyUnlock: Screen("Emergency Unlock")

        @Serializable
        object NfcTag: Screen("Set NFC Tag")

        @Serializable
        object RestartOnboarding: Screen("Restart Onboarding")
    }

    @Serializable
    object Info: Screen("Info")
}