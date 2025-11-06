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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.R
import com.example.tubes.ui.screen.home.components.HomeTopBar
import com.example.tubes.ui.screen.home.models.AuthorUi
import com.example.tubes.ui.screen.home.models.CategoryUi
import com.example.tubes.ui.screen.home.models.QuizUi

/* =======================
 *   HOME SCREEN
 * ======================= */
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
    // state tab untuk bottom bar (tanpa navigation dulu)
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }

    Box(Modifier.fillMaxSize()) {

        // BG: kalau tidak punya drawable, ganti ke gradient
        Image(
            painter = painterResource(id = R.drawable.section_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // SATU-SATUNYA SCROLL VERTICAL
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp) // ruang bottom bar
        ) {
            // Topbar (punya wave image sendiri)
            item { HomeTopBar(userName = userName) }

            // Sedikit jarak agar terasa blend halus (tweak bila perlu)
            item { Spacer(Modifier.height(12.dp)) }

            // Isi section (semua list di dalamnya bisa horizontal scroll)
            item {
                HomeSection(
                    categories = categories.map { CategoryUi(it) },
                    trending = trending.ifEmpty {
                        listOf(
                            QuizUi("Machine Learning Practice Test", "Wan Guntar Alam", 5),
                            QuizUi("Understanding Neural Networks", "Guntur", 3)
                        )
                    },
                    topAuthors = listOf(
                        AuthorUi("Hannah"), AuthorUi("Brian"),
                        AuthorUi("Nurul"), AuthorUi("Gaby"), AuthorUi("Hana")
                    ),
                    topPicks = if (topPicks.isEmpty()) trending else topPicks,
                    yourQuizzes = if (yourQuizzes.isEmpty()) trending else yourQuizzes
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
        }

        // Bottom bar custom (tanpa navigation)
        HomeBottomNav(
            selected = selectedTab,
            onSelected = { selectedTab = it },
            onQrClick = onQR,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/* =======================
 *   BOTTOM BAR (inline)
 * ======================= */

enum class BottomTab(val label: String) {
    Home("Home"),
    Quizzes("Quizzes"),
    Leaderboard("Leaderboard"),
    Profile("Profile")
}

private val BarPurple  = Color(0xFF4C4FA4)   // warna bar
private val DeepBlue   = Color(0xFF162471)   // ring/icon aktif
private val GoldActive = Color(0xFFF4D488)   // lingkaran aktif

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
        // Bar container rounded top
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

                Spacer(Modifier.width(56.dp)) // ruang QR center

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

            // "home indicator" putih kecil
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-8).dp)
                    .size(width = 120.dp, height = 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.95f))
            )
        }

        // Tombol QR melayang di tengah
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
            // ikon di lingkaran emas + ring biru (match mockup)
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

/* =======================
 *   PREVIEW
 * ======================= */
@Preview(showBackground = true, backgroundColor = 0xFF121142, widthDp = 402, heightDp = 1723)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        categories = listOf("Technology", "Technology", "Technology", "Music", "Music"),
        trending = listOf(
            QuizUi("Machine Learning Practice Test", "Wan Guntar Alam", 3),
            QuizUi("Machine Learning Practice Test", "Wan Guntar Alam", 3)
        ),
        topPicks = listOf(
            QuizUi("Machine Learning Practice Test", "Wan Guntar Alam", 3),
            QuizUi("Machine Learning Practice Test", "Wan Guntar Alam", 3)
        ),
        yourQuizzes = listOf(
            QuizUi("Machine Learning Practice Test", "Wan Guntar Alam", 3),
            QuizUi("Machine Learning Practice Test", "Wan Guntar Alam", 3)
        )
    )
}
