package com.example.tubes

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tubes.data.repository.AuthRepositoryImpl
import com.example.tubes.data.repository.HomeRepositoryImpl
import com.example.tubes.ui.screen.*
import com.example.tubes.ui.screen.home.HomeScreen
import com.example.tubes.ui.screen.home.models.toAuthorUi
import com.example.tubes.data.AuthState
import com.example.tubes.data.repository.ProfileRepositoryImpl
import com.example.tubes.viewmodel.AuthViewModel
import com.example.tubes.viewmodel.HomeViewModel
import com.example.tubes.data.repository.QuizRepositoryImpl
import com.example.tubes.ui.screen.profile.ProfileScreen
import com.example.tubes.ui.screen.quizzes.YourQuizzesScreen
import com.example.tubes.viewmodel.ProfileViewModel
import com.example.tubes.viewmodel.QuizViewModel

@Composable
fun StudentNavigation() {
    val navController = rememberNavController()

    val authRepository = remember { AuthRepositoryImpl() }
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authRepository) as T
            }
        }
    )

    val authState by authViewModel.authState.collectAsState()
    val homeRepository = remember { HomeRepositoryImpl() }
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(homeRepository) as T
            }
        }
    )

    val quizRepository = remember { QuizRepositoryImpl() }
    val quizViewModel: QuizViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return QuizViewModel(quizRepository) as T
            }
        }
    )

    // --- PERBAIKAN 1: Membuat startDestination menjadi dinamis ---
    // Jika sudah login, mulai di "main", jika tidak, mulai di "login"
    // Ini mencegah layar login berkedip saat membuka ulang aplikasi.
    val startDestination = if (authState is AuthState.Success) "main" else Screen.LoginScreen.route

    val profileRepository = remember { ProfileRepositoryImpl() }
    val profileViewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(profileRepository) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ========== LOGIN ==========
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateRegister = { navController.navigate(Screen.RegisterScreen.route) },
                // --- PERBAIKAN 2: Hapus navigasi manual dari onLoginSuccess ---
                onLoginSuccess = {
                    // KOSONGKAN BLOK INI.
                    // Gerbang di AppNavigation.kt akan menangani navigasi secara reaktif.
                }
            )
        }

        // ========== REGISTER ==========
        composable(Screen.RegisterScreen.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateLogin = {
                    navController.popBackStack() // Lebih baik popBackStack daripada navigate
                },
                // --- PERBAIKAN 3: Hapus navigasi manual dari onRegisterSuccess ---
                onRegisterSuccess = {
                    // KOSONGKAN BLOK INI JUGA.
                    // Biarkan Gerbang yang bekerja.
                }
            )
        }

        // Di dalam NavHost
// ...

