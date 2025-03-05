package com.example.task3.ui

import android.content.Context
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.task3.R

@Composable
fun PlayerScreen(navController: NavController, trackIndex: Int) {
    val context = LocalContext.current

    val trackList = listOf(
        R.raw.track1 to "Track 1",
        R.raw.track2 to "Track 2",
        R.raw.track3 to "Track 3",
        R.raw.track4 to "Track 4"
    )

    var currentTrackIndex by remember { mutableStateOf(trackIndex) }
    var trackName by remember { mutableStateOf(trackList[currentTrackIndex].second) }
    val exoPlayer = remember { createExoPlayer(context, trackList[currentTrackIndex].first) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        exoPlayer.playWhenReady = isPlaying
        onDispose { exoPlayer.release() }
    }

    val fadeIn = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fadeIn.animateTo(1f, animationSpec = tween(1000))
    }

    val infiniteTransition = rememberInfiniteTransition()
    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = 80f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize().alpha(fadeIn.value)
    ) {
        Image(
            painter = painterResource(R.drawable.background_music),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.5f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 300f
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Now Playing: $trackName",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    color = if (isPlaying) Color.Cyan else Color.Blue,
                    radius = animatedRadius,
                    style = Stroke(width = 8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                AnimatedIconButton(R.drawable.previous, "Previous") {
                    currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else trackList.lastIndex
                    trackName = trackList[currentTrackIndex].second
                    exoPlayer.setMediaItem(MediaItem.fromUri(getTrackUri(context, trackList[currentTrackIndex].first)))
                    exoPlayer.prepare()
                    exoPlayer.play()
                    isPlaying = true
                }

                Spacer(modifier = Modifier.width(24.dp))

                AnimatedIconButton(if (isPlaying) R.drawable.pause else R.drawable.play, "Play/Pause") {
                    isPlaying = !isPlaying
                    if (isPlaying) exoPlayer.play() else exoPlayer.pause()
                }

                Spacer(modifier = Modifier.width(24.dp))

                AnimatedIconButton(R.drawable.next, "Next") {
                    currentTrackIndex = (currentTrackIndex + 1) % trackList.size
                    trackName = trackList[currentTrackIndex].second
                    exoPlayer.setMediaItem(MediaItem.fromUri(getTrackUri(context, trackList[currentTrackIndex].first)))
                    exoPlayer.prepare()
                    exoPlayer.play()
                    isPlaying = true
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedIconButton(R.drawable.back, "Back") {
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun AnimatedIconButton(iconRes: Int, contentDescription: String, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.85f else 1f, animationSpec = tween(150))

    IconButton(
        onClick = {
            isPressed = true
            onClick()
            isPressed = false
        },
        modifier = Modifier.size(56.dp).scale(scale)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
}

fun createExoPlayer(context: Context, trackRes: Int): ExoPlayer {
    val exoPlayer = ExoPlayer.Builder(context).build()
    val uri = getTrackUri(context, trackRes)
    val mediaItem = MediaItem.fromUri(uri)
    exoPlayer.setMediaItem(mediaItem)
    exoPlayer.prepare()
    return exoPlayer
}

fun getTrackUri(context: Context, trackRes: Int): Uri {
    return Uri.parse("android.resource://${context.packageName}/$trackRes")
}

@Preview
@Composable
fun PreviewPlayerScreen() {
    val navController = NavController(LocalContext.current)
    PlayerScreen(navController, 0)
}
