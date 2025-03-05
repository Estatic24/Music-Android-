package com.example.task3.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.task3.widget.services.NextTrackCallback
import com.example.task3.widget.services.PlayPauseCallback
import com.example.task3.widget.services.PrevTrackCallback

class MusicWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        MusicWidgetState.loadState(context)

        val callback = when (intent.action) {
            "TOGGLE_PLAY" -> actionRunCallback<PlayPauseCallback>()
            "NEXT_TRACK" -> actionRunCallback<NextTrackCallback>()
            "PREV_TRACK" -> actionRunCallback<PrevTrackCallback>()
            else -> null
        }

        if (callback != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    MusicWidget().updateAll(context)
                } catch (e: Exception) {
                    android.util.Log.e("MusicWidgetReceiver", "Ошибка обновления виджета: ${e.message}")
                }
            }
        }
    }
}
