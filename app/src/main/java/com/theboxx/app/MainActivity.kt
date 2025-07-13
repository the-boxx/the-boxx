package com.theboxx.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theboxx.app.data.SettingDatabase
import com.theboxx.app.data.SettingEvent
import com.theboxx.app.data.SettingState
import com.theboxx.app.ui.theme.TheBoxxTheme
import com.theboxx.app.ui.theme.avenirNextFamily

class MainActivity(
    private val eventTriggered: EventTriggered? = null
) : ComponentActivity() {


    private val settingDb by lazy {
        SettingDatabase.getDatabase(applicationContext)
    }

    private val viewModel by viewModels<SettingViewModel>(
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
                val settingState by viewModel.settingState.collectAsState()

                if (settingState.isLoading) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }
                } else {
                    var eventTriggered =
                        intent.getSerializableExtra("eventTriggered") as? EventTriggered
                    if (eventTriggered != null) {
                        when (eventTriggered) {
                            EventTriggered.SWITCH_BOXX_STATE -> {
                                Log.d("asand", "Switching boxx state")
                                Log.d("asand", "Boxx state: ${settingState.boxxState}")
                                viewModel.onEvent(SettingEvent.SetBoxxState(!settingState.boxxState))
                                viewModel.onEvent(SettingEvent.SaveSetting)
                                intent.removeExtra("eventTriggered")
                            }
                        }
                    }
                    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                        val padding = padding
                        BoxxStateView(settingState, viewModel::onEvent)
                    }
                }
            }
        }
    }


}


@Composable
fun BoxxStateView(state: SettingState, onEvent: (SettingEvent) -> Unit) {

    val context = LocalContext.current

    val boxxText: String =
        if (state.boxxState) {
            "Boxxed"
        } else {
            "Un-Boxxed"
        }
    val boxxImage: Int =
        if (state.boxxState) {
            R.drawable.boxxed
        } else {
            R.drawable.unboxxed
        }


    val backgroundColor = if (state.boxxState) Color.DarkGray else Color.Black

//    val backgroundImage = R.drawable.bg
//    Image(
//        painter = painterResource(id = backgroundImage),
//        contentDescription = "Background Image",
//        modifier = Modifier.fillMaxSize(),
//        contentScale = ContentScale.FillBounds
//    )

    Image(
        painter = painterResource(boxxImage),
        contentDescription = "Boxx",
        modifier = Modifier
            .fillMaxSize()

    )
    Column(
        modifier = Modifier
//            .background(backgroundColor)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row {
            Text(
                text = "Current State",
                fontFamily = avenirNextFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(12.dp, 12.dp, 12.dp, 0.dp)
            )
        }
        Row {
            Text(
                text = boxxText,
                fontFamily = avenirNextFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(12.dp, 6.dp, 12.dp, 200.dp)
            )
        }
        Row {
            Box() {
                Button(
                    onClick = {
                        val intent = Intent(context, SettingsActivity::class.java)

                        context.startActivity(intent)
                    }
                ) {
                    Text(
                        text = "Open Settings"
                    )
                }
            }
        }
        Row {
            Box(
            ) {
                Button(
                    onClick = {
                        onEvent(SettingEvent.SetBoxxState(!state.boxxState))
                        onEvent(SettingEvent.SetCurrentProfile(state.currentProfile))
                        onEvent(SettingEvent.SaveSetting)
                    }
                ) {
                    Text(text = "Switch")
                }
            }
        }
//        Row {
//            Text(
//                text = "Current profile: " + state.currentProfile.toString(),
//                modifier = Modifier
//                    .padding(12.dp)
//            )
//        }
//        Row {
//            Box(
//                modifier = Modifier
////                    .fillMaxWidth()
//            ) {
//                Button(
//                    onClick = {
//                        val profile = state.currentProfile + 1
//                        onEvent(SettingEvent.SetCurrentProfile(profile))
//                        onEvent(SettingEvent.SetBoxxState(state.boxxState))
//                        onEvent(SettingEvent.SaveSetting)
//                    }
//                ) {
//                    Text(text = "Next Profile")
//                }
//            }
//        }
    }
}
