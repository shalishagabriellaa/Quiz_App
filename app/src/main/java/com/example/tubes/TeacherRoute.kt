package com.example.tubes

sealed class TeacherRoute(val route: String) {

    object Dashboard : TeacherRoute("dashboard")
    object Quizzes : TeacherRoute("quizzes")
    object QuizCreate : TeacherRoute("quiz_create")

    object QuizAddQuestions :
        TeacherRoute("quiz_add_questions/{quizId}/{totalQuestions}") {

        fun createRoute(
            quizId: String,
            totalQuestions: Int
        ): String {
            return "quiz_add_questions/$quizId/$totalQuestions"
        }
    }

    object QuizEdit : TeacherRoute("quiz_edit/{quizId}") {
        fun createRoute(quizId: String) = "quiz_edit/$quizId"
    }

    object Bank : TeacherRoute("bank")
    object Monitoring : TeacherRoute("monitoring")
    object Profile : TeacherRoute("profile")
}