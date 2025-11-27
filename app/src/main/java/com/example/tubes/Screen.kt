package com.example.tubes

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash")
    object LoginScreen : Screen("login")
    object RegisterScreen : Screen("register")
    object MainScreen : Screen("main")
    object HomeScreen : Screen("home")
    object ProfileScreen : Screen("profile")
    object SettingScreen : Screen("settings")
    object CategoryScreen : Screen("category")
    object QuizScreen : Screen("quiz")

    // Dengan parameter
    object CategorySpecifyScreen : Screen("categorySpecify/{categoryId}/{categoryName}")
    object TestInformationScreen : Screen("testInfo/{quizId}")
}