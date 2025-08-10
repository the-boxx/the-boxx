package com.theboxx.app.ui.screen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.theboxx.app.AppLaunchDetectionService
import com.theboxx.app.R
import com.theboxx.app.SettingViewModel
import com.theboxx.app.data.settings.SettingEvent
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
fun OnboardingScreenMain(padding: PaddingValues, navController: NavController, viewModel: SettingViewModel) {
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

private fun isAccessibilityServiceEnabled(context: Context, serviceComponentIdentifier: String): Boolean {
    val accessibilityEnabled = try {
        Settings.Secure.getInt(context.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
    } catch (e: Settings.SettingNotFoundException) {
        Log.e("AccessibilityCheck", "ACCESSIBILITY_ENABLED setting not found", e)
    }

    if (accessibilityEnabled == 0) {
        Log.d("AccessibilityCheck", "Accessibility is globally disabled.")
        return false
    }

    val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    if (enabledServicesSetting == null) {
        return false
    }
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)

    return colonSplitter.contains(serviceComponentIdentifier)
}

@Composable
fun OnboardingScreenAccessibility(padding: PaddingValues, navController: NavController, viewModel: SettingViewModel, context: Context) {
    val currentContext = LocalContext.current
    val hasAccessibilityPermission = remember {
        mutableStateOf(false)
    }

    val serviceComponentIdentifier = "${context.packageName}/${AppLaunchDetectionService::class.java.name}"

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            while (!hasAccessibilityPermission.value) {
                val permission = isAccessibilityServiceEnabled(context, serviceComponentIdentifier)
                hasAccessibilityPermission.value = permission
                delay(1000L)
            }
        }
    }

    val textTransparency = if (hasAccessibilityPermission.value) 0.6f else 0.9f

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
                    viewModel.onEvent(SettingEvent.CompleteOnboarding())
                    viewModel.onEvent(SettingEvent.SaveSetting)
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

@Composable
fun OnboardingScreenNfcTag(padding: PaddingValues, navController: NavController, viewModel: SettingViewModel) {
    val settingState = viewModel.settingState.collectAsState().value
    val isTagIdSet = settingState.tagId?.isNotEmpty() ?: false

    val textTransparency = if (isTagIdSet) 0.6f else 0.9f

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

@Composable
fun OnboardingScreenApps(padding: PaddingValues, navController: NavController, settingViewModel: SettingViewModel, navigationViewModel: NavigationViewModel) {
    SettingsScreenApps(padding, navController, settingViewModel, navigationViewModel)
}

@Composable
fun OnboardingScreenEmergencyUnlock(padding: PaddingValues, navController: NavController, viewModel: SettingViewModel) {
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
                viewModel.onEvent(SettingEvent.CompleteOnboarding())
                viewModel.onEvent(SettingEvent.SaveSetting)
                navController.navigate(NavigationScreens.Status)
            },
            modifier = textModifier
        ) {
            Text("Finish")
        }
    }
}
