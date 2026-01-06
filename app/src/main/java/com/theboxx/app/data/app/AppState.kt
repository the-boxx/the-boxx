package com.theboxx.app.data.app

import com.theboxx.app.data.system.packages.UserApps

data class AppState(
    val apps: List<App> = emptyList(),

    val app: App = App("", true),

    val isLoading: Boolean = true,

    val packageName: String = "",
    val allowOperation: Boolean = true,

    val installedApps: List<UserApps> = emptyList(),
    val filteredInstalledApps: List<UserApps> = emptyList(),
    val appFilterString: String = ""

    )
