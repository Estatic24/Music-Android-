package com.example.task3.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.color.ColorProvider
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import com.example.task3.R
import com.example.task3.widget.services.NextTrackService
import com.example.task3.widget.services.PlayPauseService
import com.example.task3.widget.services.PrevTrackService

class MusicWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val musicState = MusicWidgetState.getCurrentState(context)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(day = Color.White, night = Color(0xFF121212))) // Фон
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (musicState.title.isEmpty()) {
                    Text(
                        text = "Загрузка...",
                        style = TextStyle(color = ColorProvider(day = Color.Black, night = Color.White))
                    )
                } else {
                    Box(
                        modifier = GlanceModifier.cornerRadius(8.dp)
                    ) {
                        Image(
                            provider = ImageProvider(musicState.albumArt),
                            contentDescription = "Обложка",
                            modifier = GlanceModifier.size(100.dp)
                        )
                    }

                    Column(modifier = GlanceModifier.padding(8.dp)) {
                        Text(
                            text = musicState.title,
                            style = TextStyle(color = ColorProvider(day = Color.Black, night = Color.White)),
                            maxLines = 1,
                            modifier = GlanceModifier.wrapContentHeight()
                        )
                        Text(
                            text = musicState.artist,
                            style = TextStyle(color = ColorProvider(day = Color.Gray, night = Color.LightGray)),
                            maxLines = 1,
                            modifier = GlanceModifier.wrapContentHeight()
                        )
                    }

                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            text = "⏮",
                            onClick = actionStartService<PrevTrackService>()
                        )
                        Button(
                            text = if (musicState.isPlaying) "⏸" else "▶",
                            onClick = actionStartService<PlayPauseService>()
                        )
                        Button(
                            text = "⏭",
                            onClick = actionStartService<NextTrackService>()
                        )
                    }
                }
            }
        }
    }
}
