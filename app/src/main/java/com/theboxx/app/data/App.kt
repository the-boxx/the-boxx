package com.theboxx.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class App(
    @PrimaryKey(autoGenerate = false)
    val packageName: String,

    val allowOperation: Boolean = true

//
//    var profiles: MutableList<AppProfile>

)
