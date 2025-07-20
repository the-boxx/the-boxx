package com.theboxx.app

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theboxx.app.data.SettingEvent
import com.theboxx.app.data.SettingState
import com.theboxx.app.ui.theme.TheBoxxTheme

class MainActivity(
    private val eventTriggered: EventTriggered? = null
) : ComponentActivity() {


    private var nfcAdapter: NfcAdapter? = null

    private var nfcPendingIntent: PendingIntent? = null

    private var nfcIntentFilters: Array<IntentFilter>? = null

    private fun initializeNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not available.", Toast.LENGTH_LONG).show()
            return
        }
        if (nfcAdapter?.isEnabled == false) {
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            return
        }

        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlags)

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
        initializeNfc()
        intent?.let { processNfcIntent(it) }


        enableEdgeToEdge()
        setContent {
            TheBoxxTheme {
                val settingState by viewModel.settingState.collectAsState()
                val context = LocalContext.current
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
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    val intent = Intent(context, SettingsActivity::class.java)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .padding(20.dp),
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    ) { padding ->
                        padding
                        BoxxStateView(settingState, viewModel::onEvent)
                    }
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processNfcIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        nfcAdapter?.let {
            if (it.isEnabled) {
                it.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilters, null)
            } else {
                Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun processNfcIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == action) {
            val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }

            tag?.id ?: "unknown"
            viewModel.onEvent(SettingEvent.SetIsTrusted(!viewModel.settingState.value.isTrusted))
        }
    }

}


@Composable
fun BoxxStateView(state: SettingState, onEvent: (SettingEvent) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = true,
                onClickLabel = "Switch Boxx State",
                onClick = {
                    onEvent(SettingEvent.SetBoxxState(!state.boxxState))
                    onEvent(SettingEvent.SaveSetting)
                }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            BoxxImage(state.boxxState, onEvent)
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
                targetState = state.boxxState,
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
    Column(
        modifier = Modifier
//            .background(backgroundColor)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {

        Row {
            Box {
                AnimatedContent(
                    targetState = state.isTrusted,
                ) { isTrusted ->
                    Icon(
                        painter = painterResource(if (isTrusted) R.drawable.lock_open_48px else R.drawable.lock_48px),
                        contentDescription = "Lock",
                        tint = Color(0xDDFFFFFF),
                        modifier = Modifier
                            .padding(48.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun BoxxImage(boxxState: Boolean, onEvent: (SettingEvent) -> Unit) {
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