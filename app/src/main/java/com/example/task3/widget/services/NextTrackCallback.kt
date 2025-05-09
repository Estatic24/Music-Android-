package com.example.task3.widget.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.example.task3.widget.MusicWidget
import com.example.task3.widget.MusicWidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrevTrackCallback : BroadcastReceiver(), ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        MusicWidgetState.prevTrack(context)
        MusicWidget().update(context, glanceId)
        MusicWidget().updateAll(context)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.Main).launch {
            MusicWidgetState.togglePlayState(context)
            MusicWidget().updateAll(context)
        }
    }
}
