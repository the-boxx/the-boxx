package com.theboxx.app

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theboxx.app.data.settings.SettingEvent
import com.theboxx.app.ui.navigation.NavigationScreens
import com.theboxx.app.ui.navigation.NavigationViewModel
import com.theboxx.app.ui.theme.TheBoxxTheme

class MainActivity() : ComponentActivity() {

// NFC
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

    @Suppress("UNCHECKED_CAST")
    private val settingViewModel by viewModels<SettingViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingViewModel(applicationContext) as T
                }
            }
        }
    )

    @Suppress("UNCHECKED_CAST")
    private val navigationViewModel by viewModels<NavigationViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NavigationViewModel() as T
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
                MainApplication(applicationContext, settingViewModel, navigationViewModel)
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

    private fun bytesToHexString(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("0x")
        if (src == null || src.isEmpty()) {
            return null
        }

        val buffer = CharArray(2)
        for (i in src.indices) {
            buffer[0] = Character.forDigit((src[i].toInt() ushr 4) and 0x0F, 16)
            buffer[1] = Character.forDigit(src[i].toInt() and 0x0F, 16)
            println(buffer)
            stringBuilder.append(buffer)
        }

        return stringBuilder.toString()
    }

    private fun processNfcIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action) {
            val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }

            val tagId = bytesToHexString(tag?.id)

            Log.d("NfcReading", "Tag id: $tagId")

            val currentScreen = navigationViewModel.navigationState.value.currentScreen

            if (currentScreen == NavigationScreens.Status) {
                if (tagId == settingViewModel.settingState.value.tagId) {
                    settingViewModel.onEvent(SettingEvent.SetIsTrusted(true))

                    settingViewModel.onEvent(SettingEvent.SetBoxxState(!settingViewModel.settingState.value.boxxState))
                    settingViewModel.onEvent(SettingEvent.SaveSetting)
//                    Toast.makeText(this, "Switched Boxx State", Toast.LENGTH_SHORT).show()
                    settingViewModel.onEvent(SettingEvent.SetIsTrusted(false))
                } else {
                    Toast.makeText(this, "Incorrect tag scanned", Toast.LENGTH_LONG).show()
                }
            } else if (currentScreen == NavigationScreens.Settings.NfcTag ||
                currentScreen == NavigationScreens.Onboarding.Nfc) {
                if (!settingViewModel.settingState.value.boxxState) {
                    settingViewModel.onEvent(SettingEvent.SetTagId(tagId))
                    settingViewModel.onEvent(SettingEvent.SaveSetting)

                    Toast.makeText(this, "NFC Tag has been set", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}