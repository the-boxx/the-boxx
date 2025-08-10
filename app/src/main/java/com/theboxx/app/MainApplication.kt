package com.theboxx.app

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.theboxx.app.data.settings.SettingEvent
import com.theboxx.app.ui.navigation.NavigationEvent
import com.theboxx.app.ui.navigation.NavigationScreens
import com.theboxx.app.ui.navigation.NavigationViewModel
import com.theboxx.app.ui.screen.InfoScreen
import com.theboxx.app.ui.screen.OnboardingScreenAccessibility
import com.theboxx.app.ui.screen.OnboardingScreenApps
import com.theboxx.app.ui.screen.OnboardingScreenEmergencyUnlock
import com.theboxx.app.ui.screen.OnboardingScreenMain
import com.theboxx.app.ui.screen.OnboardingScreenNfcTag
import com.theboxx.app.ui.screen.Screen
import com.theboxx.app.ui.screen.SettingsScreenApps
import com.theboxx.app.ui.screen.SettingsScreenEmergencyUnlock
import com.theboxx.app.ui.screen.SettingsScreenMain
import com.theboxx.app.ui.screen.SettingsScreenNfcTag
import com.theboxx.app.ui.screen.SettingsScreenRestartOnboarding
import com.theboxx.app.ui.screen.StatusScreen

@Composable
fun MainApplication(context: Context, settingViewModel: SettingViewModel, navigationViewModel: NavigationViewModel) {
    val navController = rememberNavController()

    val isOnboarded = settingViewModel.settingState.collectAsState().value.isOnboarded
    val startScreen = if (isOnboarded) NavigationScreens.Status else NavigationScreens.Onboarding

//    Scaffold(
//        topBar = { TopBoxxAppBar(navController, settingViewModel, navigationViewModel) },
//        contentWindowInsets = WindowInsets.Companion.safeDrawing,
//        bottomBar = { if (isOnboarded) BottomNavigationBar(navController, navigationViewModel) },
//    ) { padding ->
    NavHost(
        navController = navController,
        startDestination = startScreen
    ) {
        navigation<NavigationScreens.Onboarding>(startDestination = NavigationScreens.Onboarding.Main) {
            screen(
                screenObject = NavigationScreens.Onboarding.Main,
                navigationViewModel = navigationViewModel
            ) {
                OnboardingScreenMain(navController)
            }
            screen(
                screenObject = NavigationScreens.Onboarding.Accessibility,
                navigationViewModel = navigationViewModel
            ) {
                OnboardingScreenAccessibility(navController, navigationViewModel, settingViewModel, context)
            }
            screen(
                screenObject = NavigationScreens.Onboarding.Nfc,
                navigationViewModel = navigationViewModel
            ) {
                OnboardingScreenNfcTag(navController, navigationViewModel, settingViewModel)
            }
            screen(
                screenObject = NavigationScreens.Onboarding.Apps,
                navigationViewModel = navigationViewModel
            ) {
                OnboardingScreenApps(
                    navController,
                    navigationViewModel,
                    settingViewModel
                )
            }
            screen(
                screenObject = NavigationScreens.Onboarding.EmergencyUnlock,
                navigationViewModel = navigationViewModel
            ) {
                OnboardingScreenEmergencyUnlock(navController, navigationViewModel, settingViewModel)
            }
        }
        screen(
            screenObject = NavigationScreens.Status,
            navigationViewModel = navigationViewModel
        ) {
            StatusScreen(navController, navigationViewModel, settingViewModel)
        }
        screen(
            screenObject = NavigationScreens.Info,
            navigationViewModel = navigationViewModel
        ) {
            InfoScreen(navController, navigationViewModel)
        }
        navigation<NavigationScreens.Settings>(startDestination = NavigationScreens.Settings.Main) {
            screen(
                screenObject = NavigationScreens.Settings.Main,
                navigationViewModel = navigationViewModel
            ) {
                SettingsScreenMain(navController, navigationViewModel, settingViewModel)
            }
            screen(
                screenObject = NavigationScreens.Settings.Apps,
                navigationViewModel = navigationViewModel
            ) {
                SettingsScreenApps(
                    navController,
                    navigationViewModel,
                    settingViewModel
                )
            }
            screen(
                screenObject = NavigationScreens.Settings.EmergencyUnlock,
                navigationViewModel = navigationViewModel
            ) {
                SettingsScreenEmergencyUnlock(navController, navigationViewModel, settingViewModel)
            }
            screen(
                screenObject = NavigationScreens.Settings.NfcTag,
                navigationViewModel = navigationViewModel
            ) {
                SettingsScreenNfcTag(navController, navigationViewModel, settingViewModel)
            }
            screen(
                screenObject = NavigationScreens.Settings.RestartOnboarding,
                navigationViewModel = navigationViewModel
            ) {
                SettingsScreenRestartOnboarding(navController, navigationViewModel, settingViewModel)
            }
        }
    }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBoxxAppBar(navController: NavController, settingViewModel: SettingViewModel, navigationViewModel: NavigationViewModel) {
    val navigationState = navigationViewModel.navigationState.collectAsState().value
    val currentScreen = navigationState.currentScreen
    val isAppSearchEnabled = navigationState.isAppSearchEnabled

    val topBarConditionScreens = listOf(
        NavigationScreens.Onboarding, NavigationScreens.Onboarding.Main,
        NavigationScreens.Status, NavigationScreens.Settings, NavigationScreens.Info).contains(currentScreen)
    if (isAppSearchEnabled) {
        SearchBar(
            inputField = {

            },
            expanded = true,
            onExpandedChange = {}
        ) {

        }
    } else {
        if (!topBarConditionScreens) {
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
                            contentDescription = "Go Back",
                        )
                    }
                },
                actions = {
                    val dropdownConditionScreens = listOf(
                        NavigationScreens.Onboarding.Nfc,
                        NavigationScreens.Onboarding.Apps,
                        NavigationScreens.Onboarding.EmergencyUnlock
                    ).contains(currentScreen)
                    val dropdownMenuEnabled = remember { mutableStateOf(false) }
                    if (dropdownConditionScreens) {
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
                }
            )
        }
    }
}

