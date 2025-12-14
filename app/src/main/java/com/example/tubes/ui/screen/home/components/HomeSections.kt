package com.example.tubes.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tubes.ui.screen.home.models.AuthorUi
import com.example.tubes.ui.screen.home.models.CategoryUi
import com.example.tubes.ui.screen.home.models.QuizUi
import com.example.tubes.ui.screen.home.models.YourQuizUi

@Composable
fun HomeSection(
    categories: List<CategoryUi>,
    trending: List<QuizUi>,
    topAuthors: List<AuthorUi>,
    topPicks: List<QuizUi>,
    yourQuizzes: List<YourQuizUi>,
    modifier: Modifier = Modifier,
    onCategorySeeAll: () -> Unit = {},
    onCategoryClick: (CategoryUi) -> Unit = {},
    onTrendingSeeAll: () -> Unit = {},
    onTrendingClick: (String) -> Unit = {},
    onTopAuthorsSeeAll: () -> Unit = {},          // 🔹 BARU
    onYourQuizSeeAll: () -> Unit = {},            // 🔹 BARU
    onYourQuizClick: (String) -> Unit = {}        // 🔹 klik item your quiz
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        /* ---------- Category ---------- */
        SectionHeader(
            title = "Category",
            onSeeAll = onCategorySeeAll
        )
        Spacer(Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(categories) { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(64.dp)
                ) {
                    AsyncImage(
                        model = item.bannerUrl,
                        contentDescription = "Category: ${item.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .background(Color(0x33FFFFFF))
                            .clickable { onCategoryClick(item) }
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = item.name,
                        color = Color.Black,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        /* ---------- Banner ---------- */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF27459F))
                .padding(18.dp)
        ) {
            Column {
                Text(
                    "Play quiz together with\nyour friends now!",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 22.sp
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { /* TODO: invite friends */ },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) { Text("Find Friends", color = Color(0xFF27459F)) }
            }

            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy((-8).dp)
            ) {
                repeat(5) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0x55FFFFFF))
                            .border(2.dp, Color(0x33FFFFFF), CircleShape)
                    )
                }
            }
        }

        Spacer(Modifier.height(26.dp))

        /* ---------- Trending Quiz ---------- */
        SectionHeader(
            title = "Trending Quiz",
            onSeeAll = onTrendingSeeAll
        )
        Spacer(Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(trending) { quiz ->
                QuizLargeCard(
                    q = quiz,
                    onClick = { onTrendingClick(quiz.id) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ---------- Top Authors ---------- */
        SectionHeader(
            title = "Top Authors",
            onSeeAll = onTopAuthorsSeeAll       // 🔹 SEKARANG ADA SEE ALL
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(topAuthors) { author ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(60.dp)
                ) {
                    AsyncImage(
                        model = author.avatarUrl,
                        contentDescription = "Avatar for ${author.fullName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .background(Color(0x33FFFFFF))
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        author.fullName,
                        color = Color.Black,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ---------- Your Quizzes ---------- */
        SectionHeader(
            title = "Your Quizzes",
            onSeeAll = if (yourQuizzes.isNotEmpty()) onYourQuizSeeAll else null
        )
        Spacer(Modifier.height(12.dp))

        val displayedQuizzes = yourQuizzes.take(3)

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            displayedQuizzes.forEach { item ->
                YourQuizRow(
                    q = item,
                    onClick = { onYourQuizClick(item.quizId) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAll: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        if (onSeeAll != null) {
            Text(
                "See all",
                color = Color(0xFF212252),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSeeAll() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun QuizLargeCard(
    q: QuizUi,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {

        Box(
            modifier = Modifier
                .height(130.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {

            if (q.bannerUrl.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFB8B8FF))
                )
            } else {
                AsyncImage(
                    model = q.bannerUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF5D57C1))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "${q.questionsCount} Qs",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            q.title,
            color = Color(0xFF1E1E1E),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp),
            maxLines = 2
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!q.authorAvatarUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = q.authorAvatarUrl,
                    contentDescription = "Avatar for ${q.authorName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB2B8FF))
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(q.authorName, color = Color(0xFF6F7393), fontSize = 12.sp)
        }
    }
}

@Composable
fun YourQuizRow(
    q: YourQuizUi,
    onClick: () -> Unit
) {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF565C92))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable(onClick = onClick)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFB8B8FF))
                ) {
                    if (!q.bannerUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = q.bannerUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        q.title,
                        color = Color(0xFF1E1E1E),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${q.questionsCount} Questions",
                        color = Color(0xFF7B7F9F),
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${q.correctAnswers}/${q.totalQuestions}",
                        color = Color(0xFF6D6ADB),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Score: ${q.lastScore}",
                        color = Color(0xFF6D6ADB),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
