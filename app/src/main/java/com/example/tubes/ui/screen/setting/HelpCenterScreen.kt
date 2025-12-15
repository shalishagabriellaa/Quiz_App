package com.example.tubes.ui.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

data class HelpArticle(
    val title: String,
    val updatedDaysAgo: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onBack: () -> Unit = {},
    onContactSupport: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val popularArticles = listOf(
        HelpArticle("Getting started with your account", 2),
        HelpArticle("How to reset your password", 2),
        HelpArticle("How to reset your password", 2),
        HelpArticle("Getting started with your account", 2)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Help Center",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlue
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Search for help...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item { Spacer(Modifier.height(20.dp)) }

            // Category Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HelpCategoryCard(
                        icon = Icons.Filled.HelpOutline,
                        title = "FAQs & Troubleshooting",
                        items = listOf(
                            "How to recover a lost password",
                            "Fixing connectivity problems",
                            "Account settings help"
                        ),
                        backgroundColor = Color(0xFFE3F2FD),
                        iconTint = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )

                    HelpCategoryCard(
                        icon = Icons.Filled.Book,
                        title = "User Guides & Manuals",
                        items = listOf(
                            "Setting up your profile",
                            "Managing notifications",
                            "Payment methods guide"
                        ),
                        backgroundColor = Color(0xFFFFF9E6),
                        iconTint = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Popular Articles
            item {
                Text(
                    "Popular Articles",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            items(popularArticles) { article ->
                ArticleItem(
                    title = article.title,
                    updatedDaysAgo = article.updatedDaysAgo,
                    onClick = { /* TODO: Open article */ }
                )
                Spacer(Modifier.height(12.dp))
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Contact Support
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onContactSupport),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Headset,
                            contentDescription = "Support",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Still need help?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                "Contact our support team",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Button(
                            onClick = onContactSupport,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Contact Us", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpCategoryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    items: List<String>,
    backgroundColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(8.dp))

            items.forEach { item ->
                Text(
                    "• $item",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ArticleItem(
    title: String,
    updatedDaysAgo: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Article,
                contentDescription = "Article",
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(40.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    "Updated $updatedDaysAgo days ago",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Go",
                tint = Color.Gray
            )
        }
    }
}
