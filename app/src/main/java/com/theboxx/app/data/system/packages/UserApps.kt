package com.theboxx.app.data.system.packages

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import com.theboxx.app.data.WhitelistPackages

data class UserApps(
    val appName: String,
    val packageName: String,
    val icon: Drawable?,
    var allowOperation: Boolean = true
) {
    companion object {
        fun getInstalledApps(context: Context): List<UserApps> {
            val packageManager = context.packageManager
            val appDetailsList = mutableListOf<UserApps>()

            val applications: List<ApplicationInfo> = try {
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            } catch (e: Exception) {
                Log.e("AppList", "Error getting installed applications", e)
                emptyList()
            }

            val whitelistedPackages = WhitelistPackages().whitelistedPackages

            for (appInfo in applications) {
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val packageName = appInfo.packageName
                if (appName != packageName && !whitelistedPackages.contains(packageName)) {
                    var icon: Drawable? = null
                    try {
                        icon = packageManager.getApplicationIcon(packageName)
                    } catch (e: PackageManager.NameNotFoundException) {
                        Log.e("AppList", "No icon found for $packageName", e)
                    }


                    appDetailsList.add(UserApps(appName, packageName, icon))
                }

            }

            appDetailsList.sortBy { it.appName.lowercase() }

            return appDetailsList
        }
    }
}