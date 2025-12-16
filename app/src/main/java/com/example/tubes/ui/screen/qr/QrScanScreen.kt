package com.example.tubes.ui.screen.qr

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.tubes.data.repository.HomeRepositoryImpl
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanScreen(
    onBack: () -> Unit,
    onFoundQuiz: (quizId: String) -> Unit, // ✅ kalau ketemu quiz, arahkan ke TestInformation / Quiz
) {
    val context = LocalContext.current
    val repo = remember { HomeRepositoryImpl() }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // simple permission request (tanpa ActivityResult API biar cepat copas)
    // kalau belum permission, kamu bisa arahkan user ke settings atau pakai rememberLauncherForActivityResult.
    // (aku tetap kasih tombol "Grant Permission" yg informatif)
    var error by remember { mutableStateOf<String?>(null) }
    var isHandlingResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF162471))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0E1C6B))
                .padding(padding)
        ) {
            if (!hasCameraPermission) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Camera permission is required to scan QR.", color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Grant permission in Settings > Apps > Quorri > Permissions.",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        // re-check (user mungkin habis balik dari settings)
                        hasCameraPermission =
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    }) {
                        Text("I already granted it")
                    }
                }
                return@Box
            }

            // Kamera preview + scan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Point your camera at the QR code",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                    ) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                DecoratedBarcodeView(ctx).apply {
                                    // scan continuous
                                    decodeContinuous(object : BarcodeCallback {
                                        override fun barcodeResult(result: BarcodeResult?) {
                                            val text = result?.text?.trim().orEmpty()
                                            if (text.isEmpty()) return
                                            if (isHandlingResult) return

                                            isHandlingResult = true

                                            // ✅ cari quiz by quizCode
                                            // kalau QR kamu encode quizCode angka seperti "383837", ini langsung cocok
                                            // kalau QR encode format lain, kamu bisa parse di sini.
                                            // contoh: "QUIZ:383837" -> ambil setelah ":"
                                            val code = if (text.contains(":")) text.substringAfter(":").trim() else text

                                            // handle with coroutine
                                            kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                val quiz = repo.getQuizByCode(code)
                                                if (quiz == null) {
                                                    error = "Quiz not found for code: $code"
                                                    isHandlingResult = false
                                                } else {
                                                    // stop scanning supaya ga double trigger
                                                    pause()
                                                    onFoundQuiz(quiz.id)
                                                }
                                            }
                                        }

                                        override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
                                    })
                                    resume()
                                }
                            },
                            update = { view ->
                                // keep resumed
                                view.resume()
                            }
                        )
                    }

                    if (error != null) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = error!!,
                            color = Color(0xFFD32F2F)
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            error = null
                            isHandlingResult = false
                        }) {
                            Text("Try again")
                        }
                    }
                }
            }
        }
    }
}
