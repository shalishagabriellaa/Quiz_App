package com.example.tubes.ui.screen.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// Impor yang diperbaiki
import com.example.tubes.R
import com.example.tubes.ui.screen.profile.models.ProfileOption
import com.example.tubes.ui.theme.TubesTheme

@Composable
fun ProfileOptionItem(
    option: ProfileOption,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(option.route) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = option.iconResId),
            contentDescription = option.text, // Deskripsi untuk aksesibilitas
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = option.text,
            // SARAN: Gunakan gaya teks dari tema
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f) // Mendorong panah ke kanan
        )

        // Ikon panah di sebelah kanan
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null, // Ikon ini murni dekoratif
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Menambahkan Preview agar mudah dites
@Preview(showBackground = true)
@Composable
private fun ProfileOptionItemPreview() {
    TubesTheme {
        val sampleOption = ProfileOption(
            iconResId = R.drawable.uil_setting,
            text = "Settings",
            route = "settings"
        )
        ProfileOptionItem(option = sampleOption, onItemClick = { route -> })
    }
}
