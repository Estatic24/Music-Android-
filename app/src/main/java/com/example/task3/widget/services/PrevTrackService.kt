package com.example.task3.widget.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.glance.appwidget.updateAll
import com.example.task3.widget.MusicWidget
import com.example.task3.widget.MusicWidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class PrevTrackService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MusicWidgetState.prevTrack(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            MusicWidget().updateAll(applicationContext)
        }
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

