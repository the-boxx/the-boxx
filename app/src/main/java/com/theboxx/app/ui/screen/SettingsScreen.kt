package com.theboxx.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.theboxx.app.SettingViewModel
import com.theboxx.app.data.settings.SettingEvent
import com.theboxx.app.data.system.packages.UserApps
import com.theboxx.app.ui.navigation.NavigationScreens
import com.theboxx.app.ui.navigation.NavigationViewModel


private val textModifier = Modifier
    .padding(12.dp)

private fun Modifier.screenColumnModifier(padding: PaddingValues, scrollState: ScrollState): Modifier {
    return this.then(Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(padding)
        .padding(14.dp))
}

data class SettingsScreenMainItem(
    val title: String,
    val enabled: Boolean = true,
    val description: String,
    val icon: ImageVector,
    val screen: Any
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenMain(navController: NavController, settingViewModel: SettingViewModel) {
    val settingState = settingViewModel.settingState.collectAsState()
    val boxxState = settingState.value.boxxState

    val settingsScreenMainItems = listOf(
        SettingsScreenMainItem(
            title = "Apps",
            enabled = !boxxState,
            description = "Select which apps to block",
            icon = Icons.Filled.Menu,
            screen = NavigationScreens.Settings.Apps
        ),
        SettingsScreenMainItem(
            title = "Emergency Unlock",
            enabled = boxxState,
            description = "Unlock the Boxx without key",
            icon = Icons.Filled.Lock,
            screen = NavigationScreens.Settings.EmergencyUnlock
        ),
        SettingsScreenMainItem(
            title = "Set NFC Tag",
            enabled = !boxxState,
            description = "Set which tag will be used to trigger Boxx changes",
            icon = Icons.Filled.AddCircle,
            screen = NavigationScreens.Settings.NfcTag
        ),
        SettingsScreenMainItem(
            title = "Restart Onboarding",
            enabled = !boxxState,
            description = "Follow the setup tutorial again",
            icon = Icons.Filled.Refresh,
            screen = NavigationScreens.Settings.RestartOnboarding
        )
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            for (item in settingsScreenMainItems) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(horizontal = 15.dp)
                        .clickable(
                            enabled = item.enabled,
                            onClick = {
                                navController.navigate(item.screen)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(9f)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (item.enabled) 1f else 0.5f)
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (item.enabled) 0.9f else 0.45f)
                        )
                    }
                }
            }
        }
    }
}

