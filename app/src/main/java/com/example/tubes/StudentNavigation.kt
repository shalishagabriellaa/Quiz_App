package com.example.tubes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.tubes.ui.screen.setting.AboutQuorriScreen
import com.example.tubes.ui.screen.setting.ChangePasswordScreen
import com.example.tubes.ui.screen.setting.HelpArticleDetailScreen
import com.example.tubes.ui.screen.setting.HelpCenterScreen
import com.example.tubes.ui.screen.setting.PersonalInfoScreen
import com.example.tubes.viewmodel.*
import com.example.tubes.ui.screen.setting.SettingScreen

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

    // 🆕 ViewModels untuk Followers & Following
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

    // startDestination pakai "splash" supaya authState kebaca dulu
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // ===== SPLASH / AUTH GATE =====
        composable("splash") {
            SplashGate(authState = authState) { route ->
                navController.navigate(route) {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        // ===== LOGIN =====
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateRegister = {
                    navController.navigate(Screen.RegisterScreen.route)
                },
                onForgotPassword = { // 👈 baru
                    navController.navigate(Screen.ForgotPasswordScreen.route)
                },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ===== REGISTER =====
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

        // === FORGOT PASSWORD ====
        composable(Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }


        // ===== SETTINGS =====
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

        // 🆕 ===== PERSONAL INFO =====
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

        // 🆕 ===== CHANGE PASSWORD =====
        composable("changePassword") {
            val vm: com.example.tubes.viewmodel.ChangePasswordViewModel = viewModel(
                factory = com.example.tubes.viewmodel.ChangePasswordViewModelFactory()
            )

            ChangePasswordScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    // ✅ balikin ke Login dan hapus semua backstack
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 🆕 ===== ABOUT QUORRI =====
        composable("aboutQuorri") {
            AboutQuorriScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ===== QUIZ SCREEN =====
        composable(Screen.QuizScreen.route + "/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            val userId = (authState as? AuthState.Success)?.userId

            QuizScreen(
                quizId = quizId,
                viewModel = quizViewModel,
                onBackClick = { navController.popBackStack() },
                onQuizComplete = { navController.popBackStack() },
                onViewExplanation = { qId ->
                    // ✅ kirim userId juga
                    navController.navigate("answerExplanation/$qId")
                },
                userId = userId
            )
        }

        // ===== ANSWER EXPLANATION =====
        composable("answerExplanation/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            val userId = (authState as? AuthState.Success)?.userId ?: ""

            AnswerExplanationScreen(
                quizId = quizId,
                userId = userId,
                onBackClick = { navController.popBackStack() } // ✅ balik Result
            )
        }

        // ===== LIST CATEGORY =====
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

        // ===== CATEGORY SPECIFY =====
        composable("categorySpecify/{categoryId}/{categoryName}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""

            CategorySpecifyScreen(
                categoryId = categoryId,
                categoryName = categoryName,
                viewModel = categorySpecifyViewModel,
                onBackClick = { navController.popBackStack() },
                onQuizClick = { quizId -> navController.navigate("testInfo/$quizId") }
            )
        }

        // ===== TEST INFORMATION =====
        composable("testInfo/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            TestInformationScreen(
                quizId = quizId,
                viewModel = testInfoViewModel,
                onBackClick = { navController.popBackStack() },
                onStartQuiz = { navController.navigate(Screen.QuizScreen.route + "/$quizId") }
            )
        }

        // ===== TRENDING FULL LIST =====
        composable("trending") {
            val state by homeViewModel.uiState.collectAsState()
            TrendingScreen(
                trending = state.trendingUi,
                onBackClick = { navController.popBackStack() },
                onQuizClick = { quizId -> navController.navigate("testInfo/$quizId") }
            )
        }

        // ===== TOP AUTHORS FULL LIST =====
        composable("topAuthors") {
            val state by homeViewModel.uiState.collectAsState()
            TopAuthorsScreen(
                authors = state.topAuthors.map { it.toAuthorUi() },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 🆕 ===== FOLLOWERS SCREEN =====
        composable("followers/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            FollowersScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onUserClick = { targetUserId ->
                    // TODO: Navigate to user profile if needed
                    // navController.navigate("userProfile/$targetUserId")
                },
                viewModel = followersViewModel
            )
        }

        // 🆕 ===== FOLLOWING SCREEN =====
        composable("following/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            FollowingScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onUserClick = { targetUserId ->
                    // TODO: Navigate to user profile if needed
                    // navController.navigate("userProfile/$targetUserId")
                },
                viewModel = followingViewModel
            )
        }

        // 🆕 ===== QUIZ HISTORY SCREEN (from Profile) =====
        composable("quizHistory/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            YourQuizzesScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onResultClick = { quizId ->
                    navController.navigate("testInfo/$quizId")
                },
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
            UserNotificationScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
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

    var showQrScanner by remember { mutableStateOf(false) }

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

        /* =========================================================
           LAYER 1 — CONTENT (NAVHOST)
           ========================================================= */
        Scaffold(
            containerColor = Color.Transparent
        ) { innerPadding ->
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

                    // Di StudentNavigation.kt, di dalam MainScreenWithBottomNav
// Ubah composable("home") menjadi seperti ini:

                    composable("home") {
                        val authState by authViewModel.authState.collectAsState()
                        val state by homeViewModel.uiState.collectAsState()

                        LaunchedEffect(authState) {
                            if (authState is AuthState.Success) {
                                homeViewModel.loadHome(
                                    (authState as AuthState.Success).userId
                                )
                            }
                        }

                        if (state.isLoading) {
                            Box(
                                Modifier.fillMaxSize(),
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
                                searchError = state.searchError, // 🆕 Pass searchError dari state
                                onHome = {},
                                onQuizzes = { bottomNavController.navigate("quizzes") },
                                onQR = { showQrScanner = true },
                                onLeaderboard = { bottomNavController.navigate("leaderboard") },
                                onProfile = { bottomNavController.navigate("profile") },
                                onSettings = { navController.navigate(Screen.SettingScreen.route) },
                                onSearchQuizCode = { code ->
                                    homeViewModel.searchQuizByCode(code) { quizId ->
                                        navController.navigate("testInfo/$quizId") // 🔥 Langsung ke testInfo
                                    }
                                },
                                onCategorySeeAll = { navController.navigate("category") },
                                onCategoryClick = { categoryUi ->
                                    navController.navigate(
                                        "categorySpecify/${categoryUi.id}/${categoryUi.name}"
                                    )
                                },
                                onTrendingSeeAll = { navController.navigate("trending") },
                                onTrendingClick = { quizId ->
                                    navController.navigate("testInfo/$quizId")
                                },
                                onTopAuthorsSeeAll = { navController.navigate("topAuthors") },
                                onYourQuizSeeAll = { bottomNavController.navigate("quizzes") },
                                onYourQuizClick = { quizId -> navController.navigate("answerExplanation/$quizId") }
                            )
                        }
                    }

                    composable("quizzes") {
                        val authState by authViewModel.authState.collectAsState()
                        val userId = (authState as? AuthState.Success)?.userId ?: ""

                        YourQuizzesScreen(
                            userId = userId,
                            onBackClick = { bottomNavController.navigate("home") },
                            onResultClick = { quizId -> navController.navigate("answerExplanation/$quizId") },
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

                        LaunchedEffect(Unit) {
                            profileViewModel.loadProfile()
                        }

                        ProfileScreen(
                            viewModel = profileViewModel,
                            onBackClick = { bottomNavController.navigate("home") },
                            onNavigateToSettings = { navController.navigate(Screen.SettingScreen.route) },
                            onNavigateToQuizHistory = { navController.navigate("quizHistory/$userId") },
                            onNavigateToFollowers = { navController.navigate("followers/$userId") },
                            onNavigateToFollowing = { navController.navigate("following/$userId") },
                            onNavigateToHelpCenter = { navController.navigate("helpCenter") }, // ✅ ini
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

        /* =========================================================
           LAYER 2 — BOTTOM BAR (OVERLAY)
           ========================================================= */
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
                onQrClick = {
                    navController.navigate("qr_scan")
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            QrFab(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navInsetBottom + (barHeight / 2)),
                onClick = { showQrScanner = true }
            )
        }

        /* =========================================================
           QR SCANNER OVERLAY
           ========================================================= */
        if (showQrScanner) {
            QrScannerOverlay(
                onDismiss = { showQrScanner = false },
                onCodeScanned = { code: String ->
                    showQrScanner = false
                    homeViewModel.searchQuizByCode(code) { quizId ->
                        navController.navigate("testInfo/$quizId")
                    }
                }
            )
        }
    }
}

@Composable
private fun QrFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val lift = if (pressed) 6.dp else 0.dp

    Box(
        modifier = modifier
            .offset(y = -lift)
            .size(68.dp)
            .shadow(14.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(Color.White)
            .border(width = 4.dp, color = DeepBlue, shape = CircleShape)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.QrCode,
            contentDescription = "QR",
            tint = DeepBlue,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun QrScannerOverlay(
    onDismiss: () -> Unit,
    onCodeScanned: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.9f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "QR Scanner",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(16.dp))

                Button(onClick = onDismiss) {
                    Text("Close")
                }

                Spacer(Modifier.height(16.dp))

                // simulasi scan (untuk test)
                Button(onClick = { onCodeScanned("DUMMY_CODE") }) {
                    Text("Simulate Scan")
                }
            }
        }
    }
}

private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)
