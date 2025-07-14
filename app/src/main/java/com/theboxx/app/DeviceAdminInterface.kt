package com.theboxx.app

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.theboxx.app.data.App

fun suspendPackages(context: Context, apps: List<App>) {
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponent = ComponentName(context, BoxxDeviceAdminReceiver::class.java)

    val appsToBlock: List<String> = apps.map {
        it.packageName
    }

    if (dpm.isDeviceOwnerApp(context.packageName) || dpm.isProfileOwnerApp(context.packageName)) {
        try {
            val failedPackages = dpm.setPackagesSuspended(adminComponent, appsToBlock.toTypedArray(), true)
            if (failedPackages.isNotEmpty()) {
                Log.w("DeviceAdmin", "Failed to suspend packages: ${failedPackages.joinToString()}")
            } else {
                Log.i("DeviceAdmin", "Packages suspended successfully: ${appsToBlock.joinToString()}")
            }
        } catch (e: SecurityException) {
            Log.e("DeviceAdmin", "Security Exception: Not device/profile owner or admin not active", e)
        }
    } else {
        Log.w("DeviceAdmin", "Not Device Owner or Profile Owner. Cannot suspend packages.")
        val intent = Intent(context, RequestDeviceAdminActivity::class.java)
        context.startActivity(intent)

    }

}

fun unsuspendPackages(context: Context, apps: List<App>) {
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponent = ComponentName(context, BoxxDeviceAdminReceiver::class.java)

    val appsToBlock: List<String> = apps.map {
        it.packageName
    }

    if (dpm.isDeviceOwnerApp(context.packageName)) { // || dpm.isProfileOwnerApp(context.packageName)
        try {
            val failedPackages = dpm.setPackagesSuspended(adminComponent, appsToBlock.toTypedArray(), false)
            if (failedPackages.isNotEmpty()) {
                Log.w("DeviceAdmin", "Failed to unsuspend packages: ${failedPackages.joinToString()}")
            } else {
                Log.i("DeviceAdmin", "Packages unsuspended successfully: ${appsToBlock.joinToString()}")
            }
        } catch (e: SecurityException) {
            Log.e("DeviceAdmin", "Security Exception: Not device/profile owner or admin not active", e)
        }
    } else {
        Log.w("DeviceAdmin", "Not Device Owner or Profile Owner. Cannot unsuspend packages.")
        val intent = Intent(context, RequestDeviceAdminActivity::class.java)
        context.startActivity(intent)

    }

}
