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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Scaffold
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theboxx.app.data.SettingEvent
import com.theboxx.app.ui.screen.BottomNavigationBar
import com.theboxx.app.ui.screen.Navigation
import com.theboxx.app.ui.theme.TheBoxxTheme

class MainActivity(
    private val eventTriggered: EventTriggered? = null
) : ComponentActivity() {

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
                Navigation(viewModel)
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
            viewModel.onEvent(SettingEvent.SetBoxxState(!viewModel.settingState.value.boxxState))
            viewModel.onEvent(SettingEvent.SaveSetting)
            Toast.makeText(this, "Switched Boxx State", Toast.LENGTH_SHORT).show()
        }
    }

}