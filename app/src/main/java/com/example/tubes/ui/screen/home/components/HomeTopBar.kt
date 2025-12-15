package com.example.tubes.ui.screen.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubes.R

@Composable
fun HomeTopBar(
    userName: String? = null,
    modifier: Modifier = Modifier,
    onSettings: () -> Unit = {},
    onSearch: (String) -> Unit = {},
    searchError: String? = null // 🆕 Tambahkan parameter error
) {
    var query by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 🆕 Function untuk handle search
    val performSearch = {
        if (query.isNotBlank()) {
            keyboardController?.hide()
            onSearch(query.trim())
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.topbar_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {

            // ============================================
            // 🔥 TOP ROW (Settings - Welcome - Owl)
            // ============================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // SETTINGS — kiri
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }

                Spacer(Modifier.weight(1f))

                // 🔥 Welcome text tepat DI TENGAH baris
                Text(
                    text = "Welcome, ${userName ?: "Guest"}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                )

                Spacer(Modifier.weight(1f))

                // OWL ICON — kanan
                Image(
                    painter = painterResource(id = R.drawable.ic_owl),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(36.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(20.dp))

            // ============================================
            // SEARCH BAR
            // ============================================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(100))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFE6E0F3).copy(alpha = 0.20f),
                                    Color(0xFF9AA0D4).copy(alpha = 0.35f)
                                )
                            )
                        )
                        .padding(start = 16.dp, end = 64.dp),
                    contentAlignment = Alignment.CenterStart
                ) {

                    BasicTextField(
                        value = query,
                        onValueChange = {
                            query = it
                        },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { performSearch() }
                        )
                    )

                    if (query.isEmpty()) {
                        Text(
                            text = "Enter the quiz code...",
                            color = Color.White.copy(0.9f),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF6D6ADB), Color(0xFF4F4AA1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = performSearch) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                }
            }

            // 🆕 Error message
            if (searchError != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = searchError,
                    color = Color(0xFFFFCDD2),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}
