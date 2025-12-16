package com.example.tubes.ui.screen.qr

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanScreen(
    onBack: () -> Unit,
    onFoundQuiz: (quizId: String) -> Unit
) {
    val context = LocalContext.current
    val repo = remember { HomeRepositoryImpl() }
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var error by remember { mutableStateOf<String?>(null) }
    var isHandlingResult by remember { mutableStateOf(false) }

    // request permission on enter
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

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
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant Permission")
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "If it keeps failing, open Settings > Apps > Quorri > Permissions.",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                return@Box
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    decodeContinuous(object : BarcodeCallback {
                                        override fun barcodeResult(result: BarcodeResult?) {
                                            val raw = result?.text?.trim().orEmpty()
                                            if (raw.isEmpty()) return
                                            if (isHandlingResult) return

                                            isHandlingResult = true
                                            error = null

                                            val code = raw.substringAfter(":", raw).trim()
                                            scope.launch {
                                                try {
                                                    val quiz = repo.getQuizByCode(code)
                                                    if (quiz == null) {
                                                        error = "Quiz not found for code: $code"
                                                        isHandlingResult = false
                                                    } else {
                                                        pause()
                                                        onFoundQuiz(quiz.id)
                                                    }
                                                } catch (e: Exception) {
                                                    error = e.message ?: "Failed to search quiz"
                                                    isHandlingResult = false
                                                }
                                            }
                                        }
                                    })
                                    resume()
                                }
                            },
                            update = { view -> view.resume() }
                        )
                    }

                    if (error != null) {
                        Spacer(Modifier.height(12.dp))
                        Text(text = error!!, color = Color(0xFFD32F2F))
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            error = null
                            isHandlingResult = false
                        }) { Text("Try again") }
                    }
                }
            }
        }
    }
}
