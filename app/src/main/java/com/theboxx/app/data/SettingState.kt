package com.theboxx.app.data

data class SettingState(
    // Defaults
    val settings: Setting = Setting(false, 0),

    val boxxState: Boolean = settings.boxxState,
    val currentProfile: Int = settings.currentProfile,

    val isLoading: Boolean = true,

    val isTrusted: Boolean = false

)