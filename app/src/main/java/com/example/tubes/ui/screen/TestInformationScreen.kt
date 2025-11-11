package com.example.tubes.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data classes
data class QuizDetail(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val participantCount: String,
    val author: QuizAuthor,
    val weeksAgo: Int,
    val totalQuestions: Int,
    val questionType: String,
    val duration: String,
    val rules: List<String>
)

data class QuizAuthor(
    val name: String,
    val avatarUrl: String
)

// Dummy data
object QuizDetailDummyData {
    val quizDetail = QuizDetail(
        id = 1,
        title = "Machine Learning Practice Test",
        imageUrl = "",
        participantCount = "8k people took this",
        author = QuizAuthor(
            name = "Wan Guntur Alam",
            avatarUrl = ""
        ),
        weeksAgo = 4,
        totalQuestions = 3,
        questionType = "Multiple Choice Question",
        duration = "5 mins",
        rules = listOf(
            "You must complete this test in one session – make sure your internet is reliable.",
            "1 mark awarded for a correct answer. No negative marking will be there for wrong answer.",
            "More you give the correct answer more chance to win the badge.",
            "If you don't earn a badge this time, you can retake this test once more."
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestInformationScreen(
    quizDetail: QuizDetail = QuizDetailDummyData.quizDetail,
    onBackClick: () -> Unit = {},
    onStartQuiz: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = quizDetail.title,
                        color = Color(0xFF1A1A1A),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEDE7F6)
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onStartQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E4FA0)
                )
            ) {
                Text(
                    text = "Start Quiz",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDE7F6))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quiz Image with participant count
            QuizImageCard(
                imageUrl = quizDetail.imageUrl,
                participantCount = quizDetail.participantCount
            )

            // Author info
            AuthorInfoSection(
                author = quizDetail.author,
                weeksAgo = quizDetail.weeksAgo
            )

            // Quiz stats
            QuizStatsSection(
                totalQuestions = quizDetail.totalQuestions,
                questionType = quizDetail.questionType,
                duration = quizDetail.duration
            )

            // Rules section
            RulesSection(rules = quizDetail.rules)

            // Good luck message
            Text(
                text = "ALL THE BEST!!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun QuizImageCard(
    imageUrl: String,
    participantCount: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF9C7FB5))
    ) {
        // Placeholder untuk quiz image
        // Nanti pakai AsyncImage dari Coil untuk load dari URL

        // Participant count overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = participantCount,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AuthorInfoSection(
    author: QuizAuthor,
    weeksAgo: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            ) {
                // Nanti pakai AsyncImage untuk avatar
            }

            Text(
                text = author.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
        }

        Text(
            text = "$weeksAgo weeks ago",
            fontSize = 13.sp,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
fun QuizStatsSection(
    totalQuestions: Int,
    questionType: String,
    duration: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Questions info
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Questions",
                tint = Color(0xFF5E4FA0),
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = totalQuestions.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = questionType,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }

        // Duration info
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Duration",
                tint = Color(0xFF5E4FA0),
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = duration,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "All Questions",
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}

@Composable
fun RulesSection(rules: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Before you start",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            rules.forEach { rule ->
                RuleItem(rule = rule)
            }
        }
    }
}

@Composable
fun RuleItem(rule: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "•",
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = rule,
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TestInformationScreenPreview() {
    MaterialTheme {
        TestInformationScreen()
    }
}

// ViewModel untuk manage quiz detail
// class QuizDetailViewModel(private val quizId: Int) : ViewModel() {
//     private val _quizDetail = MutableStateFlow<QuizDetail?>(null)
//     val quizDetail: StateFlow<QuizDetail?> = _quizDetail.asStateFlow()
//
//     private val _isLoading = MutableStateFlow(false)
//     val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//     init {
//         loadQuizDetail()
//     }
//
//     private fun loadQuizDetail() {
//         viewModelScope.launch {
//             _isLoading.value = true
//             try {
//                 val detail = repository.getQuizDetail(quizId)
//                 _quizDetail.value = detail
//             } catch (e: Exception) {
//                 // Handle error
//             } finally {
//                 _isLoading.value = false
//             }
//         }
//     }
// }

// Repository interface
// interface QuizDetailRepository {
//     suspend fun getQuizDetail(quizId: Int): QuizDetail
//     suspend fun startQuiz(quizId: Int): QuizSession
// }

// Room Database entities
// @Entity(tableName = "quiz_details")
// data class QuizDetailEntity(
//     @PrimaryKey val id: Int,
//     val title: String,
//     val imageUrl: String,
//     val totalParticipants: Int,
//     val authorId: Int,
//     val createdDate: Long,
//     val totalQuestions: Int,
//     val questionType: String,
//     val durationMinutes: Int
// )
//
// @Entity(tableName = "quiz_rules")
// data class QuizRuleEntity(
//     @PrimaryKey(autoGenerate = true) val id: Int = 0,
//     val quizId: Int,
//     val ruleText: String,
//     val orderIndex: Int
// )
//
// @Dao
// interface QuizDetailDao {
//     @Query("SELECT * FROM quiz_details WHERE id = :quizId")
//     suspend fun getQuizDetail(quizId: Int): QuizDetailEntity?
//
//     @Query("SELECT * FROM quiz_rules WHERE quizId = :quizId ORDER BY orderIndex")
//     suspend fun getQuizRules(quizId: Int): List<QuizRuleEntity>
// }

// Data class untuk quiz session
// data class QuizSession(
//     val sessionId: String,
//     val quizId: Int,
//     val userId: String,
//     val startTime: Long,
//     val questions: List<Question>
// )

// Navigation
// fun NavGraphBuilder.quizDetailScreen(
//     onBackClick: () -> Unit,
//     onStartQuiz: (quizId: Int) -> Unit
// ) {
//     composable(
//         route = "quiz_detail/{quizId}",
//         arguments = listOf(navArgument("quizId") { type = NavType.IntType })
//     ) { backStackEntry ->
//         val quizId = backStackEntry.arguments?.getInt("quizId") ?: return@composable
//         val viewModel: QuizDetailViewModel = viewModel(
//             factory = QuizDetailViewModelFactory(quizId)
//         )
//         val quizDetail by viewModel.quizDetail.collectAsState()
//
//         quizDetail?.let {
//             QuizDetailScreen(
//                 quizDetail = it,
//                 onBackClick = onBackClick,
//                 onStartQuiz = { onStartQuiz(quizId) }
//             )
//         }
//     }
// }