data class BottomNavigationBarItem(
    val screen: Screen,
    val children: Set<Screen> = setOf(),
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

@Composable
fun BottomNavigationBar(navController: NavController, navigationViewModel: NavigationViewModel) {
    val bottomNavigationBarItems = listOf(
        BottomNavigationBarItem(
            screen = NavigationScreens.Status,
            unselectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home,
        ),
        BottomNavigationBarItem(
            screen = NavigationScreens.Settings,
            children = setOf(
                NavigationScreens.Settings.Apps,
                NavigationScreens.Settings.Main,
                NavigationScreens.Settings.NfcTag,
                NavigationScreens.Settings.EmergencyUnlock,
                NavigationScreens.Settings.RestartOnboarding
            ),
            unselectedIcon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings,
        ),
        BottomNavigationBarItem(
            screen = NavigationScreens.Info,
            unselectedIcon = Icons.Outlined.Info,
            selectedIcon = Icons.Filled.Info,
        )
    )

//    var selectedItemIndex by rememberSaveable {
//        mutableIntStateOf(0)
//    }
//    var previousSelectedItemIndex by rememberSaveable {
//        mutableIntStateOf(0)
//    }

    NavigationBar {
        val navigationState = navigationViewModel.navigationState.collectAsState()
        bottomNavigationBarItems.forEachIndexed { index, item ->
            val isSelected = navigationState.value.currentScreen == item.screen || item.children.contains(navigationState.value.currentScreen)
            NavigationBarItem(
                selected = isSelected,
                onClick = {
//                    previousSelectedItemIndex = selectedItemIndex
//                    selectedItemIndex = index
                    navController.navigate(item.screen) {
                        launchSingleTop = true
                    }

                },
                label = {
                    Text(
                        text = item.screen.title
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.screen.title
                    )
                }
            )
        }
    }

}

inline fun <reified S : Screen> NavGraphBuilder.screen(
    screenObject: S,
    navigationViewModel: NavigationViewModel,
    crossinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable<S> { navBackStackEntry ->
        navigationViewModel.onEvent(NavigationEvent.SetCurrentScreen(screenObject))
        content(navBackStackEntry)
    }
}