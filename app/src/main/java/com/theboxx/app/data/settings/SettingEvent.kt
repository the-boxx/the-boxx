package com.theboxx.app.data.settings

import android.content.Context

sealed interface SettingEvent {
    object SaveSetting: SettingEvent
    data class SetBoxxState(val boxxState: Boolean): SettingEvent
    data class SetTagId(val tagId: String?): SettingEvent
    data class SetIsTrusted(val isTrusted: Boolean): SettingEvent
    data class CompleteOnboarding(val onboarded: Boolean = true): SettingEvent
    object EmergencyUnlock: SettingEvent
    object ResetAllSettings: SettingEvent
    data class GetInstalledApps(val context: Context): SettingEvent
    object FilterInstalledApps: SettingEvent
    data class SetAppFilterString(val appFilterString: String = ""): SettingEvent
    data class GetApp(val packageName: String): SettingEvent
    object SaveApp: SettingEvent
    data class SetPackageName(val packageName: String): SettingEvent
    data class SetPackageAllow(val allowOperation: Boolean): SettingEvent
    data class UpdateAppInList(val filtered: Boolean, val index: Int, val allowOperation: Boolean): SettingEvent
}