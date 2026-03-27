package com.example.paxrioverde

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AndroidContext {
    private var context: Context? = null

    fun initialize(ctx: Context) {
        context = ctx.applicationContext
    }

    fun get(): Context {
        return context ?: throw IllegalStateException("Context not initialized. Call AndroidContext.initialize(context) in MainActivity.")
    }
}
