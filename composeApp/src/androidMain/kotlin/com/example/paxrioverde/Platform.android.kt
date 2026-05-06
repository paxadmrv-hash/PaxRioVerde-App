package com.example.paxrioverde

import android.content.Intent
import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

fun getInitialNavigation(intent: Intent?): String? {
    return intent?.getStringExtra("navigate_to")
}
