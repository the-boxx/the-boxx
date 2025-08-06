package com.theboxx.app.data.settings

sealed interface SettingEvent {
    object SaveSetting: SettingEvent
    data class SetBoxxState(val boxxState: Boolean): SettingEvent
    data class SetTagId(val tagId: String?): SettingEvent
    data class SetIsTrusted(val isTrusted: Boolean): SettingEvent
    data class CompleteOnboarding(val onboarded: Boolean = true): SettingEvent
    data class GetApp(val packageName: String): SettingEvent
    object SaveApp: SettingEvent
    data class SetPackageName(val packageName: String): SettingEvent
    data class SetPackageAllow(val allowOperation: Boolean): SettingEvent
//    data class SetAppProfile(val profile: AppProfile): SettingEvent
}