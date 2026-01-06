package com.theboxx.app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
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
    val navigationState = navigationViewModel.navigationState.collectAsState().value
    val currentScreen = navigationState.currentScreen

    val isOnboarded = settingViewModel.settingState.collectAsState().value.isOnboarded
    val startScreen = if (isOnboarded) NavigationScreens.Status else NavigationScreens.Onboarding
    val bottomNavCondition = (currentScreen == NavigationScreens.Status ||
            currentScreen == NavigationScreens.Settings ||
            currentScreen == NavigationScreens.Settings.Main ||
            currentScreen == NavigationScreens.Info)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        contentWindowInsets = WindowInsets.Companion.safeDrawing,
        bottomBar = {
            AnimatedContent(bottomNavCondition) { bottomNavCondition ->
                if (bottomNavCondition) {
                    BottomNavigationBar(navController, navigationViewModel)
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            startDestination = startScreen,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) }
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
            homeScreen(
                screenObject = NavigationScreens.Status,
                navigationViewModel = navigationViewModel
            ) {
                StatusScreen(settingViewModel)
            }
            homeScreen(
                screenObject = NavigationScreens.Info,
                navigationViewModel = navigationViewModel
            ) {
                InfoScreen(navController, navigationViewModel)
            }
            navigation<NavigationScreens.Settings>(startDestination = NavigationScreens.Settings.Main) {
                homeScreen(
                    screenObject = NavigationScreens.Settings.Main,
                    navigationViewModel = navigationViewModel
                ) {
                    SettingsScreenMain(navController, settingViewModel)
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

    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    var previousSelectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        val navigationState = navigationViewModel.navigationState.collectAsState()
        bottomNavigationBarItems.forEachIndexed { index, item ->
            val isSelected = navigationState.value.currentScreen == item.screen || item.children.contains(navigationState.value.currentScreen)
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    previousSelectedItemIndex = selectedItemIndex
                    selectedItemIndex = index
                    if (!isSelected) {
                        navController.navigate(item.screen) {
                            launchSingleTop = true
                            popUpTo(bottomNavigationBarItems[previousSelectedItemIndex].screen)
                        }
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

@OptIn(ExperimentalAnimationApi::class)
inline fun <reified S : Screen> NavGraphBuilder.homeScreen(
    screenObject: S,
    navigationViewModel: NavigationViewModel,
    crossinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    val enterTransition =
        fadeIn() +
        scaleIn(initialScale = 0.7f)

    val exitTransition =
        fadeOut() +
        scaleOut(targetScale = 0.7f)

    val popEnterTransition =
        fadeIn() +
        scaleIn(initialScale = 0.7f)

    val popExitTransition =
        fadeOut() +
        scaleOut(targetScale = 0.7f)

    composable<S>(
        enterTransition = {enterTransition},
        exitTransition = {exitTransition},
        popEnterTransition = {popEnterTransition},
        popExitTransition = {popExitTransition}
    ) { navBackStackEntry ->
        navigationViewModel.onEvent(NavigationEvent.SetCurrentScreen(screenObject))
        content(navBackStackEntry)
    }
}