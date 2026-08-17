package com.example.paxrioverde.util

import android.content.pm.ApplicationInfo
import com.example.paxrioverde.AndroidContext

actual val isDebug: Boolean by lazy {
    val context = AndroidContext.get()
    (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}
