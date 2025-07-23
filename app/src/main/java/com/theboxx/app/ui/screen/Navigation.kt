package com.theboxx.app.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.theboxx.app.SettingViewModel


@Composable
fun Navigation(viewModel: SettingViewModel) {
    val navController = rememberNavController()

    val currentScreen by remember {
        mutableStateOf<Any>(Screens.StatusScreen)
    }

    Scaffold(
        topBar = {TopBoxxAppBar(navController)},
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {BottomNavigationBar(navController)},
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screens.StatusScreen
        ) {
            composable<Screens.StatusScreen> {
                StatusScreen(padding, viewModel)
            }
            composable<Screens.InfoScreen> {
                InfoScreen(padding)
            }
            navigation<Screens.SettingsScreen>(startDestination = Screens.SettingsScreen.Main) {
                composable<Screens.SettingsScreen.Main> {
                    SettingsScreenMain(padding, navController, viewModel)
                }
                composable<Screens.SettingsScreen.Apps> {
                    SettingsScreenApps(padding, viewModel)
                }
                composable<Screens.SettingsScreen.EmergencyUnlock> {
                    SettingsScreenEmergencyUnlock(padding, viewModel)
                }
            }
        }
    }
}

fun getInfoFromRoute(route: String): Screens {
    val screen: Screens = when (route) {
        Screens.StatusScreen::class.qualifiedName -> {
            Screens("Home", Screens.StatusScreen )
        }
        Screens.SettingsScreen::class.qualifiedName -> {
            Screens("Settings", Screens.SettingsScreen)
        }
        Screens.SettingsScreen.Main::class.qualifiedName -> {
            Screens("Settings", Screens.SettingsScreen)
        }
        Screens.InfoScreen::class.qualifiedName -> {
            Screens("Info", Screens.InfoScreen)
        }
        Screens.SettingsScreen.Apps::class.qualifiedName -> {
            Screens("Apps", Screens.SettingsScreen.Apps)
        }
        Screens.SettingsScreen.EmergencyUnlock::class.qualifiedName -> {
            Screens("Emergency Unlock", Screens.SettingsScreen.EmergencyUnlock)
        }
        else -> {
            Screens("", RuntimeException())
        }
    }
    return screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBoxxAppBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination?.route ?: ""
    val info = getInfoFromRoute(destination)

    val conditionScreens = listOf<Any>(Screens.StatusScreen, Screens.SettingsScreen, Screens.InfoScreen).contains(info.screen)
    if (!conditionScreens) {
        TopAppBar(
            title = {
                Text(
                    text = info.title
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
            }
        )
    }
}

data class BottomNavigationBarItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val screen: Any
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val bottomNavigationBarItems = listOf(
        BottomNavigationBarItem(
            title = "Home",
            unselectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home,
            screen = Screens.StatusScreen
        ),
        BottomNavigationBarItem(
            title = "Settings",
            unselectedIcon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings,
            screen = Screens.SettingsScreen
        ),
        BottomNavigationBarItem(
            title = "Info",
            unselectedIcon = Icons.Outlined.Info,
            selectedIcon = Icons.Filled.Info,
            screen = Screens.InfoScreen
        )
    )

    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    var previousSelectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        bottomNavigationBarItems.forEachIndexed { index, item ->
            val isSelected = selectedItemIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    previousSelectedItemIndex = selectedItemIndex
                    selectedItemIndex = index
                    navController.navigate(item.screen) {
                        popUpTo(bottomNavigationBarItems[previousSelectedItemIndex].screen) { inclusive = true}
                    }
                },
                label = {
                    Text(
                        text = item.title
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                }
            )
        }
    }

}