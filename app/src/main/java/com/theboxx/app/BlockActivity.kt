package com.theboxx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theboxx.app.ui.theme.TheBoxxTheme

class BlockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launchedPackageName = intent.getStringExtra("LAUNCHED_PACKAGE_NAME")

        enableEdgeToEdge()
        setContent {
            TheBoxxTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeContent
                ) { padding ->
                    BackHandler {
                        finish()
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                        Row {
                            Image(
                                Icons.Rounded.Close,
                                contentDescription = "Blocked",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
                                modifier = Modifier
                                    .size(150.dp),

                                )
                        }
                        Row {
                            Text(
                                text = if (launchedPackageName != null) {
                                    "We blocked \"$launchedPackageName\" from opening"
                                } else {
                                    "Error: Blocked, but no package name was provided"
                                },
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp,

                                )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                            }
                        }
                    }
                }
            }
        }

    }
}