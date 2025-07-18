package com.theboxx.app

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theboxx.app.ui.theme.TheBoxxTheme
import com.theboxx.app.ui.theme.avenirNextFamily

class BlockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launchedPackageName = intent.getStringExtra("LAUNCHED_PACKAGE_NAME")

        enableEdgeToEdge()
        setContent {
            TheBoxxTheme {
                Column(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,

                    ) {
                    Row {
                        Image(
                            Icons.Rounded.Close,
                            contentDescription = "Blocked",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.fillMaxWidth()
                                .height(150.dp),

                            )
                    }
                    Row {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (launchedPackageName != null) {
                                    "We blocked \"$launchedPackageName\" from opening"
                                } else {
                                    "Error: Blocked, but no package name was provided"
                                },
                                modifier = Modifier
                                    .width(LocalConfiguration.current.screenWidthDp.dp - 100.dp),
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp,
                                color = Color.White,

                                )
                        }
                    }
                }
            }
        }

    }
}