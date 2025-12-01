package com.example.tubes.ui.screen.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.R
import com.example.tubes.ui.theme.TubesTheme

@Composable
fun ProfileHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp), // Beri padding atas dan bawah
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto Profil
        Image(
            painter = painterResource(id = R.drawable.profile_avatar),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nama Pengguna
        Text(
            text = "Janiskyoo",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Email Pengguna
        Text(
            text = "@janiskyowl8@gmail.com",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly, // Agar pembagian ruang rata
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item Statistik: Plays
            StatisticItem(count = "100", label = "Plays")

            // Garis Pemisah Vertikal
            Divider(
                modifier = Modifier
                    .height(30.dp) // Atur tinggi garis
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.5f) // Warna garis putih transparan
            )

            // Item Statistik: Followers
            StatisticItem(count = "500", label = "Followers")

            // Garis Pemisah Vertikal
            Divider(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.5f)
            )

            // Item Statistik: Following
            StatisticItem(count = "500", label = "Following")
        }
    }
}

@Composable
fun StatisticItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF0E1C6B)
@Composable
private fun ProfileHeaderPreview() {
    TubesTheme {
        ProfileHeader()
    }
}
