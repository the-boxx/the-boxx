package com.theboxx.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TheBoxxTheme {
                Scaffold { padding ->

                    val isLoading by settingsViewModel.installedAppListIsLoading.observeAsState(true)

                    val pager = settingsViewModel.installedAppPager.observeAsState(emptyFlow<PagingData<UserApps>>()).value
                    LaunchedEffect(Unit) {
                        settingsViewModel.loadInstalledApps(applicationContext)
                    }

                    Log.d("SettingsActivity", "pager: ${pager}")

                    val pagedApps = pager.collectAsLazyPagingItems()
                    Log.d("SettingsActivity", "pagedApps: ${pagedApps}")

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .graphicsLayer {}
                                .fillMaxSize(),
                            contentPadding = PaddingValues(5.dp),


                        ) {
                            items(pagedApps.itemCount) { index ->
                                val app = pagedApps[index]
                                if (app != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(75.dp),
                                        verticalAlignment = Alignment.CenterVertically

                                    ) {
    //                                    Column {
    //                                        val icon: Drawable? = app.icon
    //                                        Image(
    //                                            icon,
    //                                            contentDescription = "Icon for ${app.appName}"
    //                                        )
    //                                    }
                                        Column {
                                            Text(
                                                text = app.appName,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                        Column {
                                            Checkbox(
                                                checked = app.allowOperation,
                                                onCheckedChange = {
                                                    settingsViewModel.onEvent(SettingEvent.SetPackageName(app.packageName))
                                                    settingsViewModel.onEvent(SettingEvent.SetPackageAllow(!app.allowOperation))
                                                    settingsViewModel.onEvent(SettingEvent.SaveApp)
                                                }
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