package com.example.tubes.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.ui.screen.home.models.QuizUi
import com.example.tubes.ui.screen.home.models.CategoryUi
import com.example.tubes.ui.screen.home.models.AuthorUi

/* ====== HomeSection (ISI SAJA) ====== */
@Composable
fun HomeSection(
    categories: List<CategoryUi>,
    trending: List<QuizUi>,
    topAuthors: List<AuthorUi>,
    topPicks: List<QuizUi>,
    yourQuizzes: List<QuizUi>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        /* ---------- Category ---------- */
        SectionHeader("Category")
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(categories) { item ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .background(Color(0x33FFFFFF))
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(item.name, color = Color.Black, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                    onClick = { /* TODO */ },
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
        SectionHeader("Trending Quiz")
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(trending) { QuizLargeCard(it) }
        }

        Spacer(Modifier.height(24.dp))

        /* ---------- Top Authors ---------- */
        SectionHeader("Top Authors")
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(topAuthors) { author ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .border(2.dp, Color.Black, CircleShape)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(author.name, color = Color.Black, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ---------- Top Picks ---------- */
        SectionHeader("Top Picks")
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(topPicks) { QuizLargeCard(it) }
        }

        Spacer(Modifier.height(24.dp))

        /* ---------- Your Quizzes ---------- */
        SectionHeader("Your Quizzes")
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            yourQuizzes.forEach { item -> YourQuizRow(item) }
        }
    }
}

/* ====== Sub-UI ====== */

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("See all", color = Color.Blue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QuizLargeCard(q: QuizUi) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .height(130.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFFB8B8FF))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF5D57C1))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("${q.questions} Qs", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
            Box(
                Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB2B8FF))
            )
            Spacer(Modifier.width(8.dp))
            Text(q.author, color = Color(0xFF6F7393), fontSize = 12.sp)
        }
    }
}

@Composable
private fun YourQuizRow(q: QuizUi) {
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
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFB8B8FF))
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(q.title, color = Color(0xFF1E1E1E), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("${q.questions} Questions", color = Color(0xFF7B7F9F), fontSize = 12.sp)
                }
                Spacer(Modifier.width(8.dp))
                Text("Result", color = Color(0xFF6D6ADB), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(3) {
                Box(
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB2B8FF))
                        .border(2.dp, Color.Black, CircleShape)
                )
                Spacer(Modifier.width((-8).dp))
            }
            Spacer(Modifier.width(10.dp))
            Text("+8127 People join", color = Color.Black, fontSize = 12.sp)
        }
    }
}

/* ====== Preview ====== */
@Preview(showBackground = true, backgroundColor = 0xFF121142, widthDp = 378, heightDp = 1330)
@Composable
fun HomeSectionPreview() {
    MaterialTheme {
        HomeSection(
            categories = listOf(CategoryUi("Technology"), CategoryUi("Music"), CategoryUi("Design")),
            trending = listOf(QuizUi("Machine Learning Practice Test", "Wan Guntar Alam", 5)),
            topAuthors = listOf(AuthorUi("Hannah"), AuthorUi("Nurul"), AuthorUi("Gaby")),
            topPicks = listOf(QuizUi("Neural Network Basics", "Guntur", 4)),
            yourQuizzes = listOf(QuizUi("Intro Kotlin", "Jet Brainly", 6))
        )
    }
}
