package com.example.tubes.ui.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tubes.R
import com.example.tubes.ui.screen.setting.components.SettingHeader
import com.example.tubes.ui.screen.setting.components.SettingItem

@Composable
fun SettingScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {

    Column(modifier = Modifier.fillMaxSize()) {

        // Header Bagian Atas
        SettingHeader(onBack = onBack)

        // Isi List Setting
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                SettingItem(
                    title = "Personal Info",
                    iconRes = R.drawable.profile_icon,
                    bgColor = Color(0xFFFBDCA0)
                )
            }

            item {
                SettingItem(
                    title = "Notification",
                    iconRes = R.drawable.notif_icon,
                    bgColor = Color(0xFFF8B5B5)
                )
            }

            item {
                SettingItem(
                    title = "Sound & Effects",
                    iconRes = R.drawable.sound_icon,
                    bgColor = Color(0xFFE2D4FF)
                )
            }

            item {
                SettingItem(
                    title = "Security",
                    iconRes = R.drawable.security_icon,
                    bgColor = Color(0xFFCFF6D5)
                )
            }

            item {
                SettingItem(
                    title = "Help Center",
                    iconRes = R.drawable.help_icon,
                    bgColor = Color(0xFFD1E7FF)
                )
            }

            item {
                SettingItem(
                    title = "About Quorri",
                    iconRes = R.drawable.about_icon,
                    bgColor = Color(0xFFF5F2A7)
                )
            }

            item {
                SettingItem(
                    title = "Logout",
                    iconRes = R.drawable.logout_icon,
                    bgColor = Color(0xFFF8B5B5),
                    textColor = Color.Red,
                    onClick = onLogout
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingScreenPreview() {
    SettingScreen()
}

