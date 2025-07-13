package com.theboxx.app.data

data class WhitelistPackages(
    val whitelistedPackages: Set<String> = setOf(
        "com.android.systemui",
        "com.samsung.accessibility",
        "com.samsung.android.biometrics.app.setting",
        "com.android.settings",
        "com.android.bluetooth",
        "com.android.nfc",
        "com.android.packageinstaller",
        "com.google.android.packageinstaller",
        "com.android.emergency",
        "com.oplus.sos",
    )
)
