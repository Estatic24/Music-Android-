package com.example.task3.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import com.example.task3.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class MusicWidgetState(
    val title: String,
    val artist: String,
    val albumArt: Int,
    val resId: Int,
    val isPlaying: Boolean
) {
    companion object {
        private var currentIndex = 0
        private var isPlaying = false

        private const val PREFS_NAME = "MusicWidgetPrefs"
        private const val KEY_INDEX = "current_index"
        private const val KEY_PLAYING = "is_playing"

        private val trackList = listOf(
            Track(R.raw.track1, "Track 1", "Eminem", R.drawable.background_music),
            Track(R.raw.track2, "Track 2", "Eminem", R.drawable.background_music),
            Track(R.raw.track3, "Track 3", "Eminem", R.drawable.background_music),
            Track(R.raw.track4, "Track 4", "Eminem", R.drawable.background_music)
        )

        data class Track(val resId: Int, val title: String, val artist: String, val albumArt: Int)

        fun getCurrentState(context: Context): MusicWidgetState {
            loadState(context)
            if (trackList.isEmpty()) {
                Log.e("MusicWidget", "trackList пуст! Виджет не сможет загрузить данные.")
            }
            val track = trackList.getOrNull(currentIndex) ?: return MusicWidgetState(
                title = "Ошибка загрузки",
                artist = "Неизвестно",
                albumArt = R.drawable.background_music,
                resId = R.raw.track1,
                isPlaying = false
            )
            Log.d("MusicWidget", "Текущий трек: ${track.title}, isPlaying: $isPlaying")
            return MusicWidgetState(track.title, track.artist, track.albumArt, track.resId , isPlaying)
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
            loadState(context)
            saveState(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    MusicWidget().updateAll(context)
                } catch (e: Exception) {
                    Log.e("MusicWidget", "Ошибка обновления виджета: ${e.message}")
                }
            }
        }


        private fun saveState(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            prefs.putInt(KEY_INDEX, currentIndex)
            prefs.putBoolean(KEY_PLAYING, isPlaying)
            prefs.apply()
        }

        fun loadState(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            currentIndex = prefs.getInt(KEY_INDEX, 0)
            isPlaying = prefs.getBoolean(KEY_PLAYING, false)
            if (currentIndex >= trackList.size) {
                currentIndex = 0
            }
        }
    }
}
