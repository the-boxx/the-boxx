package com.theboxx.app

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import com.theboxx.app.data.App

fun suspendPackages(context: Context, apps: List<App>) {
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponent = ComponentName(context, BoxxDeviceAdminReceiver::class.java)



}
