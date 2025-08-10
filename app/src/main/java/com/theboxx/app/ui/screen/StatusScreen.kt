package com.theboxx.app.ui.screen

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theboxx.app.R
import com.theboxx.app.SettingViewModel

@Composable
fun StatusScreen(padding: PaddingValues, viewModel: SettingViewModel) {
    val settingState by viewModel.settingState.collectAsState()
    if (settingState.isLoading) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
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
                .background(MaterialTheme.colorScheme.background)
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