package com.example.paxrioverde

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference

@SuppressLint("StaticFieldLeak")
object AndroidContext {
    private var context: Context? = null
    private var activityRef: WeakReference<Activity>? = null

    fun initialize(ctx: Context) {
        context = ctx.applicationContext
        if (ctx is Activity) {
            activityRef = WeakReference(ctx)
        }
    }

    fun get(): Context {
        return context ?: throw IllegalStateException("Context not initialized. Call AndroidContext.initialize(context) in MainActivity.")
    }

    fun getActivity(): Activity? = activityRef?.get()
}
