package com.example.task3.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.task3.widget.services.PlayPauseService
import com.example.task3.widget.services.NextTrackService
import com.example.task3.widget.services.PrevTrackService

class MusicWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicWidget()
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "TOGGLE_PLAY" -> {
                context.startForegroundService(Intent(context, PlayPauseService::class.java))
            }
            "NEXT_TRACK" -> {
                context.startForegroundService(Intent(context, NextTrackService::class.java))
            }
            "PREV_TRACK" -> {
                context.startForegroundService(Intent(context, PrevTrackService::class.java))
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            MusicWidget().updateAll(context)
        }
    }
}
