package com.example.tubes.ui.screen.teacher

import androidx.compose.runtime.Composable
import com.example.tubes.ui.screen.setting.PersonalInfoScreen
import com.example.tubes.viewmodel.PersonalInfoViewModel

@Composable
fun TeacherPersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    onBack: () -> Unit = {}
) {
    // reuse UI user (biar konsisten)
    PersonalInfoScreen(viewModel = viewModel, onBack = onBack)
}
