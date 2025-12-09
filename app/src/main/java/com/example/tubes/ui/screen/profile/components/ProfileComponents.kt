package com.example.tubes.ui.screen.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val AccentPurple = Color(0xFF6A5ACD) // Contoh warna ungu untuk kartu

@Composable
fun TotalPointsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AccentPurple), // Ganti dengan warna tema Anda
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total : 5.000 Points",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = { /* Handle View Click */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("View", color = AccentPurple, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)

@Composable
fun ProfileMenu(items: List<ProfileMenuItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items.forEach { item ->
            MenuItemRow(item = item)
            Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp) // Pembatas
        }
    }
}

@Composable
fun MenuItemRow(item: ProfileMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon (Ganti Icon sesuai kebutuhan/desain)
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = AccentPurple, // Contoh warna icon
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.width(16.dp))

        // Title
        Text(
            text = item.title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f) // Membuat teks mengambil sisa ruang
        )

        // Arrow Icon
        Icon(
            imageVector = Icons.Default.ArrowForwardIos, // Ganti dengan ikon > yang lebih kecil jika perlu
            contentDescription = "Go",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}