package com.example.havetime.data.repository

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import com.example.havetime.domain.repository.WidgetRepository
import com.example.havetime.presentation.widget.HaveTimeWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class WidgetRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WidgetRepository {
    override fun updateWidget() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                HaveTimeWidget().updateAll(context)
                Log.d("RRR", "widget updatedAll")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}