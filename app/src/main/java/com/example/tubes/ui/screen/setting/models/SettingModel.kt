package com.example.tubes.ui.screen.setting.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SettingModel(
    val title: String,
    val icon: Int,            // drawable resource
    val bgColor: Color,
    val onClick: () -> Unit
)
