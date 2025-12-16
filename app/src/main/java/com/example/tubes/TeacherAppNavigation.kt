package com.example.tubes

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tubes.data.AuthState
import com.example.tubes.data.cloudinary.CloudinaryManager
import com.example.tubes.data.repository.CloudinaryRepositoryImpl
import com.example.tubes.data.repository.TeacherAnalyticsRepositoryImpl
import com.example.tubes.data.repository.TeacherNotificationRepositoryImpl
import com.example.tubes.data.repository.TeacherProfileRepositoryImpl
import com.example.tubes.data.repository.TeacherQuestionBankRepositoryImpl
import com.example.tubes.data.repository.TeacherQuestionRepositoryImpl
import com.example.tubes.data.repository.TeacherQuizRepositoryImpl
import com.example.tubes.ui.screen.setting.AboutQuorriScreen
import com.example.tubes.ui.screen.teacher.QuizPreviewScreen
import com.example.tubes.ui.screen.teacher.TeacherAddQuestionScreen
import com.example.tubes.ui.screen.teacher.TeacherAnalyticsScreen
import com.example.tubes.ui.screen.teacher.TeacherCreateQuizScreen
import com.example.tubes.ui.screen.teacher.TeacherNotificationScreen
import com.example.tubes.ui.screen.teacher.TeacherProfileScreen
import com.example.tubes.ui.screen.teacher.TeacherQuestionBankScreen
import com.example.tubes.ui.screen.teacher.TeacherQuizListScreen
import com.example.tubes.ui.screen.teacher.TeacherQuizQrScreen
import com.example.tubes.ui.teacher.TeacherDashboard
import com.example.tubes.ui.teacher.TeacherViewAllScreen
import com.example.tubes.ui.teacher.TeacherViewAllType
import com.example.tubes.ui.teacher.components.TeacherBottomNavigation
import com.example.tubes.viewmodel.AuthViewModel
import com.example.tubes.viewmodel.TeacherAddQuestionViewModel
import com.example.tubes.viewmodel.TeacherAddQuestionViewModelFactory
import com.example.tubes.viewmodel.TeacherAnalyticsViewModel
import com.example.tubes.viewmodel.TeacherCreateQuizViewModel
import com.example.tubes.viewmodel.TeacherCreateQuizViewModelFactory
import com.example.tubes.viewmodel.TeacherNotificationViewModel
import com.example.tubes.viewmodel.TeacherNotificationViewModelFactory
import com.example.tubes.viewmodel.TeacherProfileViewModel
import com.example.tubes.viewmodel.TeacherProfileViewModelFactory
import com.example.tubes.viewmodel.TeacherQuestionBankViewModel
import com.example.tubes.viewmodel.TeacherQuestionBankViewModelFactory
import com.example.tubes.viewmodel.TeacherQuizListViewModel
import com.example.tubes.viewmodel.TeacherQuizListViewModelFactory
import com.example.tubes.viewmodel.TeacherQuizQrViewModel
import com.example.tubes.viewmodel.TeacherQuizQrViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.example.tubes.ui.screen.teacher.TeacherPersonalInfoScreen
import com.example.tubes.ui.screen.teacher.TeacherChangePasswordScreen
import com.example.tubes.viewmodel.PersonalInfoViewModel
import com.example.tubes.viewmodel.PersonalInfoViewModelFactory
import com.example.tubes.viewmodel.ChangePasswordViewModel
import com.example.tubes.viewmodel.ChangePasswordViewModelFactory
import com.example.tubes.ui.screen.teacher.TeacherChangePasswordScreen
import com.example.tubes.ui.screen.teacher.TeacherPersonalInfoScreen
@Composable
fun TeacherAppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authState by authViewModel.authState.collectAsState()
    val authorId = (authState as? AuthState.Success)?.userId

    // ✅ HIDE BOTTOM BAR untuk semua route yang "detail/fullscreen" (punya parameter juga)
    val shouldHideBottomBar = currentRoute?.let { route ->
        route.startsWith("quiz_create") ||
                route.startsWith("quiz_add_questions") ||
                route.startsWith("teacher_view_all") ||
                route.startsWith("teacher_quiz_qr")
    } ?: false

    Scaffold(
        bottomBar = {
            if (!shouldHideBottomBar) {
                TeacherBottomNavigation(
                    selectedRoute = currentRoute ?: TeacherRoute.Dashboard.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = TeacherRoute.Dashboard.route,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // ================= DASHBOARD =================
            composable(TeacherRoute.Dashboard.route) {
                TeacherDashboard(
                    authorId = authorId,
                    onOpenNotifications = {
                        navController.navigate(TeacherRoute.Notifications.route)
                    },
                    onViewAllAverage = {
                        navController.navigate(
                            TeacherRoute.ViewAll.createRoute(TeacherViewAllType.AVERAGE_SCORE.name)
                        )
                    },
                    onViewAllRecent = {
                        navController.navigate(
                            TeacherRoute.ViewAll.createRoute(TeacherViewAllType.RECENT_QUIZ.name)
                        )
                    },
                    onViewAllParticipants = {
                        navController.navigate(
                            TeacherRoute.ViewAll.createRoute(TeacherViewAllType.PARTICIPANTS.name)
                        )
                    }
                )
            }

            // ================= VIEW ALL (FETCH FROM DB) =================
            composable(
                route = TeacherRoute.ViewAll.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStackEntry ->

                val typeStr =
                    backStackEntry.arguments?.getString("type")
                        ?: TeacherViewAllType.RECENT_QUIZ.name

                val type = runCatching { TeacherViewAllType.valueOf(typeStr) }
                    .getOrElse { TeacherViewAllType.RECENT_QUIZ }

                val vm: com.example.tubes.viewmodel.TeacherViewAllViewModel =
                    viewModel(backStackEntry)

                val state by vm.uiState.collectAsState()

                LaunchedEffect(authorId, type) {
                    vm.load(authorId, type)
                }

                TeacherViewAllScreen(
                    type = type,
                    uiState = state,
                    onBack = { navController.popBackStack() }
                )
            }

            // ================= QUIZ LIST =================
            composable(TeacherRoute.Quizzes.route) { backStackEntry ->
                if (authorId != null) {
                    val repo = TeacherQuizRepositoryImpl(FirebaseFirestore.getInstance())
                    val viewModel: TeacherQuizListViewModel =
                        viewModel(backStackEntry, factory = TeacherQuizListViewModelFactory(repo))

                    TeacherQuizListScreen(
                        authorId = authorId,
                        viewModel = viewModel,
                        onAddQuizClick = { navController.navigate(TeacherRoute.QuizCreate.route) },
                        onEditQuizClick = { quizId ->
                            navController.navigate(TeacherRoute.QuizEdit.createRoute(quizId))
                        },
                        onGenerateQrClick = { quizId ->
                            navController.navigate(TeacherRoute.QuizQr.createRoute(quizId))
                        }
                    )
                }
            }

            // ================= ADD QUESTIONS =================
            composable(
                route = "quiz_add_questions/{quizId}/{totalQuestions}",
                arguments = listOf(
                    navArgument("quizId") { type = NavType.StringType },
                    navArgument("totalQuestions") { type = NavType.IntType }
                )
            ) { backStackEntry ->

                val quizId = backStackEntry.arguments!!.getString("quizId")!!
                val totalQuestions = backStackEntry.arguments!!.getInt("totalQuestions")

                val repo = TeacherQuestionRepositoryImpl(FirebaseFirestore.getInstance())

                val viewModel: TeacherAddQuestionViewModel =
                    viewModel(
                        backStackEntry,
                        factory = TeacherAddQuestionViewModelFactory(
                            quizId = quizId,
                            totalQuestions = totalQuestions,
                            repo = repo
                        )
                    )

                TeacherAddQuestionScreen(
                    viewModel = viewModel,
                    onFinished = { navController.popBackStack("quizzes", false) },
                    onPreview = { navController.navigate("quiz_preview") },
                    onBack = { navController.popBackStack() }
                )
            }

            // ================= QUIZ EDIT =================
            composable(
                route = TeacherRoute.QuizEdit.route,
                arguments = listOf(navArgument("quizId") { type = NavType.StringType })
            ) { backStackEntry ->
                val quizId = backStackEntry.arguments!!.getString("quizId")!!
                Log.d("EDIT_DEBUG", "EDIT ROUTE HIT quizId=$quizId")

                val cloudinaryManager = CloudinaryManager(
                    cloudName = BuildConfig.CLOUDINARY_NAME,
                    apiKey = BuildConfig.CLOUDINARY_API_KEY,
                    apiSecret = BuildConfig.CLOUDINARY_API_SECRET
                )

                val quizRepo = TeacherQuizRepositoryImpl(FirebaseFirestore.getInstance())
                val cloudinaryRepo = CloudinaryRepositoryImpl(cloudinaryManager)

                val viewModel: TeacherCreateQuizViewModel =
                    viewModel(
                        key = "TeacherCreateQuizViewModel_$quizId",
                        viewModelStoreOwner = backStackEntry,
                        factory = TeacherCreateQuizViewModelFactory(
                            quizRepo,
                            cloudinaryRepo,
                            quizId = quizId
                        )
                    )

                TeacherCreateQuizScreen(
                    authorId = authorId!!,
                    viewModel = viewModel,
                    onNavigateToAddQuestions = { qId, totalQuestions ->
                        navController.navigate(
                            TeacherRoute.QuizAddQuestions.createRoute(qId, totalQuestions)
                        )
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // ================= PREVIEW =================
            composable("quiz_preview") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("quiz_add_questions/{quizId}/{totalQuestions}")
                }
                val viewModel: TeacherAddQuestionViewModel = viewModel(parentEntry)

                QuizPreviewScreen(
                    questions = viewModel.previewQuestions,
                    onBack = { navController.popBackStack() }
                )
            }

            // ================= QR =================
            composable(
                route = TeacherRoute.QuizQr.route,
                arguments = listOf(navArgument("quizId") { type = NavType.StringType })
            ) { backStackEntry ->
                val quizId = backStackEntry.arguments!!.getString("quizId")!!
                val quizRepo = TeacherQuizRepositoryImpl(FirebaseFirestore.getInstance())
                val viewModel: TeacherQuizQrViewModel =
                    viewModel(backStackEntry, factory = TeacherQuizQrViewModelFactory(quizRepo))

                TeacherQuizQrScreen(
                    quizId = quizId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // ================= BANK =================
            composable(TeacherRoute.Bank.route) { backStackEntry ->
                if (authorId != null) {
                    val repo = TeacherQuestionBankRepositoryImpl(FirebaseFirestore.getInstance())
                    val viewModel: TeacherQuestionBankViewModel =
                        viewModel(
                            backStackEntry,
                            factory = TeacherQuestionBankViewModelFactory(
                                repository = repo,
                                authorId = authorId
                            )
                        )
                    TeacherQuestionBankScreen(viewModel = viewModel)
                }
            }

            // ================= CREATE QUIZ =================
            composable(TeacherRoute.QuizCreate.route) { backStackEntry ->
                if (authorId == null) return@composable

                val cloudinaryManager = CloudinaryManager(
                    cloudName = BuildConfig.CLOUDINARY_NAME,
                    apiKey = BuildConfig.CLOUDINARY_API_KEY,
                    apiSecret = BuildConfig.CLOUDINARY_API_SECRET
                )

                val quizRepo = TeacherQuizRepositoryImpl(FirebaseFirestore.getInstance())
                val cloudinaryRepo = CloudinaryRepositoryImpl(cloudinaryManager)

                val viewModel: TeacherCreateQuizViewModel =
                    viewModel(
                        backStackEntry,
                        factory = TeacherCreateQuizViewModelFactory(quizRepo, cloudinaryRepo)
                    )

                TeacherCreateQuizScreen(
                    authorId = authorId,
                    viewModel = viewModel,
                    onNavigateToAddQuestions = { quizId, totalQuestions ->
                        navController.navigate(
                            TeacherRoute.QuizAddQuestions.createRoute(quizId, totalQuestions)
                        )
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // ================= MONITORING =================
            composable(TeacherRoute.Monitoring.route) { backStackEntry ->
                if (authorId != null) {
                    val repo = remember { TeacherAnalyticsRepositoryImpl(FirebaseFirestore.getInstance()) }
                    val viewModel: TeacherAnalyticsViewModel =
                        viewModel {
                            TeacherAnalyticsViewModel(repository = repo, authorId = authorId)
                        }
                    TeacherAnalyticsScreen(viewModel = viewModel)
                }
            }

            // ================= PROFILE =================
            composable(TeacherRoute.Profile.route) { backStackEntry ->
                if (authorId == null) return@composable

                val repo = TeacherProfileRepositoryImpl(FirebaseFirestore.getInstance())
                val viewModel: TeacherProfileViewModel =
                    viewModel(
                        backStackEntry,
                        factory = TeacherProfileViewModelFactory(
                            repository = repo,
                            authorId = authorId
                        )
                    )

                TeacherProfileScreen(
                    viewModel = viewModel,
                    onPersonalInfo = { navController.navigate(TeacherRoute.PersonalInfo.route) },
                    onChangePassword = { navController.navigate(TeacherRoute.ChangePassword.route) },
                    onAboutQuorri = { navController.navigate(TeacherRoute.AboutQuorri.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // ================= TEACHER PERSONAL INFO =================
            composable(TeacherRoute.PersonalInfo.route) { backStackEntry ->
                val context = LocalContext.current

                val vm: PersonalInfoViewModel =
                    viewModel(
                        backStackEntry,
                        factory = PersonalInfoViewModelFactory(context)
                    )

                TeacherPersonalInfoScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }

// ================= TEACHER CHANGE PASSWORD =================
            composable(TeacherRoute.ChangePassword.route) { backStackEntry ->
                val vm: ChangePasswordViewModel =
                    viewModel(
                        backStackEntry,
                        factory = ChangePasswordViewModelFactory()
                    )

                TeacherChangePasswordScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onLoggedOut = {
                        // ✅ pastikan authViewModel juga logout (bukan cuma FirebaseAuth.signOut di ChangePasswordVM)
                        authViewModel.logout()

                        // ✅ balik ke dashboard / atau nanti bisa arahkan ke login screen kamu
                        navController.navigate(TeacherRoute.Dashboard.route) {
                            popUpTo(TeacherRoute.Dashboard.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

// ================= ABOUT QUORRI =================
            composable(TeacherRoute.AboutQuorri.route) {
                AboutQuorriScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // ================= NOTIFICATIONS =================
            composable(TeacherRoute.Notifications.route) { backStackEntry ->
                if (authorId == null) return@composable

                val repo = TeacherNotificationRepositoryImpl(FirebaseFirestore.getInstance())
                val viewModel: TeacherNotificationViewModel =
                    viewModel(
                        backStackEntry,
                        factory = TeacherNotificationViewModelFactory(
                            repository = repo,
                            userId = authorId
                        )
                    )

                TeacherNotificationScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun TeacherQuestionBankScreen(viewModel: TeacherQuestionBankViewModel) {
    TODO("Not yet implemented")
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "$title Screen", color = Color.Black)
    }
}
