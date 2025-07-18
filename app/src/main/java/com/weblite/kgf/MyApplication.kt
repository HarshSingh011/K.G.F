package com.weblite.kgf

import android.app.Application
import com.weblite.kgf.Api2.SharedPrefManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize SharedPrefManager
        SharedPrefManager.init(this)
    }
}