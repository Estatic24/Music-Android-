package com.example.task3.widget

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.task3.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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
        private var mediaPlayer: MediaPlayer? = null
        private var currentPosition = 0

        private const val PREFS_NAME = "MusicWidgetPrefs"
        private const val KEY_INDEX = "current_index"
        private const val KEY_PLAYING = "is_playing"
        private const val KEY_POSITION = "current_position"

        private val trackList = listOf(
            Track(R.raw.track1, "Track 1", "Eminem", R.drawable.background_music),
            Track(R.raw.track2, "Track 2", "Eminem", R.drawable.background_music2),
            Track(R.raw.track3, "Track 3", "Eminem", R.drawable.background_music3),
            Track(R.raw.track4, "Track 4", "Eminem", R.drawable.background_music4)
        )

        data class Track(val resId: Int, val title: String, val artist: String, val albumArt: Int)

        fun getCurrentState(context: Context): MusicWidgetState {
            loadState(context)
            val track = trackList.getOrNull(currentIndex) ?: return MusicWidgetState(
                title = "Ошибка загрузки",
                artist = "Неизвестно",
                albumArt = R.drawable.background_music,
                resId = R.raw.track1,
                isPlaying = false
            )
            return MusicWidgetState(track.title, track.artist, track.albumArt, track.resId, isPlaying)
        }


        fun getResizedAlbumArt(context: Context, resId: Int): Int {
            return try {
                val drawable = context.resources.getDrawable(resId, null)
                if (drawable != null) resId else R.drawable.background_music4
            } catch (e: Exception) {
                Log.e("MusicWidget", "Ошибка загрузки обложки: ${e.message}")
                R.drawable.background_music4
            }
        }


        fun togglePlayState(context: Context) {
            if (isPlaying) {
                pauseMusic()
            } else {
                resumeMusic(context)
            }
            isPlaying = !isPlaying
            saveState(context)
            CoroutineScope(Dispatchers.Main).launch {
                MusicWidget().updateAll(context)
            }

        }

        fun nextTrack(context: Context) {
            stopMusic()
            currentIndex = (currentIndex + 1) % trackList.size
            isPlaying = true
            currentPosition = 0
            saveState(context)
            playMusic(context)
            CoroutineScope(Dispatchers.Main).launch {
                MusicWidget().updateAll(context)
            }

        }

        fun prevTrack(context: Context) {
            stopMusic()
            currentIndex = if (currentIndex > 0) currentIndex - 1 else trackList.size - 1
            isPlaying = true
            currentPosition = 0
            saveState(context)
            playMusic(context)
            CoroutineScope(Dispatchers.Main).launch {
                MusicWidget().updateAll(context)
            }

        }


        private fun playMusic(context: Context) {
            stopMusic()
            val track = trackList[currentIndex]
            mediaPlayer = MediaPlayer.create(context, track.resId).apply {
                seekTo(currentPosition)
                start()
                setOnCompletionListener {
                    nextTrack(context)
                }
            }
        }

        private fun pauseMusic() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    currentPosition = it.currentPosition
                    it.pause()
                }
            }
        }

        private fun resumeMusic(context: Context) {
            if (mediaPlayer == null) {
                playMusic(context)
            } else {
                mediaPlayer?.seekTo(currentPosition)
                mediaPlayer?.start()
            }
        }

        private fun stopMusic() {
            mediaPlayer?.release()
            mediaPlayer = null
        }

        private fun updateWidget(context: Context) {
            saveState(context)
            val newState = getCurrentState(context)

            Log.d("MusicWidget", "Обновление виджета: ${newState.title}, isPlaying: ${newState.isPlaying}, albumArt: ${newState.albumArt}")
        }




        private fun saveState(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            prefs.putInt(KEY_INDEX, currentIndex)
            prefs.putBoolean(KEY_PLAYING, isPlaying)
            prefs.putInt(KEY_POSITION, currentPosition)
            prefs.apply()
        }

        fun loadState(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            currentIndex = prefs.getInt(KEY_INDEX, 0)
            isPlaying = prefs.getBoolean(KEY_PLAYING, false)
            currentPosition = prefs.getInt(KEY_POSITION, 0)
            if (currentIndex >= trackList.size) {
                currentIndex = 0
            }
        }
    }
}
