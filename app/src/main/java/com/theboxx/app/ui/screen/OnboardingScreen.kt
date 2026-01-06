package com.theboxx.app.ui.screen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.theboxx.app.R
import com.theboxx.app.SettingViewModel
import com.theboxx.app.data.settings.SettingEvent
import com.theboxx.app.data.system.accessibility.isAccessibilityServiceEnabled
import com.theboxx.app.ui.navigation.NavigationScreens
import com.theboxx.app.ui.navigation.NavigationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val textModifier = Modifier
    .padding(12.dp)
private fun Modifier.screenColumnModifier(padding: PaddingValues, scrollState: ScrollState): Modifier {
    return this.then(Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(padding)
        .padding(14.dp))
}
@Composable
fun OnboardingScreenMain(navController: NavController) {
    Scaffold { padding ->
        Column(
            modifier = Modifier.screenColumnModifier(padding, rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.boxx_single),
                contentDescription = "The Boxx Icon",
                modifier = Modifier
                    .padding(24.dp)
                    .size(150.dp)
            )
            Text(
                text = "Welcome to the Boxx",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                modifier = textModifier
            )
            Text(
                text = "With this app, we hope you will be able to build more self-control, " +
                        "and grow to control your device instead of letting it control you.",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
            )
            Text(
                text = "Let's get the app set up!",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
            )
            Button(
                onClick = {
                    navController.navigate(NavigationScreens.Onboarding.Accessibility)
                },
                modifier = textModifier
            ) {
                Text("Setup")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopOnboardingAppBar(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel) {
    val navigationState = navigationViewModel.navigationState.collectAsState().value
    val currentScreen = navigationState.currentScreen

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
            val dropdownMenuEnabled = remember { mutableStateOf(false) }
            Box {
                IconButton(
                    onClick = {
                        dropdownMenuEnabled.value = !dropdownMenuEnabled.value
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Other options"
                    )
                }
                DropdownMenu(
                    expanded = dropdownMenuEnabled.value,
                    onDismissRequest = {
                        dropdownMenuEnabled.value = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Skip tutorial")
                        },
                        onClick = {
                            settingViewModel.onEvent(SettingEvent.CompleteOnboarding())
                            settingViewModel.onEvent(SettingEvent.SaveSetting)
                            navController.navigate(NavigationScreens.Status)
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun OnboardingScreenAccessibility(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel, context: Context) {
    val currentContext = LocalContext.current
    val hasAccessibilityPermission = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        settingViewModel.viewModelScope.launch {
            while (!hasAccessibilityPermission.value) {
                val permission = isAccessibilityServiceEnabled(context)
                hasAccessibilityPermission.value = permission
                delay(1000L)
            }
        }
    }

    val textTransparency = if (hasAccessibilityPermission.value) 0.6f else 0.9f

    Scaffold(
        topBar = { TopOnboardingAppBar(navController, navigationViewModel, settingViewModel)}
    ) { padding ->
        Column(
            modifier = Modifier.screenColumnModifier(padding, rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Accessibility Permission",
                modifier = textModifier,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
            Text(
                text = "This permission is required for the Boxx to block you from accessing apps " +
                        "you've selected. Rest assured, that's the only thing it'll do.",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = textTransparency)
            )
            Text(
                text = "Still not sure? Check out the source code for the app. Yes, " +
                        "it's open source :)",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = textTransparency)
            )
            Text(
                text = buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Url(
                            "https://github.com/the-boxx/the-boxx"
                        )
                    ) {
                        append("Source Code")
                    }
                },
                modifier = textModifier,
                color = MaterialTheme.colorScheme.primary.copy(alpha = textTransparency)
            )
            if (hasAccessibilityPermission.value) {
                Card(
                    modifier = textModifier
                ) {
                    Text(
                        text = "Great, let's move on to the tutorial!",
                        modifier = textModifier,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Button(
                    onClick = {
                        currentContext.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    },
                    modifier = textModifier
                ) {
                    Text("Give permission")
                }
            }
            Button(
                enabled = hasAccessibilityPermission.value,
                onClick = {
                    navController.navigate(NavigationScreens.Onboarding.Nfc)
                },
                modifier = textModifier
            ) {
                Text("Next")
            }
            if (hasAccessibilityPermission.value) {
                Button(
                    onClick = {
                        settingViewModel.onEvent(SettingEvent.CompleteOnboarding())
                        settingViewModel.onEvent(SettingEvent.SaveSetting)
                        navController.navigate(NavigationScreens.Status)
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Skip tutorial")
                }
            }
        }
    }
}

@Composable
fun OnboardingScreenNfcTag(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel) {
    val settingState = settingViewModel.settingState.collectAsState().value
    val isTagIdSet = settingState.tagId?.isNotEmpty() ?: false

    val textTransparency = if (isTagIdSet) 0.6f else 0.9f

    Scaffold(
        topBar = { TopOnboardingAppBar(navController, navigationViewModel, settingViewModel)}
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
                text = "It's time to scan the tag you'll use to Boxx/Un-boxx your device. " +
                        "Scan it now!",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = textTransparency)
            )

            if (isTagIdSet) {
                Card(
                    modifier = textModifier
                ) {
                    Text(
                        text = "Excellent, let's set up your app restrictions!",
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

            Button(
                enabled = isTagIdSet,
                onClick = {
                    navController.navigate(NavigationScreens.Onboarding.Apps)
                },
                modifier = textModifier
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun OnboardingScreenApps(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel) {
    SettingsScreenApps(navController, navigationViewModel, settingViewModel, isOnboarding = true)
}

@Composable
fun OnboardingScreenEmergencyUnlock(navController: NavController, navigationViewModel: NavigationViewModel, settingViewModel: SettingViewModel) {
    Scaffold(
        topBar = { TopOnboardingAppBar(navController, navigationViewModel, settingViewModel)}
    ) { padding ->
        Column(
            modifier = Modifier.screenColumnModifier(padding, rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "All Set",
                modifier = textModifier,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
            Text(
                text = "That's it! We hope you enjoy using the app!",
                modifier = textModifier
            )
            Button(
                onClick = {
                    settingViewModel.onEvent(SettingEvent.CompleteOnboarding())
                    settingViewModel.onEvent(SettingEvent.SaveSetting)
                    navController.navigate(NavigationScreens.Status)
                },
                modifier = textModifier
            ) {
                Text("Finish")
            }
        }
    }
}
