package com.theboxx.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.theboxx.app.SettingViewModel
import com.theboxx.app.data.SettingEvent
import com.theboxx.app.data.system.packages.UserApps
import kotlinx.coroutines.flow.emptyFlow


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
    val appsEnabled by remember { if (settingState.value.boxxState) mutableStateOf(false) else mutableStateOf(true)}

    val settingsScreenMainItems = listOf(
        SettingsScreenMainItem(
            title = "Apps",
            enabled = appsEnabled,
            description = "Select which apps to block",
            icon = Icons.Filled.Menu,
            screen = Screens.SettingsScreen.Apps
        ),
        SettingsScreenMainItem(
            title = "Emergency Unlock",
            description = "Unlock the Boxx without key",
            icon = Icons.Filled.Lock,
            screen = Screens.SettingsScreen.EmergencyUnlock
        )
    )

    Column(
        modifier = Modifier
            .padding(padding)
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
fun SettingsScreenApps(padding: PaddingValues, viewModel: SettingViewModel) {

    val context = LocalContext.current
    val isLoading by viewModel.installedAppListIsLoading.observeAsState(true)
    val appState = viewModel.appState.collectAsState().value

    val pager = viewModel.installedAppPager.observeAsState(emptyFlow<PagingData<UserApps>>()).value
    LaunchedEffect(appState.apps.isEmpty()) {
        if (appState.apps.isNotEmpty()) {
            viewModel.loadInstalledApps(context, appState)
        }
    }
    val pagedApps = pager.collectAsLazyPagingItems()

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pagedApps.itemCount) { index ->
                val app = pagedApps[index]
                if (app != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(24.dp, 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        //                                    Column {
                        //                                        val icon: Drawable? = app.icon
                        //                                        Image(
                        //                                            icon,
                        //                                            contentDescription = "Icon for ${app.appName}"
                        //                                        )
                        //                                    }
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
                        Column (
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Checkbox(
                                checked = app.allowOperation,
                                onCheckedChange = {
                                    app.allowOperation = !app.allowOperation
                                    viewModel.onEvent(SettingEvent.SetPackageName(app.packageName))
                                    viewModel.onEvent(SettingEvent.SetPackageAllow(app.allowOperation))
                                    viewModel.onEvent(SettingEvent.SaveApp)
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
    }
}

@Composable
fun SettingsScreenEmergencyUnlock(padding: PaddingValues, viewModel: SettingViewModel) {
    Column(
        modifier = Modifier
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Emergency Unlock")
        Button(
            onClick = {
                viewModel.onEvent(SettingEvent.SetBoxxState(false))
                viewModel.onEvent(SettingEvent.SaveSetting)
            }
        ) {
            Text("Do it!")
        }
    }
}
