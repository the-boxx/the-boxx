package com.theboxx.app.data.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Setting(
    val boxxState: Boolean,

    val tagId: String?,

    val isOnboarded: Boolean = false,

    @PrimaryKey(false)
    val id: Int = 0
)