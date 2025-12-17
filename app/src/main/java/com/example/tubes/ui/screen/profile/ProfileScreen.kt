package com.example.tubes.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tubes.viewmodel.ProfileViewModel
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest

// Colors
private val DeepBlue = Color(0xFF0E1C6B)
private val LightBackground = Color(0xFFF5F5F5)
private val CardBackground = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToQuizHistory: () -> Unit = {},
    onNavigateToFollowers: () -> Unit = {},
    onNavigateToFollowing: () -> Unit = {},
    onNavigateToHelpCenter: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // ✅ logout dialog state
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    // ✅ Popup konfirmasi logout (sama vibe kayak SettingScreen)
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFF44336)
                )
            },
            title = {
                Text(
                    text = "Logout Confirmation",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out from your account?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(
                        "Logout",
                        color = Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        // topBar kamu memang dikomen, jadi aku biarin
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = DeepBlue
                    )
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${uiState.error}", color = Color.Red)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProfile() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {

                        // HEADER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DeepBlue)
                        ) {
                            ProfileHeader(
                                userName = uiState.userName,
                                userEmail = uiState.userEmail,
                                avatarUrl = uiState.avatarUrl,
                                followers = uiState.followersCount,
                                following = uiState.followingCount,
                                onFollowersClick = onNavigateToFollowers,
                                onFollowingClick = onNavigateToFollowing
                            )
                        }

                        // CONTENT
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(LightBackground)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(
                                top = 24.dp,
                                start = 20.dp,
                                end = 20.dp,
                                bottom = 100.dp
                            )
                        ) {
                            item {
                                TotalPointsCard(totalPoints = uiState.totalPoints)
                            }

                            item { Spacer(Modifier.height(12.dp)) }

                            item {
                                MenuItemCard(
                                    icon = Icons.Filled.Notifications,
                                    title = "Notifications",
                                    onClick = onNavigateToNotifications
                                )
                            }
                            item { Spacer(Modifier.height(12.dp)) }

                            item {
                                MenuItemCard(
                                    icon = Icons.Filled.Settings,
                                    title = "Settings",
                                    onClick = onNavigateToSettings
                                )
                            }

                            item { Spacer(Modifier.height(12.dp)) }

                            item {
                                MenuItemCard(
                                    icon = Icons.Filled.History,
                                    title = "View quiz history",
                                    onClick = onNavigateToQuizHistory
                                )
                            }

                            item { Spacer(Modifier.height(12.dp)) }

                            // ✅ Help Center sekarang jalan
                            item {
                                MenuItemCard(
                                    icon = Icons.Filled.Help,
                                    title = "Help Center",
                                    onClick = onNavigateToHelpCenter
                                )
                            }

                            item { Spacer(Modifier.height(32.dp)) }

                            // ✅ Logout sekarang pakai dialog
                            item {
                                Button(
                                    onClick = { showLogoutDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFEF5350)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(vertical = 16.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Logout,
                                        contentDescription = "Logout",
                                        tint = Color.White
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Logout",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            item { Spacer(Modifier.height(20.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    avatarUrl: String,
    followers: Int,
    following: Int,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    val context = LocalContext.current

    val finalAvatarUrl = remember(avatarUrl) {
        avatarUrl.toCloudinaryAvatarUrl(
            size = 300,
            fallback = "https://via.placeholder.com/200"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(finalAvatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = userEmail,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(Modifier.height(24.dp))

//        Row(
//            modifier = Modifier.fillMaxWidth(0.6f),
//            horizontalArrangement = Arrangement.SpaceEvenly,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            StatItem(value = followers.toString(), label = "Followers", onClick = onFollowersClick)
//
//            Divider(
//                modifier = Modifier
//                    .width(1.dp)
//                    .height(40.dp),
//                color = Color.White.copy(alpha = 0.3f)
//            )
//
//            StatItem(value = following.toString(), label = "Following", onClick = onFollowingClick)
//        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(8.dp)
    ) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(4.dp))
        Text(text = label, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun TotalPointsCard(totalPoints: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total : ${totalPoints.formatPoints()} Points",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun MenuItemCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(getIconBackgroundColor(title)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = getIconColor(title),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Text(
                title,
                fontSize = 16.sp,
                color = Color(0xFF333333),
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun getIconBackgroundColor(title: String): Color = when (title) {
    "Settings" -> Color(0xFFE3F2FD)
    "View quiz history" -> Color(0xFFFFF3E0)
    "Help Center" -> Color(0xFFE8F5E9)
    else -> Color(0xFFF5F5F5)
}

private fun getIconColor(title: String): Color = when (title) {
    "Settings" -> Color(0xFF2196F3)
    "View quiz history" -> Color(0xFFFF9800)
    "Help Center" -> Color(0xFF4CAF50)
    else -> Color.Gray
}

private fun Int.formatPoints(): String = when {
    this >= 1000000 -> String.format("%.1fM", this / 1000.0).replace(".0", "")
    this >= 1000 -> String.format("%.1fK", this / 1000.0).replace(".0", "")
    else -> this.toString()
}

private fun String?.toCloudinaryAvatarUrl(
    size: Int = 300,
    fallback: String
): String {
    val raw = this?.trim().orEmpty()
    if (raw.isEmpty()) return fallback

    val httpsUrl = if (raw.startsWith("http://")) raw.replaceFirst("http://", "https://") else raw
    if (!httpsUrl.contains("res.cloudinary.com")) return httpsUrl

    return if (httpsUrl.contains("/image/upload/")) {
        httpsUrl.replace(
            "/image/upload/",
            "/image/upload/f_auto,q_auto,c_fill,g_face,w_${size},h_${size}/"
        )
    } else httpsUrl
}
