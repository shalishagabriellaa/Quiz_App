package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.data.model.TeacherQuestionForm

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPreviewScreen(
    questions: List<Pair<Int, TeacherQuestionForm>>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Preview", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(questions) { _, (index, question) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Text(
                            "Question $index",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlue
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            question.questionText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )

                        Spacer(Modifier.height(16.dp))

                        question.options.forEachIndexed { i, opt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${'A' + i}.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (i == question.correctAnswerIndex) Color(0xFF4CAF50) else Color.Gray,
                                    modifier = Modifier.width(30.dp)
                                )
                                Text(
                                    text = opt,
                                    fontSize = 14.sp,
                                    color = if (i == question.correctAnswerIndex) Color(0xFF4CAF50) else Color.Black
                                )
                            }
                        }

                        if (question.explanation.isNotBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Divider(color = Color.LightGray)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Explanation:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                question.explanation,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to Edit", color = Color.White)
                }
            }
        }
    }
}
