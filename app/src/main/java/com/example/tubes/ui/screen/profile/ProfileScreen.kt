package com.example.tubes.ui.screen.profile

// Import yang sudah dirapikan
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tubes.ui.screen.profile.components.ProfileHeader
import com.example.tubes.ui.screen.profile.components.ProfileOptions
import com.example.tubes.ui.screen.profile.components.ProfilePointsCard
import com.example.tubes.ui.screen.profile.components.ProfileTopBar
import com.example.tubes.ui.theme.TubesTheme

@Composable
fun ProfileScreen() {
    // Box utama untuk menumpuk TopBar di atas segalanya
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // Column untuk menumpuk header dan konten scroll
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0E1C6B))
            ) {
                ProfileHeader()
            }



            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    // Mengisi sisa ruang yang tersedia
                    .weight(1f)
                    // Beri background dengan warna custom
                    .background(Color(0xFFF0EEFE))

                    // Beri sudut melengkung HANYA di bagian atas
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(top = 36.dp)
            ) {
                // Item pertama: Kartu Poin
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
        ProfileScreen()
    }
}
