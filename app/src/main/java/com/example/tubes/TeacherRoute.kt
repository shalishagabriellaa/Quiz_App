package com.example.tubes

sealed class TeacherRoute(val route: String) {

    object Dashboard : TeacherRoute("dashboard")
    object Quizzes : TeacherRoute("quizzes")
    object QuizCreate : TeacherRoute("quiz_create")

    object QuizAddQuestions : TeacherRoute("quiz_add_questions/{quizId}/{totalQuestions}") {
        fun createRoute(quizId: String, totalQuestions: Int): String {
            return "quiz_add_questions/$quizId/$totalQuestions"
        }
    }

    object QuizEdit : TeacherRoute("quiz_edit/{quizId}") {
        fun createRoute(quizId: String) = "quiz_edit/$quizId"
    }

    object QuizQr {
        const val route = "teacher_quiz_qr/{quizId}"
        fun createRoute(quizId: String) = "teacher_quiz_qr/$quizId"
    }

    object Bank : TeacherRoute("bank")
    object Notifications : TeacherRoute("notifications")
    object Monitoring : TeacherRoute("monitoring")
    object Profile : TeacherRoute("profile")

    object ViewAll : TeacherRoute("teacher_view_all/{type}") {
        fun createRoute(type: String) = "teacher_view_all/$type"
    }

    // ✅ NEW: PROFILE MENU PAGES (TEACHER)
    object PersonalInfo : TeacherRoute("teacher_personal_info")
    object ChangePassword : TeacherRoute("teacher_change_password")
    object AboutQuorri : TeacherRoute("teacher_about_quorri")
}
