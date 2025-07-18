package com.theboxx.app

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.theboxx.app.data.App
import com.theboxx.app.data.system.packages.UserApps
//import com.theboxx.app.data.AppProfile
import com.theboxx.app.data.AppState
//import com.theboxx.app.data.AppWithProfiles
import com.theboxx.app.data.Setting
import com.theboxx.app.data.SettingDatabase
import com.theboxx.app.data.SettingEvent
import com.theboxx.app.data.SettingState
import com.theboxx.app.data.system.packages.UserAppsPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel(
    private val applicationContext: Context
    ): ViewModel() {

    private val settingDb by lazy {
        SettingDatabase.getDatabase(applicationContext)
    }
    private val settingDao = settingDb.settingDao
    private val appDao = settingDb.appDao

//  SETTINGS
    private val _settingState = MutableStateFlow(SettingState(SettingState().settings))
    private val _settingSettings: StateFlow<Setting> = settingDao.getSettings().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Setting(false, 0))
    val settingState = combine(_settingState, _settingSettings) { state, settings ->
        state.copy(
            boxxState = settings.boxxState,
            settings = settings
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingState())

//  PROFILES and APPS
    private val _appState = MutableStateFlow(AppState())
    private val _appApps = appDao.getApps().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val appState = combine(_appState, _appApps) { state, apps ->
        state.copy(
            apps = apps
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppState())

    private val _installedAppList = MutableLiveData<List<UserApps>>()
//    val installedAppList: List<UserApps>

    private val _installedAppListIsLoading = MutableLiveData(true)
    val installedAppListIsLoading: LiveData<Boolean> = _installedAppListIsLoading

    val installedAppPager = MutableLiveData<Flow<PagingData<UserApps>>>()

    fun loadInstalledApps(context: Context) {
        viewModelScope.launch {
            _installedAppListIsLoading.value = true
            _installedAppList.value = withContext(Dispatchers.IO) {
                UserApps.getInstalledApps(context)
            }
            Log.d("viewmodel", "getinstall apps ${_installedAppList.value}")
            val installedApps = _installedAppList.value ?: emptyList()
            installedAppPager.value = Pager(
                PagingConfig(pageSize = 30)
            ) {
                UserAppsPagingSource(context, installedApps)
            }.flow.cachedIn(viewModelScope)

            Log.d("viewmodel", "Installed Apps: ${installedAppPager.value}")
            _installedAppListIsLoading.value = false
        }
    }

    fun getInstalledAppsPaged(context: Context): Flow<PagingData<UserApps>> {

        viewModelScope.launch {_installedAppListIsLoading.value = true}
        Log.d("viewmodel", "Loading Apps")
        loadInstalledApps(context)
        val installedApps = _installedAppList.value ?: emptyList()

        Log.d("viewmodel", "Installed Apps: ${installedApps}")
        Log.d("viewmodel", "Loading Pager")
        val pager = Pager(
            PagingConfig(pageSize = 20)
        ) {
            UserAppsPagingSource(context, installedApps)
        }.flow.cachedIn(viewModelScope)
        viewModelScope.launch {_installedAppListIsLoading.value = false}
        return pager
    }

    init {
        loadSettingsFromDatabase()
    }

    private fun loadSettingsFromDatabase() {
        viewModelScope.launch {
            try {
                val currentSettings = settingDao.getSettings().firstOrNull()
                val currentApps = appDao.getApps().firstOrNull()

                _settingState.update {
                    it.copy(
                        isLoading = false,
                        boxxState = currentSettings?.boxxState ?: SettingState().settings.boxxState,
                        settings = currentSettings ?: SettingState().settings
                    )
                }

                _appState.update {
                    it.copy(
                        isLoading = false,
                        apps = currentApps ?: emptyList(),
                        app = App("")
                    )
                }

            } catch (e: Exception) {
                _settingState.update {
                    it.copy(
                        isLoading = false
                    )
                }
                _appState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    suspend fun getApp(packageName: String): App {
        Log.d("asand", "Getting app: $packageName")
//        viewModelScope.launch {
//            _appState.update {
//                it.copy(
//                    app = appDao.getApp(packageName) ?: App("", emptyList())
//                )
//            }
//        }
        return appDao.getApp(packageName) ?: App(packageName)
    }


    fun onEvent(event: SettingEvent) {
//        Log.d("asand", "Event: $event")
        viewModelScope.launch {
            when (event) {
                is SettingEvent.SetBoxxState -> {
                    _settingState.update {
                        it.copy(
                            boxxState = event.boxxState

                        )
                    }

                }

                is SettingEvent.SaveSetting -> {
                    if (!_settingState.value.isLoading) {
                        if (_settingState.value.isTrusted) {

                            val boxxState = _settingState.value.boxxState

                            val setting = Setting(
                                boxxState = boxxState
                            )

                            Log.d("asand", "Saving setting: $setting")

                            settingDao.upsertSetting(setting)
                        } else {
                            Toast.makeText(applicationContext, "Please unlock the boxx first.", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                is SettingEvent.SetIsTrusted -> {
                    _settingState.update {
                        it.copy(
                            isTrusted = event.isTrusted
                        )
                    }
                }

                is SettingEvent.GetApp -> {
                    _appState.update {
                        it.copy(
                            app = appDao.getApp(event.packageName) ?: App(event.packageName)
                        )
                    }
                }

                is SettingEvent.SaveApp -> {
                    if (!_appState.value.isLoading) {
                        val packageName = _appState.value.packageName
                        val allowOperation = _appState.value.allowOperation

                        val app = App(packageName = packageName, allowOperation = allowOperation)
                        Log.d("asand", "Saving app: $app")
                        if (allowOperation) {
                            appDao.deleteApp(app) // Delete unused ones
                        } else {
                            appDao.upsertApp(app)
                        }

                        // RESET to defaults after operation
                        _appState.update {
                            it.copy(
                                packageName = "",
                                allowOperation = true
                            )
                        }
                    }
                }

                is SettingEvent.SetPackageName -> {
                    _appState.update {
                        it.copy(
                            packageName = event.packageName
                        )
                    }
                }

                is SettingEvent.SetPackageAllow -> {
                    _appState.update {
                        it.copy(
                            allowOperation = event.allowOperation
                        )
                    }
                }

            }
        }
    }
}