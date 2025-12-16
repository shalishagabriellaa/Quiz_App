package com.example.tubes.ui.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpArticleDetailScreen(
    articleId: String,
    onBack: () -> Unit
) {
    val article = rememberHelpArticle(articleId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(article?.title ?: "Help") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        if (article == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Article not found", color = Color.Gray)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF7F7FB))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(article.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${article.category} • Updated ${article.updatedDaysAgo} days ago",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Details", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFEFF3FF))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1A4DFF))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = article.content,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ✅ Static lookup helper (same data as HelpCenterScreen)
@Composable
private fun rememberHelpArticle(articleId: String): HelpArticle? {
    val all = listOf(
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
    return all.firstOrNull { it.id == articleId }
}
