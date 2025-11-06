package com.example.tubes.ui.screen.home.shapes

import androidx.compose.foundation.shape.GenericShape

val WaveShape = GenericShape { size, _ ->
    // wave di bagian atas
    moveTo(0f, 80f)
    cubicTo(
        size.width * 0.25f, 0f,
        size.width * 0.75f, 160f,
        size.width, 80f
    )
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}
