package com.example.tubes.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tubes.ui.screen.home.models.CategoryUi
import androidx.compose.foundation.text.BasicTextField

@Composable
fun CategoryScreen(
    categories: List<CategoryUi>,
    onBackClick: () -> Unit = {},
    onCategoryClick: (CategoryUi) -> Unit = {}
) {
    var showSearch by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // Filter kategori berdasarkan query
    val filteredCategories = remember(categories, query) {
        if (query.isBlank()) categories
        else categories.filter { it.name.contains(query, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            CategoryTopBar(
                title = "Category",
                onBackClick = onBackClick,
                onSearchToggle = { showSearch = !showSearch }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0x212252))
        ) {
            // Search bar muncul ketika tombol search diklik
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
                    BasicTextField(
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
                            text = "Search category...",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCategories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryTopBar(
    title: String,
    onBackClick: () -> Unit,
    onSearchToggle: () -> Unit
) {
    // Tinggi header meniru style HomeTopBar (background warna & tombol bulat)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2C3E7C),
                        Color(0xFF1A237E)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔙 Tombol Back – style sama seperti snippet (bulat, putih transparan)
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // 🔍 Tombol Search – style sama juga
            IconButton(
                onClick = onSearchToggle,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }

        // Title di tengah
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun CategoryCard(
    category: CategoryUi,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A237E))
            .clickable(onClick = onClick)
    ) {
        // Background gambar dari Firestore (Cloudinary atau lainnya)
        if (!category.bannerUrl.isNullOrEmpty()) {
            AsyncImage(
                model = category.bannerUrl,
                contentDescription = "Category: ${category.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Fallback gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1A237E),
                                Color(0xFF283593)
                            )
                        )
                    )
            )
        }

        // Overlay gelap tipis supaya teks kebaca
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )

        // Nama kategori
        Text(
            text = category.name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}
