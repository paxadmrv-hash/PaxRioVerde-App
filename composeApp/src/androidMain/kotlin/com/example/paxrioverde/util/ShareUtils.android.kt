package com.example.paxrioverde.util

import android.content.Intent
import com.example.paxrioverde.AndroidContext

actual fun shareText(text: String, title: String) {
    val context = AndroidContext.get()
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val shareIntent = Intent.createChooser(sendIntent, title).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(shareIntent)
}
