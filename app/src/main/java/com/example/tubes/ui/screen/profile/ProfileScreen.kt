package com.example.tubes.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // Import 'getValue'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Import yang benar
import com.example.tubes.ui.screen.profile.components.ProfileHeader
import com.example.tubes.ui.screen.profile.components.ProfileOptions
import com.example.tubes.ui.screen.profile.components.ProfilePointsCard
import com.example.tubes.ui.screen.profile.components.ProfileTopBar
import com.example.tubes.ui.theme.TubesTheme
import com.example.tubes.viewmodel.ProfileViewModel // Import ViewModel
import com.example.tubes.viewmodel.ProfileViewModelFactory // Import Factory kita

@Composable
fun ProfileScreen(

    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Tampilkan loading indicator
            }
        }
        uiState.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.error ?: "Terjadi kesalahan")
            }
        }
        else -> {
            ProfileContent(
                userName = uiState.userName,
                userEmail = uiState.userEmail
            )
        }
    }
}
@Composable
private fun ProfileContent(
    userName: String,
    userEmail: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0E1C6B))
            ) {
                ProfileHeader(
                    name = userName,
                    email = userEmail
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF0EEFE))
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(top = 36.dp)
            ) {
                item {
                    ProfilePointsCard()
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    ProfileOptions()
                }
            }
        }

        // TopBar diletakkan di lapisan paling atas, selalu terlihat
        ProfileTopBar(
            onNotificationClick = { /* TODO: Aksi saat notif diklik */ },
            onEditClick = { /* TODO: Aksi saat edit diklik */ },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreen() {
    TubesTheme {
        ProfileContent(userName = "John Doe", userEmail = "john.doe@example.com")
    }
}

