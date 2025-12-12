package com.example.tubes.ui.screen.quizzes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

// Required imports for viewModelScope
import androidx.lifecycle.viewModelScope
import com.example.tubes.ui.screen.home.models.YourQuizUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizzesUiState(
    val isLoading: Boolean = false,
    val quizzes: List<QuizItem> = emptyList(),
    val error: String? = null
)

// Colors
private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

data class QuizItem(
    val id: String,
    val title: String,
    val questionCount: Int,
    val thumbnailUrl: String,
    val participantCount: Int,
    val participantAvatars: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourQuizzesScreen(
    onBackClick: () -> Unit,
    onQuizClick: (String) -> Unit,
    viewModel: YourQuizzesViewModel = viewModel(),
    quizzes: List<YourQuizUi>
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadQuizzes()
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
                actions = {
                    IconButton(onClick = { /* Search action */ }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlue
                )
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
                        Button(onClick = { viewModel.loadQuizzes() }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.quizzes.isEmpty() -> {
                    Text(
                        text = "No quizzes available",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.quizzes) { quiz ->
                            QuizCard(
                                quiz = quiz,
                                onClick = { onQuizClick(quiz.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizCard(
    quiz: QuizItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Content row with thumbnail and info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quiz thumbnail
                AsyncImage(
                    model = quiz.thumbnailUrl,
                    contentDescription = "Quiz thumbnail",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                // Quiz info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = quiz.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${quiz.questionCount} Questions",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }

                // Result button
                TextButton(
                    onClick = onClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF5B7FFF)
                    )
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_view),
                        contentDescription = "View result",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Result", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Participants row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar stack
                Row {
                    quiz.participantAvatars.take(3).forEachIndexed { index, avatarUrl ->
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Participant avatar",
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = (-8 * index).dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "+${quiz.participantCount} People join",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ViewModel
class YourQuizzesViewModel : androidx.lifecycle.ViewModel() {
    private val _uiState = MutableStateFlow(QuizzesUiState())
    val uiState: StateFlow<QuizzesUiState> = _uiState.asStateFlow()

    fun loadQuizzes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // TODO: Replace with actual API call
                // val quizzes = repository.getYourQuizzes()

                // Mock data for demonstration
                delay(1000)
                val mockQuizzes = listOf(
                    QuizItem(
                        id = "1",
                        title = "Machine Learning Basics",
                        questionCount = 3,
                        thumbnailUrl = "https://via.placeholder.com/150",
                        participantCount = 8127,
                        participantAvatars = listOf(
                            "https://via.placeholder.com/50",
                            "https://via.placeholder.com/50",
                            "https://via.placeholder.com/50"
                        )
                    ),
                    QuizItem(
                        id = "2",
                        title = "Data Science Fundamentals",
                        questionCount = 5,
                        thumbnailUrl = "https://via.placeholder.com/150",
                        participantCount = 5432,
                        participantAvatars = listOf(
                            "https://via.placeholder.com/50",
                            "https://via.placeholder.com/50"
                        )
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    quizzes = mockQuizzes
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}
