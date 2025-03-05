package com.example.task3.widget

import android.content.Context
import android.net.Uri
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.Util

class MusicPlayer(private val context: Context) {
    private var player: ExoPlayer? = null

    fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(Util.USAGE_MEDIA) // Используем Util.USAGE_MEDIA
                        .setContentType(Util.CONTENT_TYPE_MUSIC) // Используем Util.CONTENT_TYPE_MUSIC
                        .build(),
                    true // handleAudioFocus
                )
            }
        }
    }

    fun play(resId: Int) {
        initializePlayer()

        val uri = Uri.parse("android.resource://${context.packageName}/$resId")
        val mediaItem = MediaItem.fromUri(uri)

        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun pause() {
        player?.pause()
    }

    fun release() {
        player?.release()
        player = null
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }
}
