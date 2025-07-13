package com.theboxx.app

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.theboxx.app.ui.theme.TheBoxxTheme

class RequestDeviceAdminActivity : AppCompatActivity() {

    private lateinit var dpm: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    private lateinit var requestDeviceAdminLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, BoxxDeviceAdminReceiver::class.java)

        setContent {
            RequestDeviceAdminView(
                onRequestClick = {
                    requestDeviceAdminRights()
                }
            )
        }
    }


    fun requestDeviceAdminRights() {
        if (!dpm.isAdminActive(adminComponent)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            intent.putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Boxx requires device admin permissions to allow full restricting of the device."
            )
            requestDeviceAdminLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == RESULT_OK) {
                    Toast.makeText(this, "Device Admin has been enabled!", Toast.LENGTH_SHORT).show()
                    finishActivity(101)
                } else {
                    Toast.makeText(this, "Device admin enabling failed or was cancelled", Toast.LENGTH_LONG).show()
                    finishActivity(101)
                }
            }
            requestDeviceAdminLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Device admin already activated", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun RequestDeviceAdminView(onRequestClick: () -> Unit) {
    TheBoxxTheme {
        Button(
            onClick = {
                onRequestClick()
            },
            content = {
                Text("Request Device Admin")
            }
        )
    }
}