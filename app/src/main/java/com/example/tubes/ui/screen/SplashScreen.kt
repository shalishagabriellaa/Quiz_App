package com.example.tubes.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tubes.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SplashFrame(
    val image: Int,
    val duration: Long  // Berapa lama frame ini tampil sebelum fade ke next
)

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    // Daftar frame dengan durasi masing-masing
    val frames = remember {
        listOf(
            SplashFrame(R.drawable.splash1, 1200),
            SplashFrame(R.drawable.splash2, 1000),
            SplashFrame(R.drawable.splash3, 1000),
            SplashFrame(R.drawable.splash4, 1000),
            SplashFrame(R.drawable.splash5, 1000),
            SplashFrame(R.drawable.splash6, 1000),
            SplashFrame(R.drawable.splash7, 1000),
            SplashFrame(R.drawable.splash8, 800),
            SplashFrame(R.drawable.splash9, 650),  // Q - dipercepat
            SplashFrame(R.drawable.splash10, 650), // U - dipercepat
            SplashFrame(R.drawable.splash11, 650), // O - dipercepat
            SplashFrame(R.drawable.splash12, 650), // R - dipercepat
            SplashFrame(R.drawable.splash13, 650), // R - dipercepat
            SplashFrame(R.drawable.splash14, 650), // I - dipercepat
            SplashFrame(R.drawable.splash15, 1000),
            SplashFrame(R.drawable.splash16, 800),
            SplashFrame(R.drawable.splash17, 800)
        )
    }

    var currentFrameIndex by remember { mutableStateOf(0) }
    val currentAlpha = remember { Animatable(1f) }
    val nextAlpha = remember { Animatable(0f) }

    val fadeDuration = 700 // Durasi crossfade smooth

    LaunchedEffect(Unit) {
        while (currentFrameIndex < frames.size - 1) {
            val currentFrame = frames[currentFrameIndex]

            // Tampilkan frame current
            currentAlpha.snapTo(1f)
            nextAlpha.snapTo(0f)

            // Tunggu durasi frame
            delay(currentFrame.duration)

            // Mulai crossfade ke frame berikutnya
            launch {
                currentAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = fadeDuration,
                        easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                    )
                )
            }
            launch {
                nextAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = fadeDuration,
                        easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                    )
                )
            }

            // Tunggu crossfade selesai
            delay(fadeDuration.toLong())

            // Pindah ke frame berikutnya
            currentFrameIndex++
        }

        // Frame terakhir
        delay(frames.last().duration)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2B3A67)),
        contentAlignment = Alignment.Center
    ) {
        // Frame saat ini
        Image(
            painter = painterResource(id = frames[currentFrameIndex].image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = currentAlpha.value },
            contentScale = ContentScale.Crop
        )

        // Frame berikutnya (untuk crossfade)
        if (currentFrameIndex < frames.size - 1) {
            Image(
                painter = painterResource(id = frames[currentFrameIndex + 1].image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = nextAlpha.value },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewSplashScreen() {
    SplashScreen(onNavigateToLogin = {})
}