// ========== PROFILE ==========
        composable(Screen.ProfileScreen.route) {
            // BERIKAN VIEWMODEL-NYA KE SINI
            ProfileScreen(viewModel = profileViewModel)
        }
        // ========== SETTINGS ==========
        composable(Screen.SettingScreen.route) {
            SettingScreen()
        }

        // ========== PROFILE ==========
        composable(Screen.ProfileScreen.route) {
            ProfileScreen()
        }

        composable(Screen.QuizScreen.route + "/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""

            val currentAuthState = authState
            val userId = if (currentAuthState is AuthState.Success) {
                currentAuthState.userId
            } else null

            QuizScreen(
                quizId = quizId,
                viewModel = quizViewModel,
                onBackClick = { navController.popBackStack() },
                // --- INI PERBAIKANNYA ---
                // Hapus parameter 'score' dan 'total'. Cukup navigasi atau lakukan aksi lain.
                onQuizComplete = {
                    Log.d("StudentNavigation", "Quiz completed, navigating back or to results.")
                    // Anda bisa navigasi ke halaman profil/hasil di sini jika perlu.
                    // Contoh: Kembali ke layar sebelumnya setelah kuis selesai.
                    navController.popBackStack()
                },
                onViewExplanation = { qId ->
                    navController.navigate("answerExplanation/$qId")
                },
                userId = userId
            )
        }

        // ========== ANSWER EXPLANATION ==========
        composable("answerExplanation/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            AnswerExplanationScreen(
                quizId = quizId,
                viewModel = quizViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ========== LIST CATEGORY ==========
        composable("category") {
            val state by homeViewModel.uiState.collectAsState()

            CategoryScreen(
                categories = state.categoriesUi,
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { categoryUi ->
                    navController.navigate("categorySpecify/${categoryUi.id}/${categoryUi.name}")
                }
            )
        }

        // ========== CATEGORY SPECIFY ==========
        composable("categorySpecify/{categoryId}/{categoryName}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""

            CategorySpecifyScreen(
                categoryId = categoryId,
                categoryName = categoryName,
                onBackClick = { navController.popBackStack() },
                onQuizClick = { quizId ->
                    navController.navigate("testInfo/$quizId")
                }
            )
        }

        // ========== TEST INFORMATION SCREEN ==========
        composable("testInfo/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            TestInformationScreen(
                quizId = quizId,
                onBackClick = { navController.popBackStack() },
                onStartQuiz = {
                    navController.navigate(Screen.QuizScreen.route + "/$quizId")
                }
            )
        }

        // ========== TRENDING FULL LIST ==========
        composable("trending") {
            val state by homeViewModel.uiState.collectAsState()

            TrendingScreen(
                trending = state.trendingUi,
                onBackClick = { navController.popBackStack() },
                onQuizClick = { quizId ->
                    navController.navigate("testInfo/$quizId")
                }
            )
        }

        // ========== TOP AUTHORS FULL LIST ==========
        composable("topAuthors") {
            val state by homeViewModel.uiState.collectAsState()

            TopAuthorsScreen(
                authors = state.topAuthors.map { it.toAuthorUi() },
                onBackClick = { navController.popBackStack() }
            )
        }

        // ========== YOUR QUIZZES FULL LIST ==========
        composable("yourQuizzes") {
            val state by homeViewModel.uiState.collectAsState()

            YourQuizzesScreen(
                quizzes = state.yourQuizzesUi,
                onBackClick = { navController.popBackStack() },
                onQuizClick = { quizId ->
                    navController.navigate("testInfo/$quizId")
                }
            )
        }


        // ========== MAIN / HOME ==========
        composable("main") {
            val authStateMain by authViewModel.authState.collectAsState()
            val state by homeViewModel.uiState.collectAsState()

            LaunchedEffect(authStateMain) {
                if (authStateMain is AuthState.Success) {
                    val uid = (authStateMain as AuthState.Success).userId
                    Log.d("HomeScreen", "Memicu loadHome dengan UID: $uid")
                    homeViewModel.loadHome(uid)
                } else {
                    Log.w("HomeScreen", "Tidak dapat memuat data, user tidak terautentikasi.")
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                HomeScreen(
                    categories = state.categoriesUi,
                    trending = state.trendingUi,
                    topPicks = emptyList(),
                    yourQuizzes = state.yourQuizzesUi,
                    topAuthors = state.topAuthors.map { it.toAuthorUi() },
                    userName = state.userName,
                    avatarUrl = state.avatarUrl,
                    onHome = { },
                    onQuizzes = { },
                    onQR = { },
                    onLeaderboard = { },
                    onProfile = { navController.navigate("profile") },
                    onSettings = { navController.navigate("settings") },

                    onSearchQuizCode = { code ->
                        homeViewModel.searchQuizByCode(code) { quizId ->
                            Log.d("HomeScreen", "Navigate ke quiz/$quizId")
                            navController.navigate("quiz/$quizId")
                        }
                    },

                    onCategorySeeAll = {
                        navController.navigate("category")
                    },

                    onCategoryClick = { categoryUi ->
                        navController.navigate(
                            "categorySpecify/${categoryUi.id}/${categoryUi.name}"
                        )
                    },

                    onTrendingSeeAll = {
                        navController.navigate("trending")
                    },

                    onTrendingClick = { quizId ->
                        navController.navigate("testInfo/$quizId")
                    },

                    onTopAuthorsSeeAll = {
                        navController.navigate("topAuthors")
                    },
                    onYourQuizSeeAll = {
                        navController.navigate("yourQuizzes")
                    },
                    onYourQuizClick = { quizId ->
                        navController.navigate("testInfo/$quizId")
                    }
                )

            }
        }
    }
}
