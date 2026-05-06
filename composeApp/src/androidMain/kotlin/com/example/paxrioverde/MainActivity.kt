package com.example.paxrioverde

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        AndroidContext.initialize(this)
        requestNotificationPermission()

        val initialScreen = when (getInitialNavigation(intent)) {
            "finance" -> Screen.Finance
            else -> null
        }

        setContent {
            App(initialScreen = initialScreen)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
