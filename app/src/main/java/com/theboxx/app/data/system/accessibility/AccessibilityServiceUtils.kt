package com.theboxx.app.data.system.accessibility

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.theboxx.app.AppLaunchDetectionService

fun isAccessibilityServiceEnabled(context: Context): Boolean { // TODO Move to a more accessible place to check on startup
    val serviceComponentIdentifier = "${context.packageName}/${AppLaunchDetectionService::class.java.name}"

    val accessibilityEnabled = try {
        Settings.Secure.getInt(context.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
    } catch (e: Settings.SettingNotFoundException) {
        Log.e("AccessibilityCheck", "ACCESSIBILITY_ENABLED setting not found", e)
    }

    if (accessibilityEnabled == 0) {
        Log.d("AccessibilityCheck", "Accessibility is globally disabled.")
        return false
    }

    val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    if (enabledServicesSetting == null) {
        return false
    }
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)

    return colonSplitter.contains(serviceComponentIdentifier)
}