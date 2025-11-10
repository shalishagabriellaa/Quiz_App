package com.example.tubes.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.R

// Data class untuk Category
data class Category(
    val id: Int,
    val name: String,
    val imageRes: Int, // Nanti bisa diganti dengan URL dari database
    val backgroundColor: Color
)

// Dummy data untuk sementara
object DummyData {
    val categories = listOf(
        Category(1, "Technology", R.drawable.ic_owl, Color(0xFF1A237E)),
        Category(2, "Music", R.drawable.ic_owl, Color(0xFFFFF3E0)),
        Category(3, "Sports", R.drawable.ic_owl, Color(0xFFE0E0E0)),
        Category(4, "Science", R.drawable.ic_owl, Color(0xFFFFF3E0)),
        Category(5, "Technology", R.drawable.ic_owl, Color(0xFF1A237E)),
        Category(6, "Music", R.drawable.ic_owl, Color(0xFFFFF3E0)),
        Category(7, "Sports", R.drawable.ic_owl, Color(0xFFE0E0E0)),
        Category(8, "Science", R.drawable.ic_owl, Color(0xFFFFF3E0)),
        Category(9, "Technology", R.drawable.ic_owl, Color(0xFF1A237E)),
        Category(10, "Music", R.drawable.ic_owl, Color(0xFFFFF3E0)),
        Category(11, "Sports", R.drawable.ic_owl, Color(0xFFE0E0E0)),
        Category(12, "Science", R.drawable.ic_owl, Color(0xFFFFF3E0)),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCategoryClick: (Category) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Category",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2C3E7C)
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5FF))
        ) {
            items(DummyData.categories) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(category.backgroundColor)
            .clickable(onClick = onClick)
    ) {
        // Background gradient overlay untuk Technology
        if (category.backgroundColor == Color(0xFF1A237E)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A237E),
                                Color(0xFF283593)
                            )
                        )
                    )
            )
        }

        // Image (placeholder - nanti diganti dengan Coil untuk load dari URL)
        // Untuk sementara, bisa pakai icon atau shape sebagai placeholder

        // Label text
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = category.name,
                color = if (category.backgroundColor == Color(0xFF1A237E))
                    Color.White else Color(0xFF333333),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CategoryScreenPreview() {
    MaterialTheme {
        CategoryScreen()
    }
}

// ViewModel untuk mengelola data dari database
// class CategoryViewModel : ViewModel() {
//     private val _categories = MutableStateFlow<List<Category>>(emptyList())
//     val categories: StateFlow<List<Category>> = _categories.asStateFlow()
//
//     fun loadCategories() {
//         viewModelScope.launch {
//             // Load dari database
//             val data = repository.getCategories()
//             _categories.value = data
//         }
//     }
// }

// Repository interface untuk database
// interface CategoryRepository {
//     suspend fun getCategories(): List<Category>
//     suspend fun getCategoryById(id: Int): Category?
// }

// Implementation dengan Room Database
// @Dao
// interface CategoryDao {
//     @Query("SELECT * FROM categories")
//     suspend fun getAllCategories(): List<CategoryEntity>
//
//     @Query("SELECT * FROM categories WHERE id = :id")
//     suspend fun getCategoryById(id: Int): CategoryEntity?
// }
//
// @Entity(tableName = "categories")
// data class CategoryEntity(
//     @PrimaryKey val id: Int,
//     val name: String,
//     val imageUrl: String,
//     val backgroundColor: String
// )