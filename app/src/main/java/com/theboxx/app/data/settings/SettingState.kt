package com.theboxx.app.data.settings

data class SettingState(
    // Defaults
    val settings: Setting = Setting(false, "", false, 0),

    val boxxState: Boolean = settings.boxxState,

    val tagId: String? = settings.tagId,

    val emergencyUnlocks: Int = settings.emergencyUnlocks,

    val isOnboarded: Boolean = true,

    val isLoading: Boolean = true,

    val isTrusted: Boolean = false

)