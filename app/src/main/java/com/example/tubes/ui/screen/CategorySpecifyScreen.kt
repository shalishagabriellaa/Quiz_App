package com.example.tubes.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tubes.data.repository.HomeRepositoryImpl
import com.example.tubes.data.model.Quiz
import com.example.tubes.ui.screen.home.models.QuizUi
import com.example.tubes.ui.screen.home.models.toUi
import kotlinx.coroutines.launch
import com.example.tubes.util.formatRelativeTime

@Composable
fun CategorySpecifyScreen(
    categoryId: String,
    categoryName: String,
    onBackClick: () -> Unit = {},
    onQuizClick: (String) -> Unit = {}        // kirim quizId saat klik card
) {
    val repo = remember { HomeRepositoryImpl() }

    var uiState by remember { mutableStateOf(CategorySpecifyUiState()) }
    val scope = rememberCoroutineScope()

    // Load data sekali tiap categoryId berubah
    LaunchedEffect(categoryId) {
        scope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            try {
                // 1. Ambil semua quiz untuk categoryId ini
                val quizzes: List<Quiz> = repo.getQuizzesByCategory(categoryId)

                // 2. Ambil nama author untuk setiap quiz (cache biar ga bolak-balik Firestore)
                val authorCache = mutableMapOf<String, String>()
                quizzes.forEach { quiz ->
                    val authorId = quiz.authorId
                    if (authorId.isNotEmpty() && !authorCache.containsKey(authorId)) {
                        val user = repo.getUser(authorId)
                        authorCache[authorId] = user?.fullName ?: user?.name ?: "Unknown"
                    }
                }

                // 3. Mapping ke QuizUi
                val quizUiList: List<QuizUi> = quizzes.map { quiz ->
                    val authorName = authorCache[quiz.authorId] ?: "Unknown"
                    quiz.toUi(authorName = authorName)
                }

                // 4. Ambil category untuk banner (pakai daftar categories yang sudah ada di Firestore)
                val allCategories = repo.getCategories()
                val thisCategory = allCategories.firstOrNull { it.id == categoryId }

                uiState = CategorySpecifyUiState(
                    isLoading = false,
                    quizzes = quizUiList,
                    error = null,
                    categoryBannerUrl = thisCategory?.bannerUrl
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    Scaffold(
        topBar = {
            // ❗ ini pakai CategoryTopBar dari CategoryScreen.kt (harus non-private)
            CategoryTopBar(
                title = categoryName,
                onBackClick = onBackClick,
                onSearchToggle = { /* kalau mau search di sini nanti */ }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5FF))
        ) {

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.quizzes.isEmpty() -> {
                    Text(
                        text = "No quiz yet for this category",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Banner kategori dari DB
                        item {
                            CategorySpecifyBanner(
                                bannerUrl = uiState.categoryBannerUrl
                            )
                        }

                        // 🔹 TOTAL QUIZ DI KATEGORI INI
                        item {
                            Text(
                                text = "${uiState.quizzes.size} Quiz",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A),
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                        }

                        // List quiz
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

/* =========================
 *   UI STATE
 * ========================= */

data class CategorySpecifyUiState(
    val isLoading: Boolean = true,
    val quizzes: List<QuizUi> = emptyList(),
    val error: String? = null,
    val categoryBannerUrl: String? = null
)

/* =========================
 *   SUB-COMPONENTS
 * ========================= */

@Composable
private fun CategorySpecifyBanner(
    bannerUrl: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        if (!bannerUrl.isNullOrEmpty()) {
            AsyncImage(
                model = bannerUrl,
                contentDescription = "Category Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // fallback gradient kalau belum ada banner di DB
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A237E),
                                Color(0xFF283593)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun QuizCard(
    quiz: QuizUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Thumbnail quiz
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF4A90E2))
            ) {
                if (!quiz.bannerUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = quiz.bannerUrl,
                        contentDescription = quiz.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Badge jumlah soal
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2C3E7C))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${quiz.questionsCount} Qs",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Info quiz
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = quiz.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = formatRelativeTime(quiz.createdAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                    )
                    Text(
                        text = quiz.authorName,
                        fontSize = 13.sp,
                        color = Color(0xFF666666),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
