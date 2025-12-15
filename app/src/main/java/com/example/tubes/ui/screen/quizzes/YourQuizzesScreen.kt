package com.example.tubes.ui.screen.quizzes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.ui.screen.home.YourQuizRow
import com.example.tubes.viewmodel.YourQuizzesViewModel

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourQuizzesScreen(
    userId: String,
    onBackClick: () -> Unit,
    /**
     * IMPORTANT:
     * Ini harus diarahkan ke preview jawaban+pembahasan (answerExplanation/{quizId}),
     * bukan start quiz lagi.
     */
    onResultClick: (String) -> Unit,
    viewModel: YourQuizzesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadYourQuizzes(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Your Quizzes",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = DeepBlue
                    )
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = Color.Red
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadYourQuizzes(userId) }) {
                            Text("Retry")
                        }
                    }
                }

                uiState.quizzes.isEmpty() -> {
                    Text(
                        text = "You haven't played any quiz yet.",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(uiState.quizzes) { item ->
                            YourQuizRow(
                                q = item,
                                onClick = {
                                    // ✅ klik item -> preview jawaban+pembahasan
                                    onResultClick(item.quizId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
