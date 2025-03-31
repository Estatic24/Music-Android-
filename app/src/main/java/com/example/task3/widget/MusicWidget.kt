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
import androidx.glance.appwidget.action.actionRunCallback
import com.example.task3.R
import android.util.Log
import androidx.glance.action.clickable
import androidx.glance.layout.ContentScale
import com.example.task3.widget.MusicWidgetState.Companion.getResizedAlbumArt
import com.example.task3.widget.services.NextTrackCallback
import com.example.task3.widget.services.PlayPauseCallback
import com.example.task3.widget.services.PrevTrackCallback

class MusicWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val musicState = MusicWidgetState.getCurrentState(context)

            Log.d("MusicWidget", "Обновление виджета: ${musicState.title}, isPlaying: ${musicState.isPlaying}, albumArt: ${musicState.albumArt}")

            provideContent {
                Box(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    Image(
                        provider = ImageProvider(getResizedAlbumArt(context, musicState.albumArt)),
                        contentDescription = "Обложка",
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .height(350.dp)
                            .background(ColorProvider(day = Color(0x99000000), night = Color(0xBB000000))), // Затемнение
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = GlanceModifier.height(10.dp))

                        Text(
                            text = musicState.title.ifEmpty { "Загрузка..." },
                            style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White)),
                            maxLines = 1
                        )
                        Text(
                            text = musicState.artist,
                            style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.LightGray)),
                            maxLines = 1
                        )

                        Spacer(modifier = GlanceModifier.height(24.dp))

                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.previous),
                                contentDescription = "Предыдущий трек",
                                modifier = GlanceModifier
                                    .size(36.dp)
                                    .clickable(actionRunCallback<PrevTrackCallback>()),
                                colorFilter = ColorFilter.tint(androidx.glance.unit.ColorProvider(Color.White))
                            )

                            Spacer(modifier = GlanceModifier.width(16.dp))

                            Image(
                                provider = ImageProvider(if (musicState.isPlaying) R.drawable.pause else R.drawable.play),
                                contentDescription = "Воспроизведение",
                                modifier = GlanceModifier
                                    .size(42.dp)
                                    .clickable(actionRunCallback<PlayPauseCallback>()),
                                colorFilter = ColorFilter.tint(androidx.glance.unit.ColorProvider(Color.White))
                            )

                            Spacer(modifier = GlanceModifier.width(16.dp))

                            Image(
                                provider = ImageProvider(R.drawable.next),
                                contentDescription = "Следующий трек",
                                modifier = GlanceModifier
                                    .size(36.dp)
                                    .clickable(actionRunCallback<NextTrackCallback>()),
                                colorFilter = ColorFilter.tint(androidx.glance.unit.ColorProvider(Color.White))
                            )
                        }
                    }
                }
            }
            kotlinx.coroutines.delay(300)
            MusicWidget().updateAll(context)

        } catch (e: Exception) {
            Log.e("MusicWidget", "Ошибка в provideGlance: ${e.message}")
        }
    }

}