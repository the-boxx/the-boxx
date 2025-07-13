package com.theboxx.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Upsert
    suspend fun upsertApp(app: App)

    @Delete
    suspend fun deleteApp(app: App)

    @Query("SELECT * FROM app WHERE packageName = :packageName")
    suspend fun getApp(packageName: String): App?

    @Query("SELECT * FROM app WHERE allowOperation = 0")
    fun getBlockedApps(): List<App>

//    @Query("SELECT * FROM app WHERE json_extract(profiles, '$.profile') = :currentProfile AND json_extract(profiles, '$.allowOperation') = 0")
//    @Query("SELECT * FROM app WHERE packageName IN (SELECT appOwnerPackageName FROM app_profiles WHERE profile = :currentProfile AND allowOperation = 0)")
//    fun getBlockedApps(currentProfile: Int): List<AppWithProfiles>

    @Query("SELECT * FROM app")
    fun getApps(): Flow<List<App>>
}