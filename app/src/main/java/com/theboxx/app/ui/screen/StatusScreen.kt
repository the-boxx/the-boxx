package com.theboxx.app.ui.screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.theboxx.app.BottomNavigationBar
import com.theboxx.app.R
import com.theboxx.app.SettingViewModel
import com.theboxx.app.data.settings.SettingEvent
import com.theboxx.app.data.system.accessibility.isAccessibilityServiceEnabled
import com.theboxx.app.ui.navigation.NavigationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(settingViewModel: SettingViewModel) {
    val settingState by settingViewModel.settingState.collectAsState()

    val currentContext = LocalContext.current
    val hasAccessibilityPermission = remember {
        mutableStateOf(false)
    }
    val openAccessibilityDialog = remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        settingViewModel.viewModelScope.launch {
            while (!hasAccessibilityPermission.value) {
                val permission = isAccessibilityServiceEnabled(currentContext)
                hasAccessibilityPermission.value = permission
                delay(1000L)
                if (openAccessibilityDialog.value == null) {
                    if (settingState.isOnboarded) {
                        openAccessibilityDialog.value = !hasAccessibilityPermission.value
                    } else {
                        openAccessibilityDialog.value = false
                    }
                }
            }
        }
    }

    if (openAccessibilityDialog.value == true) {
        BasicAlertDialog(
            onDismissRequest = {
                openAccessibilityDialog.value = false
            }
        ) {
            Card {
                Text(
                    text = "The Accessibility Service for The Boxx is disabled. Without this " +
                            "permission, The Boxx cannot properly restrict your device. " +
                            "Would you like to re-enable it?",
                    modifier = Modifier
                        .padding(12.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Button(
                        onClick = {
                            openAccessibilityDialog.value = false
                        },
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        Text("Not now")
                    }
                    Button(
                        onClick = {
                            currentContext.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            openAccessibilityDialog.value = false
                        },
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Take me there"
                        )
                    }
                }
            }
        }
    }

    Scaffold(
//        bottomBar = { BottomNavigationBar(navController, navigationViewModel) }
    ) { padding ->
        if (settingState.isLoading) {
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Row {
                    BoxxImage(settingState.boxxState)
                }
                Row {
                    Text(
                        text = "Current State",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(12.dp, 36.dp, 12.dp, 0.dp)
                    )
                }
                Row {
                    AnimatedContent(
                        targetState = settingState.boxxState,
                    ) { boxxState ->
                        val boxxText: String =
                            if (boxxState) {
                                "Boxxed"
                            } else {
                                "Un-Boxxed"
                            }
                        Text(
                            text = boxxText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            modifier = Modifier
                                .padding(12.dp, 6.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                ) {
                    AnimatedContent(
                        settingState.boxxState
                    ) { boxxState ->
                        val openDialog = remember { mutableStateOf(false) }
                        if (!boxxState) {
                            Button(
                                onClick = {
                                    openDialog.value = true
                                }
                            ) {
                                Text("Boxx Now")
                            }
                            if (openDialog.value) {
                                BasicAlertDialog(
                                    onDismissRequest = {
                                        openDialog.value = false
                                    }
                                ) {
                                    Card {
                                        Text(
                                            text = "Are you sure you'd like to boxx your device? " +
                                                "You will not be able to un-boxx without your boxx.",
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
                                                    settingViewModel.onEvent(SettingEvent.SetIsTrusted(true))
                                                    settingViewModel.onEvent(SettingEvent.SetBoxxState(true))
                                                    settingViewModel.onEvent(SettingEvent.SaveSetting)
                                                    settingViewModel.onEvent(SettingEvent.SetIsTrusted(false))
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
            }
        }
    }
}

@Composable
fun BoxxImage(boxxState: Boolean) {
    val animationSpec: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
    val animationSpecFloat: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
    val boxxAllOffsetX by animateDpAsState(
        targetValue = if (boxxState) (0).dp else 20.dp,
        animationSpec = animationSpec
    )
    Box(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 12.dp)
            .offset(x = boxxAllOffsetX),
    ) {
        Image(
            painter = painterResource(R.drawable.boxx_black),
            contentDescription = "Boxx",
            modifier = Modifier
        )
        val boxxLeftOffsetX by animateDpAsState(
            targetValue = if (boxxState) 0.dp else (-72).dp,
            animationSpec = animationSpec
        )
        val boxxTopOffsetY by animateDpAsState(
            targetValue = if (boxxState) 0.dp else (-72).dp,
            animationSpec = animationSpec
        )
        val boxxFrontOffsetX by animateDpAsState(
            targetValue = if (boxxState) (200/3).dp else 96.dp,
            animationSpec = animationSpec
        )
        val boxxFrontOffsetY by animateDpAsState(
            targetValue = if (boxxState) (200/3).dp else 96.dp,
            animationSpec = animationSpec
        )
        val boxxFrontScale by animateFloatAsState(
            targetValue = if (boxxState) 1f else 1.1f,
            animationSpec = animationSpecFloat
        )
        Image(
            painter = painterResource(R.drawable.boxx_left),
            contentDescription = "boxx left",
            modifier = Modifier
                .offset(boxxLeftOffsetX, 0.dp)
        )
        Image(
            painter = painterResource(R.drawable.boxx_top),
            contentDescription = "boxx top",
            modifier = Modifier
                .offset(0.dp, boxxTopOffsetY)
        )
        Image(
            painter = painterResource(R.drawable.boxx_front),
            contentDescription = "boxx front",
            modifier = Modifier
                .offset(boxxFrontOffsetX, boxxFrontOffsetY)
                .scale(boxxFrontScale)
        )
    }
}