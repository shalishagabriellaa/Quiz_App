package com.example.tubes.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

@Composable
fun TopAuthorsScreen(
    authors: List<AuthorUi>,
    onBackClick: () -> Unit
) {
    // Sama seperti CategoryScreen: ada icon search di top bar
    var showSearch by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // Filter authors berdasarkan query (nama)
    val filteredAuthors = remember(authors, query) {
        if (query.isBlank()) authors
        else authors.filter {
            it.fullName.contains(query, ignoreCase = true)
        }
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            CategoryTopBar(
                title = "Top Authors",
                onBackClick = onBackClick,
                onSearchToggle = { showSearch = !showSearch }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF212252)) // sama dengan CategoryScreen
        ) {
            // (Optional) search bar muncul ketika tombol search diklik
            if (showSearch) {
                Spacer(Modifier.height(15.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF212252))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontSize = 14.sp
                        ),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (query.isEmpty()) {
                        Text(
                            text = "Search authors...",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredAuthors) { author ->
                    AuthorRow(author = author)
                }
            }
        }
    }
}

@Composable
private fun AuthorRow(
    author: AuthorUi
) {
    // Dummy state follow (belum connect ke backend follow/unfollow beneran)
    var isFollowing by remember { mutableStateOf(false) }

    // Bikin "username" sederhana dari nama, biar mirip desain
    val handle = remember(author.fullName) {
        "@${author.fullName.replace(" ", "").lowercase()}"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(50))
        ) {
            if (!author.avatarUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = author.avatarUrl,
                    contentDescription = "Avatar for ${author.fullName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback warna polos
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFB8B8FF))
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Nama + handle
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = author.fullName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = handle,
                fontSize = 13.sp,
                color = Color(0xFF6F7393),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        // Tombol Follow / Following
        FollowButton(
            isFollowing = isFollowing,
            onToggle = { isFollowing = !isFollowing }
        )
    }
}

@Composable
private fun FollowButton(
    isFollowing: Boolean,
    onToggle: () -> Unit
) {
    val text = if (isFollowing) "Following" else "Follow"
    val bgColor = if (isFollowing) Color.White else Color(0xFF198CFF)
    val textColor = if (isFollowing) Color(0xFF198CFF) else Color.White
    val borderColor = Color(0xFF198CFF)

    Button(
        onClick = onToggle,
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
