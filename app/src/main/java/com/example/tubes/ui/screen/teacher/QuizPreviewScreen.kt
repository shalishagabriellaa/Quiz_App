package com.example.tubes.ui.screen.teacher

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tubes.data.model.TeacherQuestionForm

@Composable
fun QuizPreviewScreen(
    questions: List<Pair<Int, TeacherQuestionForm>>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        Text("Quiz Preview")

        Spacer(Modifier.height(16.dp))

        questions.forEach { (index, q) ->
            Text("Question $index")
            Text(q.questionText)

            q.options.forEachIndexed { i, opt ->
                Text(
                    text = "${'A' + i}. $opt",
                    color = if (i == q.correctAnswerIndex)
                        Color(0xFF4C75FF)
                    else
                        Color.Black
                )
            }

            if (q.explanation.isNotBlank()) {
                Text(
                    text = "Explanation: ${q.explanation}",
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        Button(onClick = onBack) {
            Text("Back to Edit")
        }
    }
}
