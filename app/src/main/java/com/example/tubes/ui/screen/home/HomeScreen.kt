package com.example.tubes.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tubes.R
import com.example.tubes.ui.screen.home.components.HomeBottomNav
import com.example.tubes.ui.screen.home.components.HomeTopBar
import com.example.tubes.ui.screen.home.models.AuthorUi
import com.example.tubes.ui.screen.home.models.CategoryUi
import com.example.tubes.ui.screen.home.models.QuizUi

@Composable
fun HomeScreen(
    categories: List<String> = emptyList(),
    trending: List<QuizUi> = emptyList(),
    topPicks: List<QuizUi> = emptyList(),
    yourQuizzes: List<QuizUi> = emptyList(),
    userName: String = "Gresia",
    onHome: () -> Unit = {},
    onQuizzes: () -> Unit = {},
    onQR: () -> Unit = {},
    onLeaderboard: () -> Unit = {},
    onProfile: () -> Unit = {}
) {
    Box(Modifier.fillMaxSize()) {

        // ===== Background image full-bleed =====
        Image(
            painter = painterResource(id = R.drawable.section_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // ===== SATU-SATUNYA scrollable =====
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp) // ruang supaya tidak ketutup bottom nav
        ) {
            item { HomeTopBar(userName = userName) }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                HomeSection(
                    categories = categories.map { CategoryUi(it) },   // ← convert List<String> → List<CategoryUi>
                    trending = trending,
                    topAuthors = listOf(                         // ← tambah author placeholder dulu
                        AuthorUi("Hannah"),
                        AuthorUi("Brian"),
                        AuthorUi("Nurul"),
                        AuthorUi("Gaby"),
                        AuthorUi("Gaby"),
                        AuthorUi("Gaby"),
                        AuthorUi("Hana")
                    ),
                    topPicks = topPicks,
                    yourQuizzes = yourQuizzes
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }

        // ===== Bottom Navigation (fixed) =====
        HomeBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHome = onHome, onQuizzes = onQuizzes, onQR = onQR,
            onLeaderboard = onLeaderboard, onProfile = onProfile
        )
    }
}
