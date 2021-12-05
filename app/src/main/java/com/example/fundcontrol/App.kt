package com.example.fundcontrol

import android.app.Application
import com.balram.locker.view.AppLocker

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppLocker.getInstance().enableAppLock(this)

        println("start App.kt")
    }



}