package com.theboxx.app

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theboxx.app.data.app.App
import com.theboxx.app.data.app.AppState
import com.theboxx.app.data.settings.Setting
import com.theboxx.app.data.db.SettingDatabase
import com.theboxx.app.data.settings.SettingEvent
import com.theboxx.app.data.settings.SettingState
import com.theboxx.app.data.system.packages.UserApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val _settingSettings: StateFlow<Setting> = settingDao.getSettings().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingState().settings)
    val settingState = combine(_settingState, _settingSettings) { state, settings ->
        state.copy(
            boxxState = settings.boxxState,
            settings = settings
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingState())
    private var trustTimerJob: Job? = null


//  PROFILES and APPS
    private val _appState = MutableStateFlow(AppState())
    private val _appApps = appDao.getApps().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val appState = combine(_appState, _appApps) { state, apps ->
        state.copy(
            apps = apps
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppState())

    private val _installedAppList = MutableLiveData<List<UserApps>>()

    private val _installedAppListIsLoading = MutableLiveData(true)
    val installedAppListIsLoading: LiveData<Boolean> = _installedAppListIsLoading

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
                        tagId = currentSettings?.tagId ?: "",
                        isOnboarded = currentSettings?.isOnboarded ?: false,
                        emergencyUnlocks = currentSettings?.emergencyUnlocks ?: 0,
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


    fun onEvent(event: SettingEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingEvent.CompleteOnboarding -> {
                    _settingState.update {
                        it.copy(
                            isOnboarded = event.onboarded
                        )
                    }
                }

                is SettingEvent.SetBoxxState -> {
                    if (_settingState.value.isTrusted) {
                        _settingState.update {
                            it.copy(
                                boxxState = event.boxxState
                            )
                        }
                    }
                }

                is SettingEvent.SetTagId -> {
                    _settingState.update {
                        it.copy(
                            tagId = event.tagId
                        )
                    }
                }


                is SettingEvent.SaveSetting -> {
                    if (!_settingState.value.isLoading) {
                        val boxxState = _settingState.value.boxxState
                        val tagId = _settingState.value.tagId
                        val isOnboarded = _settingState.value.isOnboarded
                        val emergencyUnlocks = _settingState.value.emergencyUnlocks

                        val setting = Setting(
                            boxxState = boxxState,
                            tagId = tagId,
                            isOnboarded = isOnboarded,
                            emergencyUnlocks = emergencyUnlocks,
                        )

                        settingDao.upsertSetting(setting)
                    }
                }

                is SettingEvent.EmergencyUnlock -> {
                    if (_settingState.value.isTrusted) {
                        val emergencyUnlocks = _settingState.value.emergencyUnlocks + 1
                        _settingState.update {
                            it.copy(
                                boxxState = false,
                                emergencyUnlocks = emergencyUnlocks
                            )
                        }
                    }
                }

                is SettingEvent.ResetAllSettings -> {
                    _settingState.update {
                        it.copy(
                            boxxState = false,
                            tagId = "",
                            isOnboarded = false,
                            emergencyUnlocks = 0,
                        )
                    }
                    _appState.update {
                        it.copy(
                            apps = emptyList(),

                            app = App("", true),
                            packageName = "",
                            allowOperation = true,

                            installedApps = emptyList(),
                            filteredInstalledApps = emptyList(),
                            appFilterString = ""
                        )
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        val appsToDelete = appDao.getBlockedApps()
                        for (app in appsToDelete) {
                            appDao.deleteApp(app)
                        }
                    }
                }

                is SettingEvent.SetIsTrusted -> {
                    _settingState.update {
                        it.copy(
                            isTrusted = event.isTrusted
                        )
                    }

                    trustTimerJob?.cancel()

                    if (event.isTrusted) {
                        trustTimerJob = viewModelScope.launch {
                            delay(60000L)
                            _settingState.update {
                                it.copy(
                                    isTrusted = false
                                )
                            }
                        }
                    }

                }

                is SettingEvent.GetInstalledApps -> {
                    _installedAppListIsLoading.value = true
                    _installedAppList.value = withContext(Dispatchers.IO) {
                        UserApps.getInstalledApps(event.context)
                    }
                    val installedApps = _installedAppList.value ?: emptyList()
                    val mappedInstalledApps = installedApps.map { app ->
                        app.copy(
                            allowOperation = appState.value.apps.find { it.packageName == app.packageName }?.allowOperation
                                ?: true
                        )
                    }

                    _appState.update {
                        it.copy (
                            installedApps = mappedInstalledApps,
                            filteredInstalledApps = it.filteredInstalledApps.ifEmpty { mappedInstalledApps }
                        )
                    }
//                    installedAppPager.value = Pager(
//                        PagingConfig(pageSize = 30)
//                    ) {
//                        UserAppsPagingSource(installedAppsCompiled)
//                    }.flow.cachedIn(viewModelScope)
//
                    _installedAppListIsLoading.value = false
                }

                is SettingEvent.UpdateAppInList -> {
                    val installedApps = _appState.value.installedApps.toMutableList()
                    val filteredInstalledApps = _appState.value.filteredInstalledApps.toMutableList()
                    val installedIndex = if (event.filtered) installedApps.indexOf(filteredInstalledApps[event.index])
                        else event.index
                    val filteredInstalledIndex = if (event.filtered) event.index
                        else filteredInstalledApps.indexOf(installedApps[event.index])

                    installedApps[installedIndex] = installedApps[installedIndex].copy(
                        allowOperation = event.allowOperation
                    )
                    val updatedInstalledApps = installedApps.toList()
                    if (filteredInstalledIndex != -1) {
                        filteredInstalledApps[filteredInstalledIndex] =
                            filteredInstalledApps[filteredInstalledIndex].copy(
                                allowOperation = event.allowOperation
                            )
                    }
                    val updatedFilteredInstalledApps = filteredInstalledApps.toList()

                    _appState.update {
                        it.copy(
                            installedApps = updatedInstalledApps,
                            filteredInstalledApps = updatedFilteredInstalledApps
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

                is SettingEvent.SetAppFilterString -> {
                    _appState.update {
                        it.copy(
                            appFilterString = event.appFilterString
                        )
                    }
                }
                
                is SettingEvent.FilterInstalledApps -> {
                    val appFilterString = _appState.value.appFilterString.lowercase()
                    val filteredApps = _appState.value.installedApps.filter {
                        it.appName.lowercase().contains(appFilterString)
//                                || it.packageName.lowercase().contains(appFilterString)
                    }
                    _appState.update {
                        it.copy(
                            filteredInstalledApps = filteredApps
                        )
                    }
                }

                is SettingEvent.SaveApp -> {
                    if (!_appState.value.isLoading) {
                        val packageName = _appState.value.packageName
                        val allowOperation = _appState.value.allowOperation

                        val app = App(packageName = packageName, allowOperation = allowOperation)
                        if (allowOperation) {
                            val appToDelete = App(app.packageName, !app.allowOperation)
                            appDao.deleteApp(appToDelete) // Delete unused ones
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

    override fun onCleared() {
        super.onCleared()
        trustTimerJob?.cancel()
    }
}