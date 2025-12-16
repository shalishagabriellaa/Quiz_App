package com.example.tubes.ui.screen.teacher

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.viewmodel.TeacherQuizQrViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

/* ========= COLORS (konsisten dengan screen teacher lain) ========= */
private val TopBarColor = androidx.compose.ui.graphics.Color(0xFF252A57)
private val PageBg = androidx.compose.ui.graphics.Color(0xFFF2F4FF)
private val CardBg = androidx.compose.ui.graphics.Color.White
private val MutedText = androidx.compose.ui.graphics.Color(0xFF7A7F9A)
private val PrimaryText = androidx.compose.ui.graphics.Color(0xFF14162B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherQuizQrScreen(
    quizId: String,
    viewModel: TeacherQuizQrViewModel,
    onBack: () -> Unit = {}
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

    Scaffold(
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarColor
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = androidx.compose.ui.graphics.Color.White
                        )
                    }
                },
                title = {
                    Text(
                        text = "Quiz QR Code",
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Scan this QR to join the quiz",
                fontSize = 14.sp,
                color = MutedText,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    qrBitmap?.let {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(androidx.compose.ui.graphics.Color(0xFFF6F7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(200.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Quiz Code",
                        fontSize = 12.sp,
                        color = MutedText
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = state.quizCode.ifEmpty { "-" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        letterSpacing = 1.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "QR code expires in 5 minutes",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

/* ========= QR GENERATOR (TETAP SAMA, JANGAN DIUBAH) ========= */

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
