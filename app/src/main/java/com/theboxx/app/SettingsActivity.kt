package com.theboxx.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.theboxx.app.data.SettingEvent
import com.theboxx.app.data.system.packages.UserApps
import com.theboxx.app.ui.theme.TheBoxxTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class SettingsActivity() : ComponentActivity() {

    private val settingsViewModel by viewModels<SettingViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingViewModel(applicationContext) as T
                }
            }
        }
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TheBoxxTheme {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                Scaffold(
                    contentWindowInsets = WindowInsets.safeContent,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            title = { Text("Settings") },
                            navigationIcon = {
                                IconButton(
                                    onClick = { finish() },
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Go back"
                                    )
                                }
                            }
                        )
                    }
                ) { padding ->

                    val isLoading by settingsViewModel.installedAppListIsLoading.observeAsState(true)
                    val appState = settingsViewModel.appState.collectAsState().value

                    val pager = settingsViewModel.installedAppPager.observeAsState(emptyFlow<PagingData<UserApps>>()).value
                    LaunchedEffect(appState.apps.isEmpty()) {
                        if (appState.apps.isNotEmpty()) {
                            settingsViewModel.loadInstalledApps(applicationContext, appState)
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
                                                    settingsViewModel.onEvent(SettingEvent.SetPackageName(app.packageName))
                                                    settingsViewModel.onEvent(SettingEvent.SetPackageAllow(app.allowOperation))
                                                    settingsViewModel.onEvent(SettingEvent.SaveApp)
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
            }
        }
    }
}