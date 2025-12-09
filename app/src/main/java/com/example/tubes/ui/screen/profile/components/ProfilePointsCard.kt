package com.example.tubes.ui.screen.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tubes.ui.theme.TubesTheme

@Composable

fun ProfilePointsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0E1C6B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total : 5.000 Points",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                // Warna teks putih agar kontras dengan latar biru
                color = Color.White
            )

            Button(
                onClick = { /* TODO: Handle view click */ },
                colors = ButtonDefaults.buttonColors(
                    // Warna tombol putih agar kontras
                    containerColor = Color.White,
                    // Warna teks tombol biru tua agar serasi
                    contentColor = Color(0xFF0E1C6B)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "View",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePointsCardPreview() {
    TubesTheme {
        // Beri Box dengan padding untuk melihat preview komponen dengan lebih baik
        Box(modifier = Modifier.padding(16.dp)) {
            ProfilePointsCard()
        }
    }
}
