package com.example.tubes.ui.screen.teacher

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.tubes.viewmodel.TeacherQuizQrViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter


@Composable
fun TeacherQuizQrScreen(
    quizId: String,
    viewModel: TeacherQuizQrViewModel
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.generateQr(quizId)
    }

    val qrBitmap = remember(state.quizCode) {
        if (state.quizCode.isNotEmpty())
            generateQrBitmap("quizCode:${state.quizCode}")
        else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("QR Quiz", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(24.dp))

        qrBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(240.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text("Code: ${state.quizCode}")
        Text("Expired 5 menit")
    }
}

fun generateQrBitmap(text: String, size: Int = 512): Bitmap {
    val matrix = MultiFormatWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        size,
        size
    )

    return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
        for (x in 0 until size) {
            for (y in 0 until size) {
                setPixel(
                    x, y,
                    if (matrix[x, y]) Color.BLACK else Color.WHITE
                )
            }
        }
    }
}

