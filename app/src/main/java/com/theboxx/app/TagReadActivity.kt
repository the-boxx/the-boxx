package com.theboxx.app

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class TagReadActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device. Go home...", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                NfcTagScanView(padding)
            }
        }

        handleIntent(intent)

    }

    override fun onResume() {
        super.onResume()

        nfcAdapter?.let {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                android.app.PendingIntent.getActivity(this, 0, intent, android.app.PendingIntent.FLAG_MUTABLE)
            } else {
                android.app.PendingIntent.getActivity(this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE)
            }

            it.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun onPause() {
        super.onPause()

        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                NfcTagScanView(padding)
            }
        }

        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == action
        ) {

            Toast.makeText(this, "Tag scanned", Toast.LENGTH_LONG).show()

            if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
                val rawMessages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableArrayExtra(
                        NfcAdapter.EXTRA_NDEF_MESSAGES,
                        NdefMessage::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                }

                if (rawMessages != null) {
                    val messages = rawMessages.map { it as NdefMessage }
                    for (message in messages) {
                        for (record in message.records) {
                            val payload = String(record.payload)
                            Log.d("NfcTagRead", "Payload: $payload")


                            redirectToMainActivity()
                        }
                    }
                } else {
                    // Handle other tag types (TECH_DISCOVERED, TAG_DISCOVERED)
                    // You might need to use the Tag object and classes from android.nfc.tech.*
                    val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, android.nfc.Tag::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                    }
                    tag?.let {
                        Log.d(
                            "NfcTagRead",
                            "Tag ID: ${it.id.joinToString("") { "%02x".format(it) }}"
                        )
                        // Further processing for non-NDEF tags
                        setContent {
                            Text("Tag ID: ${it.id.joinToString("") { "%02x".format(it) }}")
                        }
                    }
                }
            }
        }

    }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity()::class.java)

        Log.d("asand", "Redirecting to MainActivity")

        intent.putExtra("eventTriggered", EventTriggered.SWITCH_BOXX_STATE)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}

@Composable
fun NfcTagScanView(padding: PaddingValues) {

    Column(
        modifier = Modifier.padding(padding)
    ) {
        Row {
            Text(text = "Scan your tag :)")
        }
    }


}