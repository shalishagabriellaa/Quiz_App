package com.example.tubes.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Struktur data minimal untuk kartu quiz (bukan dummy; hanya tipe)
data class QuizUi(val title: String, val author: String)

/* ===== util judul section ===== */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

/* ===== Category ===== */
fun LazyListScope.categorySection(items: List<String>) {
    if (items.isEmpty()) return
    item { SectionTitle("Category") }
    item { Spacer(Modifier.height(10.dp)) }
    item {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f))
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(label, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
    item { Spacer(Modifier.height(16.dp)) }
}

/* ===== Banner (selalu tampil) ===== */
fun LazyListScope.playWithFriendsBanner() {
    item {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF4051A1))
                .padding(20.dp)
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
                    onClick = {},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Find Friends", color = Color(0xFF4051A1))
                }
            }
        }
    }
    item { Spacer(Modifier.height(16.dp)) }
}

/* ===== Trending ===== */
fun LazyListScope.trendingSection(items: List<QuizUi>) {
    if (items.isEmpty()) return
    item { SectionTitle("Trending Quiz") }
    item { Spacer(Modifier.height(10.dp)) }
    item {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { QuizCard(it) }
        }
    }
    item { Spacer(Modifier.height(16.dp)) }
}

/* ===== Top Authors (placeholder tanpa data; sembunyikan kalau belum perlu)
   Jika nanti mau pakai data, ubah signature mirip categorySection.
*/
fun LazyListScope.topAuthorsSection() {
    // sementara tampilkan kosong? kalau tidak butuh, comment return
    // return
    item { SectionTitle("Top Authors") }
    item { Spacer(Modifier.height(10.dp)) }
    item {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(0) { } // tidak render apa-apa
        }
    }
    item { Spacer(Modifier.height(16.dp)) }
}

/* ===== Top Picks ===== */
fun LazyListScope.topPicksSection(items: List<QuizUi>) {
    if (items.isEmpty()) return
    item { SectionTitle("Top Picks") }
    item { Spacer(Modifier.height(10.dp)) }
    item {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { QuizCard(it) }
        }
    }
    item { Spacer(Modifier.height(16.dp)) }
}

/* ===== Your Quizzes ===== */
fun LazyListScope.yourQuizzesSection(items: List<QuizUi>) {
    item { SectionTitle("Your Quizzes") }
    item { Spacer(Modifier.height(10.dp)) }
    item {
        if (items.isEmpty()) {
            // placeholder ringan
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(92.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada kuis", color = Color.White.copy(0.9f))
            }
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items.forEach {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.28f))
                            .padding(16.dp)
                    ) {
                        Text(it.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/* ===== Kartu Quiz ===== */
@Composable
private fun QuizCard(item: QuizUi) {
    Column(
        modifier = Modifier
            .width(210.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.28f))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .height(110.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.45f))
        )
        Spacer(Modifier.height(8.dp))
        Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(item.author, color = Color.White, fontSize = 12.sp)
    }
}
