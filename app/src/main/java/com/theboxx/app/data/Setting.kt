package com.theboxx.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Setting(
    val boxxState: Boolean,

    @PrimaryKey(false)
    val id: Int = 0
)