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
import androidx.navigation.NavController
import com.example.tubes.R
import com.example.tubes.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.tubes.Screen

data class SplashFrame(
    val image: Int,
    val duration: Long  // Berapa lama frame ini tampil sebelum fade ke next
)

@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit
) {
    // Semua frames animasi
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
            SplashFrame(R.drawable.splash9, 650),
            SplashFrame(R.drawable.splash10, 650),
            SplashFrame(R.drawable.splash11, 650),
            SplashFrame(R.drawable.splash12, 650),
            SplashFrame(R.drawable.splash13, 650),
            SplashFrame(R.drawable.splash14, 650),
            SplashFrame(R.drawable.splash15, 1000),
            SplashFrame(R.drawable.splash16, 800),
            SplashFrame(R.drawable.splash17, 800)
        )
    }

    var currentFrameIndex by remember { mutableStateOf(0) }
    val currentAlpha = remember { Animatable(1f) }
    val nextAlpha = remember { Animatable(0f) }

    val fadeDuration = 700

    // ===== ANIMASI SPLASH =====
    LaunchedEffect(Unit) {
        while (currentFrameIndex < frames.size - 1) {

            val frame = frames[currentFrameIndex]

            // reset alpha
            currentAlpha.snapTo(1f)
            nextAlpha.snapTo(0f)

            // tunggu durasi frame
            delay(frame.duration)

            // fade out current frame
            launch {
                currentAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = fadeDuration,
                        easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                    )
                )
            }

            // fade in next frame
            launch {
                nextAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = fadeDuration,
                        easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                    )
                )
            }

            delay(fadeDuration.toLong())

            currentFrameIndex++
        }

        // tunggu frame terakhir
        delay(frames.last().duration)

        // ➜ Kirim event ke Navigation (bukan ViewModel)
        onAnimationFinished()
    }


    // ===== UI LAYER =====
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2B3A67)),
        contentAlignment = Alignment.Center
    ) {

        // Current frame
        Image(
            painter = painterResource(id = frames[currentFrameIndex].image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = currentAlpha.value },
            contentScale = ContentScale.Crop
        )

        // Next frame untuk crossfade
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


