package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.tubes.ui.theme.Pink80


data class Question(
    val id: String,
    val text: String,
    val category: String,
    val difficulty: String,
    val isCorrect: Boolean?,
    val isBookmarked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen() {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }

    val questions = remember {
        listOf(
            Question(
                id = "Q1",
                text = "Which algorithm is used for supervised classification?",
                category = "Supervised Learning",
                difficulty = "Medium",
                isCorrect = true,
                isBookmarked = false
            ),
            Question(
                id = "Q2",
                text = "What is the purpose of Gradient Descent?",
                category = "Supervised Learning",
                difficulty = "Medium",
                isCorrect = false,
                isBookmarked = false
            ),
            Question(
                id = "Q3",
                text = "Which metric is suitable for regression evaluation?",
                category = "Supervised Learning",
                difficulty = "Hard",
                isCorrect = null,
                isBookmarked = false
            )
        )
    }

    Scaffold(
        containerColor = Pink80  ,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        "Questions Bank",
//                        color = Color.White,
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = { }) {
//                        Icon(
//                            Icons.Default.Settings,
//                            contentDescription = "Menu",
//                            tint = Color.White
//                        )
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { }) {
//                        Icon(
//                            Icons.Default.Notifications,
//                            contentDescription = "Notifications",
//                            tint = Color.White
//                        )
//                    }
//                    IconButton(onClick = { }) {
//                        Box(
//                            modifier = Modifier
//                                .size(32.dp)
//                                .clip(CircleShape)
//                                .background(Color(0xFFE91E63))
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFF1E2847)
//                )
//            )
//        },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFF1E2847),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(bottom = 16.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                    Text(
                        "Questions Bank",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { }) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE91E63))
                            )
                        }
                    }
                }

                // Search Bar inside TopBar
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier.height(height = 40.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = {
                        Text(
                            "Search your questions",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF8B5CF6)
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Color(0xFF8B5CF6),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Export",
                        tint = Color.White
                    )
                    Text(
                        "Export All",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()

                .padding(padding)
        ) {
            // Search Bar
//            OutlinedTextField(
//                value = "",
//                onValueChange = { },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                placeholder = {
//                    Text(
//                        "Search your questions",
//                        color = Color.Gray,
//                        fontSize = 14.sp
//                    )
//                },
//                leadingIcon = {
//                    Icon(
//                        Icons.Default.Search,
//                        contentDescription = "Search",
//                        tint = Color.Gray
//                    )
//                },
//                shape = RoundedCornerShape(12.dp),
//                colors = OutlinedTextFieldDefaults.colors(
//                    unfocusedContainerColor = Color.White,
//                    focusedContainerColor = Color.White,
//                    unfocusedBorderColor = Color.Transparent,
//                    focusedBorderColor = Color(0xFF8B5CF6)
//                )
//            )

            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Total Questions",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        "94",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        "Average Difficulty",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        "Medium",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        "Last Updated",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        "02/11/25",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category Quiz", fontSize = 12.sp) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("All", "Supervised Learning", "Unsupervised Learning").forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Questions List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(questions) { question ->
                    QuestionCard(question)
                }
            }
        }
    }
}

@Composable
fun QuestionCard(question: Question) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Question Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    question.id,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2847)
                )
//                Icon(
//                    if (question.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
//                    contentDescription = "Bookmark",
//                    tint = if (question.isBookmarked) Color(0xFF8B5CF6) else Color.Gray,
//                    modifier = Modifier.size(20.dp)
//                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Question Text
            Text(
                question.text,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category and Difficulty Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            "Category: ${question.category}",
                            fontSize = 11.sp
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFE3F2FD),
                        labelColor = Color(0xFF1976D2)
                    ),
                    border = null
                )
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            "Difficulty: ${question.difficulty}",
                            fontSize = 11.sp
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFFFF3E0),
                        labelColor = Color(0xFFE65100)
                    ),
                    border = null
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text("Detail", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFE8F5E9),
                        labelColor = Color(0xFF2E7D32)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                )
                AssistChip(
                    onClick = { },
                    label = { Text("Edit", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFFFF3E0),
                        labelColor = Color(0xFFE65100)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                )
                AssistChip(
                    onClick = { },
                    label = { Text("Delete", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFFFEBEE),
                        labelColor = Color(0xFFC62828)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                )
            }
        }
    }
}
