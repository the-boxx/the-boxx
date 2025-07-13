//package com.theboxx.app
//
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.viewModels
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//
//class SettingsActivity() : ComponentActivity() {
//    private val settingDb by lazy {
//        SettingDatabase.getDatabase(applicationContext)
//    }
//
//    private val settingsViewModel by viewModels<SettingViewModel>(
//        factoryProducer = {
//            object : ViewModelProvider.Factory {
//                override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                    return SettingViewModel(applicationContext) as T
//                }
//            }
//        }
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        enableEdgeToEdge()
//        setContent {
//            val settingsState by settingsViewModel.settingState.collectAsState()
//            val appState by settingsViewModel.appState.collectAsState()
//
//            val installedApps: List<AppDetail> = AppDetail.getInstalledApps(applicationContext)
//
//
//            if (appState.isLoading) {
//                Box(
//                    modifier = Modifier
//                        .background(Color.Black)
//                        .fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(
//                        color = Color.White
//                    )
//                }
//            } else {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.Black)
//                ) {
//                    items(installedApps) { appDetail ->
//                        AppListItem(appDetail, settingsViewModel, settingsState.currentProfile)
//
//                    }
//                }
//            }
//
//        }
//
//
//    }
//
//}
//
//@Composable
//fun AppListItem(
//    appDetail: AppDetail,
//    settingsViewModel: SettingViewModel,
//    currentProfile: Int
//) {
//
//    var appFromDb by remember { mutableStateOf<App?>(null) }
//    var isLoadingApp by remember { mutableStateOf(true) }
//
//    LaunchedEffect(key1 = appDetail.packageName) {
//        isLoadingApp = true
//        Log.d("asand", "Getting app: ${appDetail.packageName}")
//        appFromDb = settingsViewModel.getApp(appDetail.packageName)
//        Log.d("asand", "App from db: $appFromDb")
//        isLoadingApp = false
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        Column {
//            if (isLoadingApp) {
//                CircularProgressIndicator()
//            } else {
//                appFromDb.let { currentAppEntity ->
//
//                    val appEntity = currentAppEntity ?: App(appDetail.packageName, mutableListOf(AppProfile(currentProfile, true)))
//                    val appProfile =
//                        appEntity.profiles.find { it.profile == currentProfile } ?: AppProfile(
//                            currentProfile,
//                            true
//                        )
//
//                    var checkedState by remember(appFromDb, currentProfile) {
//                        mutableStateOf(appProfile.allowOperation)
//                    }
////                    val checkedState = appProfile.allowOperation
//////            settingsViewModel.getApp(appDetail.packageName)
//////            val currentAppProfile =
//////                appState.app.profiles.elementAtOrNull(currentProfile)
//////                    ?: AppProfile(currentProfile, true)
//////            val checkedState = currentAppProfile.allowOperation
//                    Checkbox(
//                        checked = checkedState,
//                        //                                    app.profiles.elementAt(settingsState.currentProfile).allowOperation,
//                        onCheckedChange = { checked ->
//                            // Update status immediately
//                            checkedState = checked
//
//                            Log.d("asand", "Checkbox changed to $checked")
//
//
//                            var appToSave = appFromDb ?: App(appDetail.packageName, mutableListOf(AppProfile(currentProfile, checked)))
//                            val existingProfile = appToSave.profiles.find { it.profile == currentProfile }
//                            if (existingProfile != null) {
//                                val updatedProfiles = appToSave.profiles.map {
//                                    if (it.profile == currentProfile) {
//                                        it.copy(allowOperation = checkedState)
//                                    } else {
//                                        it
//                                    }
//                                }
//                                appToSave.profiles = updatedProfiles.toMutableList()
//                            } else {
//                                appToSave.profiles.add(AppProfile(currentProfile, checkedState))
//                            }
//
//
//                            settingsViewModel.onEvent(SettingEvent.SetPackageName(appDetail.packageName))
//                            settingsViewModel.onEvent(
//                                SettingEvent.SetAppProfile(
//                                    AppProfile(
//                                        currentProfile,
//                                        checked
//                                    )
//                                )
//                            )
//                            settingsViewModel.onEvent(SettingEvent.SaveApp)
//                        }
//                    )
////                } :? run {
////                    Text("App not in DB")
//                }
//            }
//        }
//        Column {
//            Text(
//                text = appDetail.appName,
//                color = Color.White
//            )
//            Text(
//                text = appDetail.packageName,
//                color = Color.White
//            )
//        }
//    }
//}