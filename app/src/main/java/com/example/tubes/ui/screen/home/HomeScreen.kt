package com.example.tubes.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.R
import com.example.tubes.ui.screen.home.components.HomeTopBar
import com.example.tubes.ui.screen.home.models.AuthorUi
import com.example.tubes.ui.screen.home.models.CategoryUi
import com.example.tubes.ui.screen.home.models.QuizUi
import com.example.tubes.ui.screen.home.models.YourQuizUi

@Composable
fun HomeScreen(
    categories: List<CategoryUi>,
    trending: List<QuizUi>,
    topPicks: List<QuizUi>,
    yourQuizzes: List<YourQuizUi>,
    topAuthors: List<AuthorUi>,
    userName: String,
    avatarUrl: String?,
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
    onYourQuizSeeAll: () -> Unit = {},
    onYourQuizClick: (String) -> Unit = {},
    onTopAuthorsSeeAll: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }

    // --- BLOK PERBAIKAN DIMULAI ---
    // LaunchedEffect akan berjalan setiap kali `selectedTab` berubah,
    // lalu memanggil fungsi navigasi yang sesuai.
    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            BottomTab.Home -> onHome()
            BottomTab.Quizzes -> onQuizzes()
            BottomTab.Leaderboard -> onLeaderboard()
            BottomTab.Profile -> onProfile()
        }
    }
    // --- BLOK PERBAIKAN SELESAI ---

    Box(Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.section_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                HomeTopBar(
                    userName = userName,
                    onSettings = onSettings,
                    onSearch = onSearchQuizCode
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
                    onYourQuizSeeAll = onYourQuizSeeAll,
                    onYourQuizClick = onYourQuizClick,
                    onTopAuthorsSeeAll = onTopAuthorsSeeAll
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
        }

        HomeBottomNav(
            selected = selectedTab,
            onSelected = { selectedTab = it },
            onQrClick = onQR,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

enum class BottomTab(val label: String) {
    Home("Home"),
    Quizzes("Quizzes"),
    Leaderboard("Leaderboard"),
    Profile("Profile")
}

private val BarPurple  = Color(0xFF4C4FA4)
private val DeepBlue   = Color(0xFF162471)
private val GoldActive = Color(0xFFF4D488)

@Composable
fun HomeBottomNav(
    selected: BottomTab,
    onSelected: (BottomTab) -> Unit,
    onQrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(98.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(BarPurple)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BarItem(
                    text = BottomTab.Home.label,
                    icon = Icons.Filled.Home,
                    selected = selected == BottomTab.Home
                ) { onSelected(BottomTab.Home) }

                BarItem(
                    text = BottomTab.Quizzes.label,
                    icon = Icons.Outlined.GridView,
                    selected = selected == BottomTab.Quizzes
                ) { onSelected(BottomTab.Quizzes) }

                Spacer(Modifier.width(56.dp))

                BarItem(
                    text = BottomTab.Leaderboard.label,
                    icon = Icons.Filled.Leaderboard,
                    selected = selected == BottomTab.Leaderboard
                ) { onSelected(BottomTab.Leaderboard) }

                BarItem(
                    text = BottomTab.Profile.label,
                    icon = Icons.Filled.Person,
                    selected = selected == BottomTab.Profile
                ) { onSelected(BottomTab.Profile) }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-8).dp)
                    .size(width = 120.dp, height = 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.95f))
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(64.dp)
                .shadow(12.dp, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onQrClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.QrCode,
                contentDescription = "QR",
                tint = DeepBlue,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun BarItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(min = 64.dp)
            .clickable { onClick() }
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GoldActive)
                    .border(3.dp, DeepBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = text, tint = DeepBlue)
            }
        } else {
            Icon(icon, contentDescription = text, tint = Color.White)
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
