package com.theboxx.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theboxx.app.data.App
//import com.theboxx.app.data.AppProfile
import com.theboxx.app.data.AppState
//import com.theboxx.app.data.AppWithProfiles
import com.theboxx.app.data.Setting
import com.theboxx.app.data.SettingDatabase
import com.theboxx.app.data.SettingEvent
import com.theboxx.app.data.SettingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val applicationContext: Context
    ): ViewModel() {

    private val settingDb by lazy {
        SettingDatabase.getDatabase(applicationContext)
    }
    private val settingDao = settingDb.settingDao
    private val appDao = settingDb.appDao
//    private val currentState = dao.getBoxxState()
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingState())


//    private val _state = MutableStateFlow(SettingState())
//    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Setting(currentState, currentProfile))
//    private val _settings = MutableStateFlow(SettingState())
//    val state = combine(_state, _settings) { state, settings ->
//        state.copy(
//            settings = settings
//        )
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingState())

//    private val _state = MutableStateFlow(SettingState())
//    private val _settings = MutableStateFlow(SettingState().settings)
//    val state = combine(_state, _settings) { state, settings ->
//        state.copy(
//            settings = settings
//        )
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingState())
////    private val state = dao.getBoxxState()
////    private val stateBetter =

//  SETTINGS
    private val _settingState = MutableStateFlow(SettingState(SettingState().settings))
    private val _settingSettings: StateFlow<Setting> = settingDao.getSettings().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Setting(false, 0))
    val settingState = combine(_settingState, _settingSettings) { state, settings ->
        state.copy(
            boxxState = settings.boxxState,
            currentProfile = settings.currentProfile,
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
                        currentProfile = currentSettings?.currentProfile ?: SettingState().settings.currentProfile,
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

                is SettingEvent.SetCurrentProfile -> {
                    _settingState.update {
                        it.copy(
                            currentProfile = event.currentProfile
                        )
                    }
                }

                is SettingEvent.SaveSetting -> {
                    if (!_settingState.value.isLoading) {
                        val boxxState = _settingState.value.boxxState
                        val currentProfile = _settingState.value.currentProfile

                        val setting = Setting(
                            boxxState = boxxState,
                            currentProfile = currentProfile
                        )

                        Log.d("asand", "Saving setting: $setting")

                        settingDao.upsertSetting(setting)
                    }
                }

                is SettingEvent.SetIsTrusted -> TODO()

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
//                        val profiles = _appState.value.app.profiles

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

//                is SettingEvent.SetAppProfile -> {
//                    _appState.update {
//                        val profile = event.profile
//                        val allProfiles = _appState.value.app.profiles.toMutableList()
//
//
//                        it.copy(
//                            profiles = allProfiles
//                        )
//                    }
//                }
            }
        }
    }
}