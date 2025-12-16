package com.example.tubes.ui.screen.setting

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

data class HelpArticle(
    val id: String,
    val title: String,
    val updatedDaysAgo: Int,
    val category: String,
    val content: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onBack: () -> Unit = {},
    onOpenArticle: (articleId: String) -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    // ✅ STATIC DATA
    val allArticles = remember {
        listOf(
            HelpArticle(
                id = "getting-started",
                title = "Getting started with your account",
                updatedDaysAgo = 2,
                category = "FAQs & Troubleshooting",
                content = "Learn how to create an account, set up your profile, and start using the app. If you face login issues, check your email/password and internet connection."
            ),
            HelpArticle(
                id = "reset-password",
                title = "How to reset your password",
                updatedDaysAgo = 2,
                category = "FAQs & Troubleshooting",
                content = "Go to Login → Forgot Password → enter your email. Check inbox/spam for reset link. Make sure your email is correct."
            ),
            HelpArticle(
                id = "profile-setup",
                title = "Setting up your profile",
                updatedDaysAgo = 4,
                category = "User Guides & Manuals",
                content = "Open Profile → Edit Profile. Add avatar, name, and bio. Keep your profile updated so leaderboard & friends can recognize you."
            ),
            HelpArticle(
                id = "notifications",
                title = "Managing notifications",
                updatedDaysAgo = 7,
                category = "User Guides & Manuals",
                content = "Open Settings → Notifications. Toggle quiz reminders and activity updates. If notifications don't appear, allow app notification permission."
            ),
            HelpArticle(
                id = "quiz-troubleshoot",
                title = "Quiz loading / timer issues (Troubleshooting)",
                updatedDaysAgo = 1,
                category = "FAQs & Troubleshooting",
                content = "If quiz screen freezes or timer doesn't move: check your connection, relaunch app, and ensure quiz duration exists. If still happens, contact support."
            )
        )
    }

    // ✅ SEARCH WORKS (title + content + category)
    val filteredArticles = remember(searchQuery, allArticles) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) allArticles
        else allArticles.filter {
            it.title.lowercase().contains(q) ||
                    it.content.lowercase().contains(q) ||
                    it.category.lowercase().contains(q)
        }
    }

    val popularArticles = remember(filteredArticles) {
        // kamu bisa atur logic "popular" statis; ini contoh: ambil 4 teratas
        filteredArticles.take(4)
    }

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
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            item { Spacer(Modifier.height(20.dp)) }

            // Category Cards (klik -> buka artikel pertama dari kategori tsb / atau bisa ke list)
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
                            "Quiz loading issues"
                        ),
                        backgroundColor = Color(0xFFE3F2FD),
                        iconTint = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // contoh: buka artikel pertama dari kategori ini
                            val first = allArticles.firstOrNull { it.category == "FAQs & Troubleshooting" }
                            if (first != null) onOpenArticle(first.id)
                        }
                    )

                    HelpCategoryCard(
                        icon = Icons.Filled.Book,
                        title = "User Guides & Manuals",
                        items = listOf(
                            "Setting up your profile",
                            "Managing notifications",
                            "Using the app features"
                        ),
                        backgroundColor = Color(0xFFFFF9E6),
                        iconTint = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val first = allArticles.firstOrNull { it.category == "User Guides & Manuals" }
                            if (first != null) onOpenArticle(first.id)
                        }
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

            if (popularArticles.isEmpty()) {
                item {
                    Text(
                        text = "No articles found for \"$searchQuery\"",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(popularArticles) { article ->
                    ArticleItem(
                        title = article.title,
                        subtitle = article.category,
                        updatedDaysAgo = article.updatedDaysAgo,
                        onClick = { onOpenArticle(article.id) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Contact Support (WhatsApp)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            openWhatsApp(
                                context = context,
                                phoneE164 = "6282273583360", // ✅ 0822... jadi 62...
                                message = "Halo, saya butuh bantuan terkait aplikasi Quorri."
                            )
                        },
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
                                "Contact our support team on WhatsApp",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Button(
                            onClick = {
                                openWhatsApp(
                                    context = context,
                                    phoneE164 = "6282273583360",
                                    message = "Halo, saya butuh bantuan terkait aplikasi Quorri."
                                )
                            },
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

private fun openWhatsApp(
    context: android.content.Context,
    phoneE164: String,
    message: String
) {
    // ✅ wa.me link (paling aman, gak perlu package check)
    val url = "https://wa.me/$phoneE164?text=${Uri.encode(message)}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Composable
private fun HelpCategoryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    items: List<String>,
    backgroundColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(8.dp))
            items.forEach { item ->
                Text("• $item", fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun ArticleItem(
    title: String,
    subtitle: String,
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
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$subtitle • Updated $updatedDaysAgo days ago",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(Icons.Filled.ChevronRight, contentDescription = "Go", tint = Color.Gray)
        }
    }
}
