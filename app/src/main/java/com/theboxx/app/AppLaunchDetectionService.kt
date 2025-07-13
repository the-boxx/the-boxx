package com.theboxx.app;

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.theboxx.app.data.SettingDatabase
import com.theboxx.app.data.WhitelistPackages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppLaunchDetectionService : AccessibilityService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob) // Use Dispatchers.Main if updating UI, Dispatchers.IO for DB


    private val targetPackageNames = setOf(
            "app.adriel.learns",
            "de.danoeh.antennapod",
            "com.adriel.myapplication"
    )


    private val whitelistedPackages = WhitelistPackages().whitelistedPackages

//    private var previouslyForegroundPackage: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val settingDb by lazy {
            SettingDatabase.getDatabase(applicationContext)
        }


        if (event == null) return
        if (!whitelistedPackages.contains(event.packageName?.toString())) { // Absolutely skip packages that could be harmful to "block"

            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                val packageName = event.packageName?.toString()
                val className = event.className?.toString()

                if (packageName != null) {
//                    Log.d(
//                        "AppLaunchDetector",
//                        "Window changed: $packageName $className, Source: ${event.source}"
//                    )

                    serviceScope.launch(Dispatchers.IO) {

//                        val currentProfile: Int = settingDb.settingDao.getSettings().firstOrNull()?.currentProfile ?: 0
                        val blockedPackageNames = settingDb.appDao.getBlockedApps()
//                        Log.d("AppLaunchDetector", "Blocked packages: $blockedPackageNames")


                        if (blockedPackageNames.find { it.packageName == packageName } != null &&
//                            packageName != previouslyForegroundPackage &&
                            packageName != applicationContext.packageName
                        ) {
//                            Log.d("AppLaunchDetector", "App launched: $packageName")

//                            previouslyForegroundPackage = packageName

                                val currentSettings = settingDb.settingDao.getSusSettings()
                                // Launch Intent to show Block page

                                if (currentSettings?.boxxState == true || currentSettings?.boxxState == null) {

                                    val intent = Intent(
                                        this@AppLaunchDetectionService,
                                        BlockActivity::class.java
                                    ).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        putExtra("LAUNCHED_PACKAGE_NAME", packageName)
                                    }

                                    startActivity(intent)
                                } else {
                                    Log.d("AppLaunchDetector", "Currently unboxxed... skipping...")
                                }

                        } else if (packageName != applicationContext.packageName) {
                            if (blockedPackageNames.find { it.packageName == packageName } == null) {
//                                previouslyForegroundPackage = packageName
                            }
                        }
                    }
                }
            }
        } else {
            return
        }
    }

    override fun onInterrupt() {
        Log.d("AppLaunchDetector", "Accessibility service interrupted")
        serviceJob.cancel()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        }

        this.serviceInfo = info
        Log.d("AppLaunchDetector", "Accessibility service connected and configured")
    }

    override fun onDestroy() {
        super.onDestroy()

        serviceJob.cancel()
    }

}
