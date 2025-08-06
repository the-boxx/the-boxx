package com.theboxx.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.theboxx.app.data.settings.Setting
import com.theboxx.app.data.settings.SettingDao
import com.theboxx.app.data.settings.SettingState
import com.theboxx.app.data.app.App
import com.theboxx.app.data.app.AppDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        Setting::class,
        App::class,
               ],
    version = 1
)
abstract class SettingDatabase: RoomDatabase() {

    abstract val settingDao: SettingDao
    abstract val appDao: AppDao

    companion object {
        @Volatile
        private var Instance: SettingDatabase? = null

        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun getDatabase(context: Context): SettingDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SettingDatabase::class.java, "settings_database")
                    .openHelperFactory(FrameworkSQLiteOpenHelperFactory())
                    .addCallback(object : Callback() {
                        override fun onCreate (db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val settingDao = getDatabase(context).settingDao
                            val appDao = getDatabase(context).appDao
                            applicationScope.launch {
                                appDao.upsertApp(
                                    App("com.theboxx.app", true)
                                )
                                settingDao.upsertSetting(SettingState().settings)
                            }
                        }
                    }
                    )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}