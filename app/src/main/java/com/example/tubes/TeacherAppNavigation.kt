package com.example.tubes

import android.util.Log
import android.widget.Toast
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
import com.example.tubes.data.repository.TeacherQuestionBankRepositoryImpl
import com.example.tubes.data.repository.TeacherQuestionRepositoryImpl
import com.example.tubes.data.repository.TeacherQuizRepositoryImpl
import com.example.tubes.ui.screen.teacher.QuizPreviewScreen
import com.example.tubes.ui.screen.teacher.TeacherAddQuestionScreen
import com.example.tubes.ui.screen.teacher.TeacherCreateQuizScreen
import com.example.tubes.ui.screen.teacher.TeacherQuestionBankScreen
import com.example.tubes.ui.screen.teacher.TeacherQuizListScreen
import com.example.tubes.ui.teacher.TeacherDashboard
import com.example.tubes.ui.teacher.components.TeacherBottomNavigation
import com.example.tubes.viewmodel.AuthViewModel
import com.example.tubes.viewmodel.TeacherAddQuestionViewModel
import com.example.tubes.viewmodel.TeacherAddQuestionViewModelFactory
import com.example.tubes.viewmodel.TeacherCreateQuizViewModel
import com.example.tubes.viewmodel.TeacherCreateQuizViewModelFactory
import com.example.tubes.viewmodel.TeacherQuestionBankViewModel
import com.example.tubes.viewmodel.TeacherQuestionBankViewModelFactory
import com.example.tubes.viewmodel.TeacherQuizListViewModel
import com.example.tubes.viewmodel.TeacherQuizListViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun TeacherAppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authState by authViewModel.authState.collectAsState()

    val authorId = (authState as? AuthState.Success)?.userId

    val hideBottomBarRoutes = listOf(
        TeacherRoute.QuizCreate.route,
        TeacherRoute.QuizAddQuestions.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
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

            // DASHBOARD
            composable(TeacherRoute.Dashboard.route) {
                TeacherDashboard(authorId = authorId)
            }

            // QUIZ LIST
            composable(TeacherRoute.Quizzes.route) { backStackEntry ->
                if (authorId != null) {

                    val repo = TeacherQuizRepositoryImpl(
                        FirebaseFirestore.getInstance()
                    )

                    val viewModel: TeacherQuizListViewModel =
                        viewModel(
                            backStackEntry,
                            factory = TeacherQuizListViewModelFactory(repo)
                        )

                    TeacherQuizListScreen(
                        authorId = authorId,
                        viewModel = viewModel,
                        onAddQuizClick = {
                            navController.navigate(TeacherRoute.QuizCreate.route)
                        },
                        onEditQuizClick = { quizId ->
                            navController.navigate(
                                TeacherRoute.QuizEdit.createRoute(quizId)
                            )
                        }
                    )
                }
            }

            // CREATE QUIZ
            composable(TeacherRoute.QuizCreate.route) { backStackEntry ->

                val cloudinaryManager = CloudinaryManager(
                    cloudName = BuildConfig.CLOUDINARY_NAME,
                    apiKey = BuildConfig.CLOUDINARY_API_KEY,
                    apiSecret = BuildConfig.CLOUDINARY_API_SECRET
                )

                val quizRepo = TeacherQuizRepositoryImpl(
                    FirebaseFirestore.getInstance()
                )

                val cloudinaryRepo =
                    CloudinaryRepositoryImpl(cloudinaryManager)

                val viewModel: TeacherCreateQuizViewModel =
                    viewModel(
                        backStackEntry,
                        factory = TeacherCreateQuizViewModelFactory(
                            quizRepo,
                            cloudinaryRepo
                        )
                    )

                TeacherCreateQuizScreen(
                    authorId = authorId!!,
                    viewModel = viewModel,
                    onNavigateToAddQuestions = { quizId, totalQuestions ->
                        navController.navigate(
                            TeacherRoute.QuizAddQuestions.createRoute(
                                quizId,
                                totalQuestions
                            )
                        )
                    }
                )
            }

            // ADD QUESTIONS
            composable(
                route = "quiz_add_questions/{quizId}/{totalQuestions}",
                arguments = listOf(
                    navArgument("quizId") { type = NavType.StringType },
                    navArgument("totalQuestions") { type = NavType.IntType }
                )
            ) { backStackEntry ->

                val quizId =
                    backStackEntry.arguments!!.getString("quizId")!!
                val totalQuestions =
                    backStackEntry.arguments!!.getInt("totalQuestions")

                val repo = TeacherQuestionRepositoryImpl(
                    FirebaseFirestore.getInstance()
                )

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
                    onFinished = {
                        navController.popBackStack("quizzes", false)
                    },
                    onPreview = {
                        navController.navigate("quiz_preview")
                    }
                )
            }

            composable(
                route = TeacherRoute.QuizEdit.route,
                arguments = listOf(
                    navArgument("quizId") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val quizId = backStackEntry.arguments!!.getString("quizId")!!
                Log.d("EDIT_DEBUG", "EDIT ROUTE HIT quizId=$quizId")
                val cloudinaryManager = CloudinaryManager(
                    cloudName = BuildConfig.CLOUDINARY_NAME,
                    apiKey = BuildConfig.CLOUDINARY_API_KEY,
                    apiSecret = BuildConfig.CLOUDINARY_API_SECRET
                )

                val quizRepo = TeacherQuizRepositoryImpl(
                    FirebaseFirestore.getInstance()
                )

                val cloudinaryRepo =
                    CloudinaryRepositoryImpl(cloudinaryManager)

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
                            TeacherRoute.QuizAddQuestions.createRoute(
                                qId,
                                totalQuestions
                            )
                        )
                    }
                )
            }

            composable("quiz_preview") { backStackEntry ->

                // 🔑 AMBIL BACKSTACK ENTRY DARI ADD QUESTION
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(
                        "quiz_add_questions/{quizId}/{totalQuestions}"
                    )
                }

                val viewModel: TeacherAddQuestionViewModel =
                    viewModel(parentEntry)

                QuizPreviewScreen(
                    questions = viewModel.previewQuestions,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }




            // OTHERS
            composable(TeacherRoute.Bank.route) { backStackEntry ->

                if (authorId != null) {

                    val repo = TeacherQuestionBankRepositoryImpl(
                        FirebaseFirestore.getInstance()
                    )

                    val viewModel: TeacherQuestionBankViewModel =
                        viewModel(
                            backStackEntry,
                            factory = TeacherQuestionBankViewModelFactory(
                                repository = repo,
                                authorId = authorId
                            )
                        )

                    TeacherQuestionBankScreen(
                        viewModel = viewModel
                    )
                }
            }

            composable(TeacherRoute.Monitoring.route) {
                PlaceholderScreen("Monitoring")
            }
            composable(TeacherRoute.Profile.route) {
                PlaceholderScreen("Profile")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title Screen",
            color = Color.Black
        )
    }
}
