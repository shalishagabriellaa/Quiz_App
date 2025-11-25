package com.example.tubes

// Satu-satunya sumber kebenaran untuk semua route
sealed class Screen(val route: String) {

    object LoginScreen : Screen("login")
    object RegisterScreen : Screen("register")
    object MainScreen : Screen("main")

    object SettingScreen : Screen("settings")
    object ProfileScreen : Screen("profile")

    // Base route untuk quiz. Nanti dipakai sebagai "quiz/{quizId}"
    object QuizScreen : Screen("quiz")
}
