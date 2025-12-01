package com.example.tubes.ui.screen.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tubes.R
import com.example.tubes.ui.screen.profile.models.ProfileOption
import com.example.tubes.ui.theme.TubesTheme

private val profileOptionsList = listOf(
    ProfileOption(iconResId = R.drawable.uil_setting, text = "Settings", route = "settings"),
    ProfileOption(iconResId = R.drawable.wpf_statistics, text = "My Statistic", route = "statistic"),
    ProfileOption(iconResId = R.drawable.mdi_recent, text = "View quiz history", route = "history"),
    ProfileOption(iconResId = R.drawable.uil_invite_friend, text = "Invite A Friend", route = "invite"),
    ProfileOption(iconResId = R.drawable.uil_help, text = "Help Center", route = "help")
)

@Composable
fun ProfileOptions(modifier: Modifier = Modifier) {
    Column(
        // Gunakan modifier yang di-pass dari luar (misal dari ProfileScreen)
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface, // Warna putih
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            )
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            )
            .padding(top = 8.dp) // Beri sedikit jarak di atas item pertama
    ) {
        profileOptionsList.forEachIndexed { index, option ->
            ProfileOptionItem(option = option)

            if (index < profileOptionsList.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
fun ProfileOptionItem(option: ProfileOption) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp) // Ukuran kotak latar belakang
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest, // Warna abu-abu terang
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = option.iconResId),
                contentDescription = option.text,
                modifier = Modifier.size(24.dp),
                // Gunakan warna yang sesuai. Jika ikonnya adalah 'mdi_recent', warnai oranye.
                tint = if (option.route == "history") Color(0xFFE59400) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = option.text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0E1C6B)
@Composable
private fun ProfileOptionsPreview() {
    TubesTheme {
        Column {
            // Dummy header untuk melihat bagaimana sudutnya menyatu
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF0E1C6B)))
            // Dummy points card
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFF0E1C6B)))

            // Sekarang pemanggilan ini valid karena ProfileOptions sudah punya nilai default
            ProfileOptions()
        }
    }
}
