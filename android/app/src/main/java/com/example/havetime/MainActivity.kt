package com.example.havetime

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.havetime.presentation.navigation.CalendarNavHost
import com.example.havetime.presentation.worker.SyncDataWorker
import com.example.havetime.ui.theme.HaveTimeTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        // 2. Устанавливаем User Agent (без этого сервер OSM не отдаст карту)
        SyncDataWorker.plannedSyncWorker(ctx)
        Configuration.getInstance().userAgentValue = packageName
        setContent {
            HaveTimeTheme(dynamicColor = false)  {
                CalendarNavHost()
            }
        }
    }
}
