package com.example.paxrioverde.util

import android.os.Build
import android.view.WindowManager
import com.example.paxrioverde.AndroidContext
import java.io.File

actual fun setScreenSecurity(enabled: Boolean) {
    val activity = AndroidContext.getActivity()
    activity?.run {
        runOnUiThread {
            if (enabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }
}

/**
 * Senior Detection: Combina verificação de Tags, Binários e Diretórios.
 */
actual fun isDeviceRooted(): Boolean {
    val buildTags = Build.TAGS
    if (buildTags != null && buildTags.contains("test-keys")) return true

    val paths = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su"
    )

    for (path in paths) {
        if (File(path).exists()) return true
    }

    // Executa 'su' para ver se o comando responde
    var process: Process? = null
    return try {
        process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
        val line = process.inputStream.bufferedReader().readLine()
        line != null
    } catch (t: Throwable) {
        false
    } finally {
        process?.destroy()
    }
}

