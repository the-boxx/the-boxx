package com.theboxx.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.theboxx.app.SettingViewModel
import com.theboxx.app.data.settings.SettingEvent
import com.theboxx.app.ui.navigation.NavigationScreens
import com.theboxx.app.ui.navigation.NavigationViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch


data class SettingsScreenMainItem(
    val title: String,
    val enabled: Boolean = true,
    val description: String,
    val icon: ImageVector,
    val screen: Any
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenMain(padding: PaddingValues, navController: NavController, viewModel: SettingViewModel) {
    val settingState = viewModel.settingState.collectAsState()
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

    Column(
        modifier = Modifier
            .padding(padding)
            .background(MaterialTheme.colorScheme.background)
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
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = if(item.enabled) 1f else 0.5f)
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = if(item.enabled) 0.9f else 0.45f)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreenApps(padding: PaddingValues, navController: NavController, settingViewModel: SettingViewModel, navigationViewModel: NavigationViewModel) {

    val context = LocalContext.current
    val isLoading by settingViewModel.installedAppListIsLoading.observeAsState(true)
    val appState = settingViewModel.appState.collectAsState().value
    val navigationState = navigationViewModel.navigationState.collectAsState().value

    val pager = settingViewModel.installedAppPager.observeAsState(emptyFlow()).value
    LaunchedEffect(appState.apps.isEmpty()) {
        if (appState.apps.isNotEmpty()) {
            settingViewModel.loadInstalledApps(context, appState)
        }
    }
    val pagedApps = pager.collectAsLazyPagingItems()

    val isOnboarding = navigationState.currentScreen == NavigationScreens.Onboarding.Apps

    val lazyListState = rememberLazyListState()

    if (isLoading) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        Box {
            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
                state = lazyListState,
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isOnboarding) {
                    item {
                        Card {
                            Text(
                                text = "Apps that are deselected here will be blocked when the device is Boxxed.",
                                modifier = Modifier
                                    .padding(12.dp)
                            )
                            Button(
                                onClick = {
                                    navController.navigate(NavigationScreens.Onboarding.EmergencyUnlock)
                                },
                                modifier = Modifier
                                    .padding(12.dp)
                            ) {
                                Text("Next")
                            }
                        }
                    }
                }
                items(pagedApps.itemCount) { index ->
                    val app = pagedApps[index]
                    if (app != null) {
                        val allowOperation = remember { mutableStateOf(app.allowOperation) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(2f)
                            ) {
                                AsyncImage(
                                    model = app.icon,
                                    contentDescription = "Icon for ${app.appName}",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(10.dp, 0.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(9f),
                            ) {
                                Text(
                                    text = app.appName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(vertical = 1.dp)
                                )
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(vertical = 1.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1.5f)
                            ) {
                                Checkbox(
                                    checked = allowOperation.value,
                                    onCheckedChange = {
                                        allowOperation.value = !allowOperation.value
                                        settingViewModel.onEvent(SettingEvent.SetPackageName(app.packageName))
                                        settingViewModel.onEvent(
                                            SettingEvent.SetPackageAllow(
                                                allowOperation.value
                                            )
                                        )
                                        settingViewModel.onEvent(SettingEvent.SaveApp)
                                    },
                                    modifier = Modifier
                                        .width(36.dp)
                                )
                            }
                        }
                    }
                }

                pagedApps.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { CircularProgressIndicator() }
                        }

                        loadState.append is LoadState.Loading -> {
                            item { CircularProgressIndicator() }
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    settingViewModel.viewModelScope.launch {
                        lazyListState.scrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(WindowInsets.safeContent.asPaddingValues())
                    .padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Go to top"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenEmergencyUnlock(padding: PaddingValues, navController: NavController, viewModel: SettingViewModel) {

    val openDialog = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Emergency Unlock")
        Button(
            onClick = {
                openDialog.value = true
            }
        ) {
            Text("Do it!")
        }
        if (openDialog.value) {
            BasicAlertDialog (
                onDismissRequest = {
                    openDialog.value = false
                },
                properties = DialogProperties()
            ) {
                Card {
                    Text(
                        text = "Are you sure?",
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
                                viewModel.onEvent(SettingEvent.SetIsTrusted(true))
                                viewModel.onEvent(SettingEvent.SetBoxxState(false))
                                viewModel.onEvent(SettingEvent.SaveSetting)
                                viewModel.onEvent(SettingEvent.SetIsTrusted(false))
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

@Composable
fun SettingsScreenNfcTag(padding: PaddingValues, viewModel: SettingViewModel) {
    val settingsState = viewModel.settingState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Scan your NFC Tag",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun SettingsScreenRestartOnboarding(padding: PaddingValues, navController: NavController, viewModel: SettingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Would you like te restart onboarding?")
        Button(
            onClick = {
                viewModel.onEvent(SettingEvent.CompleteOnboarding(false))
                viewModel.onEvent(SettingEvent.SaveSetting)
                navController.navigate(NavigationScreens.Onboarding) {
                    popUpTo(NavigationScreens.Settings.RestartOnboarding) { inclusive = true }
                }
            }
        ) {
            Text("Yep!")
        }
    }
}