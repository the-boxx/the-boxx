package com.theboxx.app.data.settings

import com.theboxx.app.ui.navigation.NavigationScreens

data class SettingState(
    // Defaults
    val settings: Setting = Setting(false, "", false),

    val boxxState: Boolean = settings.boxxState,

    val tagId: String? = "",

    val hasSetNewTag: Boolean = true,

    val isOnboarded: Boolean = true,

    val currentScreen: Any = NavigationScreens.Status,

    val isLoading: Boolean = true,

    val isTrusted: Boolean = false

)