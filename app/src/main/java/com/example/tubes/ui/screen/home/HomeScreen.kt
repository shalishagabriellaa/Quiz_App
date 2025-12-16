package com.example.tubes.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tubes.R
import com.example.tubes.ui.screen.home.components.HomeTopBar
import com.example.tubes.ui.screen.home.models.AuthorUi
import com.example.tubes.ui.screen.home.models.CategoryUi
import com.example.tubes.ui.screen.home.models.QuizUi
import com.example.tubes.ui.screen.home.models.YourQuizUi

// Di HomeScreen.kt, ubah signature function menjadi:

@Composable
fun HomeScreen(
    categories: List<CategoryUi>,
    trending: List<QuizUi>,
    topPicks: List<QuizUi>,
    yourQuizzes: List<YourQuizUi>,
    topAuthors: List<AuthorUi>,
    userName: String,
    avatarUrl: String?,
    searchError: String? = null, // 🆕 Tambahkan parameter ini
    onHome: () -> Unit,
    onQuizzes: () -> Unit,
    onQR: () -> Unit,
    onLeaderboard: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onSearchQuizCode: (String) -> Unit,
    onCategorySeeAll: () -> Unit,
    onCategoryClick: (CategoryUi) -> Unit,
    onTrendingSeeAll: () -> Unit,
    onTrendingClick: (String) -> Unit,
    onTopAuthorsSeeAll: () -> Unit,
    onYourQuizSeeAll: () -> Unit,
    onYourQuizClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.section_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                HomeTopBar(
                    userName = userName,
                    onSettings = onSettings,
                    onSearch = onSearchQuizCode,
                    searchError = searchError // 🆕 Pass searchError ke HomeTopBar
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                HomeSection(
                    categories = categories,
                    trending = trending,
                    topAuthors = topAuthors,
                    topPicks = topPicks,
                    yourQuizzes = yourQuizzes,
                    onCategorySeeAll = onCategorySeeAll,
                    onCategoryClick = onCategoryClick,
                    onTrendingSeeAll = onTrendingSeeAll,
                    onTrendingClick = onTrendingClick,
                    onTopAuthorsSeeAll = onTopAuthorsSeeAll,
                    onYourQuizSeeAll = onYourQuizSeeAll,
                    onYourQuizClick = onYourQuizClick,
                )
            }
        }
    }
}
