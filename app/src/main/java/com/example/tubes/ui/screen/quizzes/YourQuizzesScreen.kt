package com.example.tubes.ui.screen.quizzes

import androidx.compose.runtime.Composable
import com.example.tubes.ui.screen.home.models.YourQuizUi
@Composable
fun YourQuizzesScreen(
    quizzes: List<YourQuizUi>,
    onBackClick: () -> Unit,
    onQuizClick: (String) -> Unit
) {
    // pakai YourQuizRow dalam LazyColumn
}