fun onSwitchAllowOperation(settingViewModel: SettingViewModel, index: Int, app: UserApps, checked: Boolean, filtered: Boolean) {
    settingViewModel.onEvent(SettingEvent.UpdateAppInList(
        filtered, index, checked
    ))
    settingViewModel.onEvent(SettingEvent.SetPackageName(app.packageName))
    settingViewModel.onEvent(
        SettingEvent.SetPackageAllow(
            checked
        )
    )
    settingViewModel.onEvent(SettingEvent.SaveApp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSettingsAppBar(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel) {
    val navigationState = navigationViewModel.navigationState.collectAsState().value
    val currentScreen = navigationState.currentScreen
    val isAppSearchEnabled = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = currentScreen.title
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    isAppSearchEnabled.value = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        }
    )
    AnimatedContent(isAppSearchEnabled.value) { enabled ->
        if (enabled) {
            Box(
                modifier = Modifier
                    .background(SearchBarDefaults.colors().containerColor)
                    .fillMaxSize()
                    .zIndex(100f),
                contentAlignment = Alignment.TopCenter
            ) {
                TopAppSearchAppBar(
                    settingViewModel = settingViewModel,
                    onBack = { isAppSearchEnabled.value = false },
                    searchBarExpanded = isAppSearchEnabled
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppSearchAppBar(settingViewModel: SettingViewModel, onBack: () -> Unit, searchBarExpanded: MutableState<Boolean>) {
    val appState = settingViewModel.appState.collectAsState().value
    val appFilterString = appState.appFilterString

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = appFilterString,
                onQueryChange = {
                    settingViewModel.onEvent(SettingEvent.SetAppFilterString(it))
                },
                onSearch = {
                    settingViewModel.onEvent(SettingEvent.FilterInstalledApps)
                },
                placeholder = {
                    Text("Search Apps")
                },
                leadingIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                expanded = searchBarExpanded.value,
                onExpandedChange = { searchBarExpanded.value = it }
            )
        },
        expanded = searchBarExpanded.value,
        onExpandedChange = { searchBarExpanded.value = it },
        windowInsets = WindowInsets.safeContent
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(count = appState.filteredInstalledApps.size) { index ->
                val app = appState.filteredInstalledApps[index]
                val allowOperation = app.allowOperation
                ListItem(
                    leadingContent = {
                        AsyncImage(
                            model = app.icon,
                            contentDescription = "Icon for ${app.appName}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(50.dp)
                        )
                    },
                    headlineContent = {
                        Text(
                            text = app.appName,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    supportingContent = {
                        Text(
                            text = app.packageName,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    trailingContent = {
                        Checkbox(
                            checked = allowOperation,
                            onCheckedChange = { checked ->
                                onSwitchAllowOperation(settingViewModel, index, app, checked, true)
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = true,
                            onClick = {
                                onSwitchAllowOperation(settingViewModel, index, app, !allowOperation, true)
                            }
                        )
                        .padding(16.dp, 4.dp)
                        .height(80.dp)
                )
            }
            if (appState.filteredInstalledApps.isEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .height(50.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("No apps found")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenApps(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel, isOnboarding: Boolean = false) {

    val context = LocalContext.current
    val isLoading by settingViewModel.installedAppListIsLoading.observeAsState(true)
    val appState = settingViewModel.appState.collectAsState().value

    LaunchedEffect(appState.apps.isEmpty()) {
        if (appState.apps.isNotEmpty()) {
            settingViewModel.onEvent(SettingEvent.GetInstalledApps(context))
        }
    }

    Scaffold(
        topBar = { TopSettingsAppBar(navController, navigationViewModel, settingViewModel) }
    ) { padding ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            LazyColumn(
                contentPadding = padding
            ) {
                if (isOnboarding) {
                    item {
                        Card(
                            modifier = Modifier
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "Apps that are deselected here will be blocked when the device is Boxxed.",
                                modifier = Modifier
                                    .padding(24.dp)
                            )
                            Button(
                                onClick = {
                                    navController.navigate(NavigationScreens.Onboarding.EmergencyUnlock)
                                },
                                modifier = Modifier
                                    .padding(24.dp)
                            ) {
                                Text("Next")
                            }
                        }
                    }
                }
                items(count = appState.installedApps.size) { index ->
                    val app = appState.installedApps[index]
                    val allowOperation = app.allowOperation
                    ListItem(
                        leadingContent = {
                            AsyncImage(
                                model = app.icon,
                                contentDescription = "Icon for ${app.appName}",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(50.dp)
                            )
                        },
                        headlineContent = {
                            Text(
                                text = app.appName,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        },
                        supportingContent = {
                            Text(
                                text = app.packageName,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        },
                        trailingContent = {
                            Checkbox(
                                checked = allowOperation,
                                onCheckedChange = { checked ->
                                    onSwitchAllowOperation(settingViewModel, index, app, checked, false)
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = true,
                                onClick = {
                                    onSwitchAllowOperation(settingViewModel, index, app, !allowOperation, false)
                                }
                            )
                            .padding(16.dp, 4.dp)
                            .height(80.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenEmergencyUnlock(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel) {
    val emergencyUnlocks = settingViewModel.settingState.collectAsState().value.emergencyUnlocks
    val emergencyUnlocksNotExceeded = emergencyUnlocks < 5
    val unboxxText = if (emergencyUnlocksNotExceeded) {
        "Un-Boxx your device when you forgot/lost your key. You have used $emergencyUnlocks out of 5 emergency unlocks."
    } else {
        "Un-Boxx your device when you forgot/lost your key. You have used all your emergency unlocks " +
        "($emergencyUnlocks/5). You must now reset The Boxx to continue using it. You will lose all " +
        "the data you have in The Boxx app. Are you sure you want to reset?"
    }
    Scaffold(
        topBar = { TopSettingsAppBar(navController, navigationViewModel, settingViewModel) }
    ) { padding ->
        val openDialog = remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Emergency Unlock",
                modifier = textModifier,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
            Text(
                text = unboxxText,
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = {
                    openDialog.value = true
                }
            ) {
                Text(
                    text = if (emergencyUnlocksNotExceeded) {"Do it!"} else {"Reset The Boxx"}
                )
            }
            if (openDialog.value) {
                BasicAlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    properties = DialogProperties()
                ) {
                    Card {
                        Text(
                            text = if (emergencyUnlocksNotExceeded) {"Are you sure?"} else
                                {"Are you sure you'd like to reset?"},
                            modifier = Modifier
                                .padding(12.dp)
                        )
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    openDialog.value = false
                                },
                                modifier = Modifier
                                    .padding(12.dp)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    openDialog.value = false
                                    navController.navigate(NavigationScreens.Status) {
                                        popUpTo(NavigationScreens.Settings.EmergencyUnlock) {
                                            inclusive = true
                                        }
                                    }
                                    if (emergencyUnlocksNotExceeded) {
//                                        Unlocks, otherwise resets.
                                        settingViewModel.onEvent(SettingEvent.SetIsTrusted(true))
                                        settingViewModel.onEvent(SettingEvent.EmergencyUnlock)
                                        settingViewModel.onEvent(SettingEvent.SaveSetting)
                                        settingViewModel.onEvent(SettingEvent.SetIsTrusted(false))
                                    } else {
                                        settingViewModel.onEvent(SettingEvent.ResetAllSettings)
                                        settingViewModel.onEvent(SettingEvent.SaveSetting)
                                    }
                                },
                                modifier = Modifier
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Yes"
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun SettingsScreenNfcTag(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel) {

    val settingState = settingViewModel.settingState.collectAsState().value
    val isTagIdSet = settingState.tagId?.isNotEmpty() ?: false

    val textTransparency = if (isTagIdSet) 0.6f else 0.9f
    Scaffold(
        topBar = { TopSettingsAppBar(navController, navigationViewModel, settingViewModel)}
    ) { padding ->
        Column(
            modifier = Modifier.screenColumnModifier(padding, rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NFC Tag",
                modifier = textModifier,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
            Text(
                text = "Scan the tag you want use to Boxx/Un-boxx your device.",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = textTransparency)
            )

            if (isTagIdSet) {
                Card(
                    modifier = textModifier
                ) {
                    Text(
                        text = "You've already set an NFC Tag. Scan again to set it again.",
                        modifier = textModifier,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tag ID: ${settingState.tagId}",
                        modifier = textModifier,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreenRestartOnboarding(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel) {
    Scaffold(
        topBar = { TopSettingsAppBar(navController, navigationViewModel, settingViewModel)}
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Would you like to restart onboarding?")
            Button(
                onClick = {
                    settingViewModel.onEvent(SettingEvent.CompleteOnboarding(false))
                    settingViewModel.onEvent(SettingEvent.SaveSetting)
                    navController.navigate(NavigationScreens.Onboarding) {
                        popUpTo(NavigationScreens.Settings.RestartOnboarding) { inclusive = true }
                    }
                }
            ) {
                Text("Yep!")
            }
        }
    }
}