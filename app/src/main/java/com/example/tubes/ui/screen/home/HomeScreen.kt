package com.example.tubes.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tubes.ui.screen.home.components.*

@Composable
fun HomeScreen(
    // data masuk dari VM / caller (default kosong)
    categories: List<String> = emptyList(),
    trending: List<QuizUi> = emptyList(),
    topPicks: List<QuizUi> = emptyList(),
    yourQuizzes: List<QuizUi> = emptyList(),
    userName: String = "Gresia",

    // bottom nav callbacks
    onNavHome: () -> Unit = {},
    onNavQuizzes: () -> Unit = {},
    onNavQR: () -> Unit = {},
    onNavLeaderboard: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF121142), Color(0xFFA9B8FF))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 92.dp) // ruang bottom nav
        ) {
            item { HomeTopBar(userName = userName) }
            item { Spacer(Modifier.height(16.dp)) }

            // Sections (hanya render kalau ada data)
            categorySection(items = categories)
            playWithFriendsBanner()
            trendingSection(items = trending)
            topAuthorsSection() // kalau kamu juga mau pakai data, tinggal ubah paramnya
            topPicksSection(items = topPicks)
            yourQuizzesSection(items = yourQuizzes)

            item { Spacer(Modifier.height(140.dp)) }
        }

        HomeBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHome = onNavHome,
            onQuizzes = onNavQuizzes,
            onQR = onNavQR,
            onLeaderboard = onNavLeaderboard,
            onProfile = onNavProfile
        )
    }
}
