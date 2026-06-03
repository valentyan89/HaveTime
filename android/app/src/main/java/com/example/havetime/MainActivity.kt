package com.example.havetime

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.havetime.data.local.ActivityDataBase
import com.example.havetime.data.remote.api.AuthApi
import com.example.havetime.data.repository.UserRepositoryImpl
import com.example.havetime.domain.repository.UserRepository
import com.example.havetime.presentation.CalendarViewModel
//import com.example.havetime.presentation.test.TestMapScreen
//import com.example.havetime.presentation.navigation.CalendarNavHost
//import com.example.todolist.presentation.test.TestScreen
import kotlinx.coroutines.launch
import com.example.havetime.ui.theme.HaveTimeTheme
import org.osmdroid.config.Configuration
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val viewModel: CalendarViewModel by viewModels {
        CalendarViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        // 2. Устанавливаем User Agent (без этого сервер OSM не отдаст карту)
        Configuration.getInstance().userAgentValue = packageName
        setContent {
            HaveTimeTheme(dynamicColor = false)  {
//                CalendarNavHost()
//                TestMapScreen()
            }
        }
    }
}
