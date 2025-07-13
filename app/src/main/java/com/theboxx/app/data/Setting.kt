package com.theboxx.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Setting(
    val boxxState: Boolean,
    val currentProfile: Int,

    @PrimaryKey(false)
    val id: Int = 0
)