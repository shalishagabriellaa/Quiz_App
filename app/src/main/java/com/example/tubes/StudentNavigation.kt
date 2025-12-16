package com.example.tubes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.tubes.data.AuthState
import com.example.tubes.data.repository.*
import com.example.tubes.ui.UserNotificationScreen
import com.example.tubes.ui.screen.*
import com.example.tubes.ui.screen.home.HomeScreen
import com.example.tubes.ui.screen.home.components.BottomTab
import com.example.tubes.ui.screen.home.components.QuizBottomBar
import com.example.tubes.ui.screen.home.models.toAuthorUi
import com.example.tubes.ui.screen.leaderboard.LeaderboardScreen
import com.example.tubes.ui.screen.profile.ProfileScreen
import com.example.tubes.ui.screen.profile.FollowersScreen
import com.example.tubes.ui.screen.profile.FollowingScreen
import com.example.tubes.ui.screen.quizzes.YourQuizzesScreen
import com.example.tubes.ui.screen.setting.*
import com.example.tubes.ui.screen.qr.QrScanScreen
import com.example.tubes.viewmodel.*

@Composable
fun StudentNavigation() {
    val navController = rememberNavController()

    // ===== ViewModels setup =====
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
    val categorySpecifyViewModel: CategorySpecifyViewModel = viewModel(
        factory = CategorySpecifyViewModelFactory(homeRepository)
    )
    val testInfoViewModel: TestInformationViewModel = viewModel(
        factory = TestInformationViewModelFactory(homeRepository)
    )
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

    val profileRepository = remember { ProfileRepositoryImpl() }
    val profileViewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(profileRepository) as T
            }
        }
    )

    val leaderboardViewModel: LeaderboardViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LeaderboardViewModel(homeRepository) as T
            }
        }
    )

    val yourQuizzesViewModel: YourQuizzesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return YourQuizzesViewModel(homeRepository) as T
            }
        }
    )

    val followersViewModel: FollowersViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FollowersViewModel(profileRepository) as T
            }
        }
    )

    val followingViewModel: FollowingViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FollowingViewModel(profileRepository) as T
            }
        }
    )

    // Root NavHost
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashGate(authState = authState) { route ->
                navController.navigate(route) {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(Screen.LoginScreen.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateRegister = { navController.navigate(Screen.RegisterScreen.route) },
                onForgotPassword = { navController.navigate(Screen.ForgotPasswordScreen.route) },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.RegisterScreen.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo(Screen.RegisterScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }

        composable(Screen.SettingScreen.route) {
            SettingScreen(
                onBack = { navController.popBackStack() },
                onPersonalInfo = { navController.navigate("personalInfo") },
                onChangePassword = { navController.navigate("changePassword") },
                onHelpCenter = { navController.navigate("helpCenter") },
                onAboutQuorri = { navController.navigate("aboutQuorri") },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("personalInfo") {
            val context = androidx.compose.ui.platform.LocalContext.current
            val personalInfoViewModel: PersonalInfoViewModel = viewModel(
                factory = PersonalInfoViewModelFactory(context)
            )
            PersonalInfoScreen(
                viewModel = personalInfoViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("changePassword") {
            val vm: ChangePasswordViewModel = viewModel(
                factory = ChangePasswordViewModelFactory()
            )
            ChangePasswordScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("aboutQuorri") {
            AboutQuorriScreen(onBack = { navController.popBackStack() })
        }

        // ✅ QR SCAN SCREEN (BOTTOM NAV AUTO HIDE, karena route ini di ROOT)
        composable("qr_scan") {
            QrScanScreen(
                onBack = { navController.popBackStack() },
                onFoundQuiz = { quizId ->
                    navController.navigate("testInfo/$quizId") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.QuizScreen.route + "/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            val userId = (authState as? AuthState.Success)?.userId

            QuizScreen(
                quizId = quizId,
                viewModel = quizViewModel,
                onBackClick = { navController.popBackStack() },
                onQuizComplete = { navController.popBackStack() },
                onViewExplanation = { qId ->
                    navController.navigate("answerExplanation/$qId")
                },
                userId = userId
            )
        }

        composable("answerExplanation/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            val userId = (authState as? AuthState.Success)?.userId ?: ""
            AnswerExplanationScreen(
                quizId = quizId,
                userId = userId,
                onBackClick = { navController.popBackStack() }
            )
        }

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

        composable("categorySpecify/{categoryId}/{categoryName}") { entry ->
            val categoryId = entry.arguments?.getString("categoryId") ?: ""
            val categoryName = entry.arguments?.getString("categoryName") ?: ""

            CategorySpecifyScreen(
                categoryId = categoryId,
                categoryName = categoryName,
                viewModel = categorySpecifyViewModel,
                onBackClick = { navController.popBackStack() },
                onQuizClick = { quizId -> navController.navigate("testInfo/$quizId") }
            )
        }

        // ✅ testInfo route (hasil scan masuk sini)
        composable("testInfo/{quizId}") { entry ->
            val quizId = entry.arguments?.getString("quizId") ?: ""
            TestInformationScreen(
                quizId = quizId,
                viewModel = testInfoViewModel,
                onBackClick = { navController.popBackStack() },
                onStartQuiz = { navController.navigate(Screen.QuizScreen.route + "/$quizId") }
            )
        }

        composable("trending") {
            val state by homeViewModel.uiState.collectAsState()
            TrendingScreen(
                trending = state.trendingUi,
                onBackClick = { navController.popBackStack() },
                onQuizClick = { quizId -> navController.navigate("testInfo/$quizId") }
            )
        }

        composable("topAuthors") {
            val state by homeViewModel.uiState.collectAsState()
            TopAuthorsScreen(
                authors = state.topAuthors.map { it.toAuthorUi() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("followers/{userId}") { entry ->
            val userId = entry.arguments?.getString("userId") ?: ""
            FollowersScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onUserClick = { /* optional */ },
                viewModel = followersViewModel
            )
        }

        composable("following/{userId}") { entry ->
            val userId = entry.arguments?.getString("userId") ?: ""
            FollowingScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onUserClick = { /* optional */ },
                viewModel = followingViewModel
            )
        }

        composable("quizHistory/{userId}") { entry ->
            val userId = entry.arguments?.getString("userId") ?: ""
            YourQuizzesScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onResultClick = { quizId -> navController.navigate("testInfo/$quizId") },
                viewModel = yourQuizzesViewModel
            )
        }

        composable("helpCenter") {
            HelpCenterScreen(
                onBack = { navController.popBackStack() },
                onOpenArticle = { id -> navController.navigate("helpDetail/$id") }
            )
        }

        composable("helpDetail/{id}") { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            HelpArticleDetailScreen(
                articleId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.NotificationScreen.route) {
            UserNotificationScreen(onBack = { navController.popBackStack() })
        }

        // ===== MAIN WITH BOTTOM NAV =====
        composable("main") {
            MainScreenWithBottomNav(
                navController = navController,
                authViewModel = authViewModel,
                homeViewModel = homeViewModel,
                profileViewModel = profileViewModel,
                quizViewModel = quizViewModel,
                leaderboardViewModel = leaderboardViewModel,
                yourQuizzesViewModel = yourQuizzesViewModel
            )
        }
    }
}

@Composable
private fun SplashGate(
    authState: AuthState,
    navigate: (String) -> Unit
) {
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) navigate("main")
        else navigate(Screen.LoginScreen.route)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MainScreenWithBottomNav(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    quizViewModel: QuizViewModel,
    leaderboardViewModel: LeaderboardViewModel,
    yourQuizzesViewModel: YourQuizzesViewModel
) {
    val bottomNavController = rememberNavController()
    val currentBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val selectedTab = when (currentRoute) {
        "home" -> BottomTab.Home
        "quizzes" -> BottomTab.Quizzes
        "leaderboard" -> BottomTab.Leaderboard
        "profile" -> BottomTab.Profile
        else -> BottomTab.Home
    }

    // ===== SAFE AREA =====
    val navInsetBottom = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    val barHeight = 86.dp
    val popupSpace = 46.dp
    val totalBottomPadding = barHeight + navInsetBottom

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(containerColor = Color.Transparent) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = totalBottomPadding)
            ) {
                NavHost(
                    navController = bottomNavController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        val authState by authViewModel.authState.collectAsState()
                        val state by homeViewModel.uiState.collectAsState()

                        LaunchedEffect(authState) {
                            if (authState is AuthState.Success) {
                                homeViewModel.loadHome((authState as AuthState.Success).userId)
                            }
                        }

                        if (state.isLoading) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                                searchError = state.searchError,

                                onHome = {},
                                onQuizzes = { bottomNavController.navigate("quizzes") },
                                onQR = { navController.navigate("qr_scan") }, // ✅ penting
                                onLeaderboard = { bottomNavController.navigate("leaderboard") },
                                onProfile = { bottomNavController.navigate("profile") },
                                onSettings = { navController.navigate(Screen.SettingScreen.route) },

                                onSearchQuizCode = { code ->
                                    homeViewModel.searchQuizByCode(code) { quizId ->
                                        navController.navigate("testInfo/$quizId")
                                    }
                                },

                                onCategorySeeAll = { navController.navigate("category") },
                                onCategoryClick = { categoryUi ->
                                    navController.navigate("categorySpecify/${categoryUi.id}/${categoryUi.name}")
                                },
                                onTrendingSeeAll = { navController.navigate("trending") },
                                onTrendingClick = { quizId -> navController.navigate("testInfo/$quizId") },
                                onTopAuthorsSeeAll = { navController.navigate("topAuthors") },
                                onYourQuizSeeAll = { bottomNavController.navigate("quizzes") },
                                onYourQuizClick = { quizId ->
                                    navController.navigate("answerExplanation/$quizId")
                                }
                            )
                        }
                    }

                    composable("quizzes") {
                        val authState by authViewModel.authState.collectAsState()
                        val userId = (authState as? AuthState.Success)?.userId ?: ""

                        YourQuizzesScreen(
                            userId = userId,
                            onBackClick = { bottomNavController.navigate("home") },
                            onResultClick = { quizId ->
                                navController.navigate("answerExplanation/$quizId")
                            },
                            viewModel = yourQuizzesViewModel
                        )
                    }

                    composable("leaderboard") {
                        LeaderboardScreen(
                            viewModel = leaderboardViewModel,
                            onSettings = { navController.navigate(Screen.SettingScreen.route) }
                        )
                    }

                    composable("profile") {
                        val authState by authViewModel.authState.collectAsState()
                        val userId = (authState as? AuthState.Success)?.userId ?: ""

                        LaunchedEffect(Unit) { profileViewModel.loadProfile() }

                        ProfileScreen(
                            viewModel = profileViewModel,
                            onBackClick = { bottomNavController.navigate("home") },
                            onNavigateToSettings = { navController.navigate(Screen.SettingScreen.route) },
                            onNavigateToQuizHistory = { navController.navigate("quizHistory/$userId") },
                            onNavigateToFollowers = { navController.navigate("followers/$userId") },
                            onNavigateToFollowing = { navController.navigate("following/$userId") },
                            onNavigateToHelpCenter = { navController.navigate("helpCenter") },
                            onLogout = {
                                authViewModel.logout()
                                navController.navigate(Screen.LoginScreen.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }

        // Bottom bar overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(barHeight + popupSpace + navInsetBottom)
                .background(Color.Transparent)
        ) {
            QuizBottomBar(
                selected = selectedTab,
                onSelected = { tab ->
                    val route = when (tab) {
                        BottomTab.Home -> "home"
                        BottomTab.Quizzes -> "quizzes"
                        BottomTab.Leaderboard -> "leaderboard"
                        BottomTab.Profile -> "profile"
                    }
                    bottomNavController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onQrClick = { navController.navigate("qr_scan") }, // ✅ penting
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

private val DeepBlue = Color(0xFF162471)
