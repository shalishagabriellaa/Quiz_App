package com.example.tubes.ui.screen.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutQuorriScreen(
    onBack: () -> Unit = {},
    onContactUs: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About Quorri",
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // Logo placeholder (ganti dengan logo Anda)
            item {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0xFF87CEEB), RoundedCornerShape(60.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.School,
                        contentDescription = "Quorri Logo",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // App name
            item {
                Text(
                    "Quorri",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            item { Spacer(Modifier.height(8.dp)) }

            // Version info
            item {
                Text(
                    "Version 1.0.0",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    "© 2025 Quorri Inc.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            item { Spacer(Modifier.height(32.dp)) }

            // What is Quorri section
            item {
                InfoSection(
                    title = "What is Quorri?",
                    backgroundColor = Color(0xFFE8F5E9),
                    content = "Quorri is a smart quiz-based learning platform designed to help students enhance their study habits through engaging and interactive quizzes. Built on the idea of \"Question\" and \"Glory,\" Quorri represents the journey every learner takes — from curiosity, to challenge, to achievement.\n\nWhether you're studying for exams, preparing assignments, or just improving your knowledge, Quorri is here to guide you every step of the way."
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            // Our Story section
            item {
                InfoSection(
                    title = "Our Story",
                    backgroundColor = Color(0xFFE3F2FD),
                    content = "We started Quorri because we saw a simple truth: Students want to learn, but traditional studying often feels boring, overwhelming, and isolated.\n\nSo we asked ourselves: What if learning felt like a game? What if every quiz was a step forward, not just a test? What mattered? From that vision, Quorri was built — an app that turns quizzes into an exciting experience, transforming learning into something you genuinely look forward to."
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            // Our Mission section
            item {
                InfoSection(
                    title = "Our Mission",
                    backgroundColor = Color(0xFFFFF9E6),
                    content = "To empower students and learners by making education:\n• Engaging, not boring\n• Motivate\n• Accessible, so anyone can learn anytime, anywhere\n• Effective, through personalized progress tracking\n• Rewarding, with interactive milestones, streaks, and achievements\n\nQuorri believes every learner is capable of greatness — they just need the right support and encouragement."
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            // What Quorri Offers section
            item {
                InfoSection(
                    title = "What Quorri Offers",
                    backgroundColor = Color(0xFFFFEBEE),
                    content = "📚 Wide Range of Quiz Categories\nFrom math and science to language and general knowledge — explore topics that fit your goals.\n\n💡 Instant Feedback & Explanations\nLearn not just what's right, but why it's right.\n\n🏆 Achievements & Streak Records\nEarn badges, maintain streaks, and celebrate your progress along the way."
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            // Why We Built Quorri section
            item {
                InfoSection(
                    title = "Why We Built Quorri",
                    backgroundColor = Color(0xFFF3E5F5),
                    content = "We believe learning should be:\n• Enjoyable, not stressful\n• Motivating, not discouraging\n• Simple, not complicated\n\nQuorri helps learners build confidence, unlock potential, and enjoy the process of mastering skills — one question at a time."
                )
            }

            item { Spacer(Modifier.height(32.dp)) }

            // Contact section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Headset,
                            contentDescription = "Contact",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Want to know us?",
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
                            onClick = onContactUs,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Contact Us", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    backgroundColor: Color,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                content,
                fontSize = 13.sp,
                color = Color(0xFF424242),
                lineHeight = 20.sp
            )
        }
    }
}
