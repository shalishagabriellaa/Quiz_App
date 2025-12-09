package com.example.tubes.ui.screen.profile.models


import androidx.annotation.DrawableRes

data class ProfileOption(
    @DrawableRes val iconResId: Int, // Resource ID untuk ikon
    val text: String,
    val route: String // Digunakan untuk navigasi ke layar tujuan
)
