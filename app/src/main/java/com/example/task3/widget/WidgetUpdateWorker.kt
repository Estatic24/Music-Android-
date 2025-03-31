package com.example.task3.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WidgetUpdateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            withContext(Dispatchers.Main) {
                MusicWidget().updateAll(applicationContext)
            }
            Log.d("WidgetUpdateWorker", "Виджет обновлен")
            Result.success()
        } catch (e: Exception) {
            Log.e("WidgetUpdateWorker", "Ошибка обновления виджета: ${e.message}")
            Result.retry()
        }
    }
}
