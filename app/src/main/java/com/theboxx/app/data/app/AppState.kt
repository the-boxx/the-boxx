package com.theboxx.app.data.app

data class AppState(
    val apps: List<App> = emptyList(),

    val app: App = App("", true),

    val isLoading: Boolean = true,

    val packageName: String = "",
    val allowOperation: Boolean = true

    )
