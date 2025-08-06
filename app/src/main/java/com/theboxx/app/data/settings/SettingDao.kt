package com.theboxx.app.data.settings

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {
    @Upsert
    suspend fun upsertSetting(setting: Setting)

    @Delete // Only to be used for resetting app settings I guess
    suspend fun deleteSetting(setting: Setting)

    @Query("SELECT * FROM setting")
    fun getSettings(): Flow<Setting>

    @Query("SELECT * FROM setting")
    suspend fun getSusSettings(): Setting?

}