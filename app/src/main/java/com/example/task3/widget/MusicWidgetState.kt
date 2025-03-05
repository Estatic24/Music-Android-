package com.example.task3.widget

import android.content.Context
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.glance.appwidget.updateAll
import com.example.task3.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class MusicWidgetState(
    val title: String,
    val artist: String,
    val albumArt: Int,
    val isPlaying: Boolean
) {
    companion object {
        private var currentIndex = 0
        private var isPlaying = false

        private const val PREFS_NAME = "MusicWidgetPrefs"
        private const val KEY_INDEX = "current_index"
        private const val KEY_PLAYING = "is_playing"

        private val trackList = listOf(
            Track(R.raw.track1, "Track 1", R.drawable.background_music),
            Track(R.raw.track2, "Track 2", R.drawable.background_music),
            Track(R.raw.track3, "Track 3", R.drawable.background_music),
            Track(R.raw.track4, "Track 4", R.drawable.background_music)
        )

        data class Track(val resId: Int, val title: String, val albumArt: Int)

        fun getCurrentState(context: Context): MusicWidgetState {
            loadState(context)
            val track = trackList[currentIndex]
            return MusicWidgetState(track.title, "Unknown Artist", track.albumArt, isPlaying)
        }

        fun togglePlayState(context: Context) {
            isPlaying = !isPlaying
            updateWidget(context)
        }

        fun nextTrack(context: Context) {
            currentIndex = (currentIndex + 1) % trackList.size
            isPlaying = true
            updateWidget(context)
        }

        fun prevTrack(context: Context) {
            currentIndex = if (currentIndex > 0) currentIndex - 1 else trackList.size - 1
            isPlaying = true
            updateWidget(context)
        }

        private fun updateWidget(context: Context) {
            saveState(context)
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    MusicWidget().updateAll(context)
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val componentName = ComponentName(context, MusicWidgetReceiver::class.java)
                    val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
                    appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds, android.R.id.content)
                } catch (_: Exception) {}
            }
        }

        private fun saveState(context: Context) {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                prefs.putInt(KEY_INDEX, currentIndex)
                prefs.putBoolean(KEY_PLAYING, isPlaying)
                prefs.apply()
            } catch (_: Exception) {}
        }

        private fun loadState(context: Context) {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                currentIndex = prefs.getInt(KEY_INDEX, 0)
                isPlaying = prefs.getBoolean(KEY_PLAYING, false)
            } catch (_: Exception) {}
        }
    }
}
