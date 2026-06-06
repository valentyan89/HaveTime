package com.example.havetime

import android.app.Application
import com.example.havetime.di.AppModule
import org.osmdroid.config.Configuration

class HaveTimeApplication : Application() {

    lateinit var appModule: AppModule

    override fun onCreate() {
        super.onCreate()

        appModule = AppModule(this)

        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this))
    }
}