package com.example.tubes.ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data classes
data class Quiz(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val numberOfQuestions: Int,
    val author: Author,
    val weeksAgo: Int,
    val plays: String,
    val backgroundColor: Color = Color.White
)

data class Author(
    val name: String,
    val avatarUrl: String
)

// Dummy data
object QuizDummyData {
    val quizzes = listOf(
        Quiz(
            id = 1,
            title = "Can Machine Really Think?",
            imageUrl = "",
            numberOfQuestions = 10,
            author = Author("Gilbert Ong", ""),
            weeksAgo = 2,
            plays = "2.0k plays",
            backgroundColor = Color(0xFF4A90E2)
        ),
        Quiz(
            id = 2,
            title = "Machine Learning Practice",
            imageUrl = "",
            numberOfQuestions = 3,
            author = Author("Wan Guntur Alam", ""),
            weeksAgo = 4,
            plays = "8k plays",
            backgroundColor = Color(0xFFE94B8B)
        ),
        Quiz(
            id = 3,
            title = "How Secure Are You Online?",
            imageUrl = "",
            numberOfQuestions = 15,
            author = Author("Gresia Angelina", ""),
            weeksAgo = 5,
            plays = "1.7k plays",
            backgroundColor = Color(0xFF5B7FE8)
        ),
        Quiz(
            id = 4,
            title = "Coding for Everyone: Programming Basics",
            imageUrl = "",
            numberOfQuestions = 20,
            author = Author("Putri Jelita", ""),
            weeksAgo = 12,
            plays = "100k plays",
            backgroundColor = Color(0xFF4ECDC4)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySpecifyScreen(
    categoryName: String = "Technology",
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onQuizClick: (Quiz) -> Unit = {},
    onSortClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2C3E7C)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5FF))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner
            item {
                CategoryBanner()
            }

            // Quiz count and sort
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${QuizDummyData.quizzes.size} Quiz",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    TextButton(onClick = onSortClick) {
                        Text(
                            text = "Default",
                            color = Color(0xFF4A90E2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.UnfoldMore,
                            contentDescription = "Sort",
                            tint = Color(0xFF6C63FF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quiz list
            items(QuizDummyData.quizzes) { quiz ->
                QuizCard(
                    quiz = quiz,
                    onClick = { onQuizClick(quiz) }
                )
            }
        }
    }
}

@Composable
fun CategoryBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E),
                        Color(0xFF283593)
                    )
                )
            )
    ) {
        // Placeholder untuk network pattern image
        // Nanti bisa diganti dengan AsyncImage dari Coil
    }
}

@Composable
fun QuizCard(
    quiz: Quiz,
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
            // Quiz thumbnail
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(quiz.backgroundColor)
            ) {
                // Placeholder untuk quiz image
                // Nanti pakai AsyncImage untuk load dari URL

                // Badge jumlah soal
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color(0xFF2C3E7C), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${quiz.numberOfQuestions} Qs",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quiz info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title
                Text(
                    text = quiz.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Time and plays
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${quiz.weeksAgo} weeks ago",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Text(
                        text = quiz.plays,
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Author
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                    ) {
                        // Nanti pakai AsyncImage untuk avatar
                    }

                    Text(
                        text = quiz.author.name,
                        fontSize = 13.sp,
                        color = Color(0xFF666666),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CategorySpecifyScreenPreview() {
    MaterialTheme {
        CategorySpecifyScreen()
    }
}

// ViewModel untuk manage data dari database
// class QuizViewModel : ViewModel() {
//     private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
//     val quizzes: StateFlow<List<Quiz>> = _quizzes.asStateFlow()
//
//     private val _isLoading = MutableStateFlow(false)
//     val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//     fun loadQuizzesByCategory(categoryId: Int) {
//         viewModelScope.launch {
//             _isLoading.value = true
//             try {
//                 val data = repository.getQuizzesByCategory(categoryId)
//                 _quizzes.value = data
//             } catch (e: Exception) {
//                 // Handle error
//             } finally {
//                 _isLoading.value = false
//             }
//         }
//     }
//
//     fun sortQuizzes(sortType: SortType) {
//         _quizzes.value = when(sortType) {
//             SortType.DEFAULT -> _quizzes.value
//             SortType.MOST_PLAYED -> _quizzes.value.sortedByDescending { it.plays }
//             SortType.NEWEST -> _quizzes.value.sortedByDescending { it.weeksAgo }
//         }
//     }
// }

// Repository interface
// interface QuizRepository {
//     suspend fun getQuizzesByCategory(categoryId: Int): List<Quiz>
//     suspend fun getQuizById(id: Int): Quiz?
//     suspend fun searchQuizzes(query: String): List<Quiz>
// }

// Room Database entities
// @Entity(tableName = "quizzes")
// data class QuizEntity(
//     @PrimaryKey val id: Int,
//     val title: String,
//     val imageUrl: String,
//     val numberOfQuestions: Int,
//     val categoryId: Int,
//     val authorId: Int,
//     val createdDate: Long,
//     val totalPlays: Int
// )
//
// @Entity(tableName = "authors")
// data class AuthorEntity(
//     @PrimaryKey val id: Int,
//     val name: String,
//     val avatarUrl: String
// )
//
// @Dao
// interface QuizDao {
//     @Query("SELECT * FROM quizzes WHERE categoryId = :categoryId")
//     suspend fun getQuizzesByCategory(categoryId: Int): List<QuizEntity>
//
//     @Query("SELECT * FROM quizzes WHERE id = :id")
//     suspend fun getQuizById(id: Int): QuizEntity?
//
//     @Query("SELECT * FROM quizzes WHERE title LIKE '%' || :query || '%'")
//     suspend fun searchQuizzes(query: String): List<QuizEntity>
// }

// enum class SortType {
//     DEFAULT,
//     MOST_PLAYED,
//     NEWEST
// }