package com.example.tubes.ui.screen.teacher

import androidx.compose.runtime.Composable
import com.example.tubes.ui.screen.setting.ChangePasswordScreen
import com.example.tubes.viewmodel.ChangePasswordViewModel

@Composable
fun TeacherChangePasswordScreen(
    viewModel: ChangePasswordViewModel,
    onBack: () -> Unit = {},
    onLoggedOut: () -> Unit = {}
) {
    ChangePasswordScreen(
        viewModel = viewModel,
        onBack = onBack,
        onLoggedOut = onLoggedOut
    )
}
