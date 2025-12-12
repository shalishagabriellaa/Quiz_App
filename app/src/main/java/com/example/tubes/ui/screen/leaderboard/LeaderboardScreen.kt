package com.example.tubes.ui.screen.leaderboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

// Required imports
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val users: List<LeaderboardUser> = emptyList(),
    val error: String? = null
)

// Colors
private val DeepBlue = Color(0xFF162471)
private val LightBackground = Color(0xFFF5F5F5)

data class LeaderboardUser(
    val id: String,
    val name: String,
    val points: Int,
    val avatarUrl: String,
    val rank: Int
)

enum class LeaderboardTab {
    WEEKLY, ALL_TIME
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(LeaderboardTab.WEEKLY) }

    LaunchedEffect(selectedTab) {
        viewModel.loadLeaderboard(selectedTab)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Leaderboard",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Owl mascot icon placeholder
                    Icon(
                        painter = painterResource(android.R.drawable.ic_dialog_info),
                        contentDescription = "Mascot",
                        tint = Color(0xFF6EC6FF),
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlue
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(padding)
        ) {
            // Tab selector
            LeaderboardTabSelector(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(16.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DeepBlue)
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = Color.Red
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadLeaderboard(selectedTab) }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Top 3 podium
                        item {
                            TopThreePodium(
                                topUsers = uiState.users.take(3)
                            )
                        }

                        // Rest of the list
                        itemsIndexed(uiState.users.drop(3)) { index, user ->
                            LeaderboardItem(
                                user = user.copy(rank = index + 4),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Bottom spacing
                        item {
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardTabSelector(
    selectedTab: LeaderboardTab,
    onTabSelected: (LeaderboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TabButton(
            text = "Weekly",
            selected = selectedTab == LeaderboardTab.WEEKLY,
            onClick = { onTabSelected(LeaderboardTab.WEEKLY) },
            modifier = Modifier.weight(1f)
        )
        TabButton(
            text = "All Time",
            selected = selectedTab == LeaderboardTab.ALL_TIME,
            onClick = { onTabSelected(LeaderboardTab.ALL_TIME) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF2196F3) else Color.Transparent,
            contentColor = if (selected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (selected) 2.dp else 0.dp
        )
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun TopThreePodium(
    topUsers: List<LeaderboardUser>
) {
    if (topUsers.isEmpty()) return

    // Arrange as: 2nd, 1st, 3rd
    val arranged = listOf(
        topUsers.getOrNull(1),
        topUsers.getOrNull(0),
        topUsers.getOrNull(2)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        arranged.forEachIndexed { visualIndex, user ->
            if (user != null) {
                val actualRank = user.rank
                val podiumColor = when (actualRank) {
                    1 -> Color(0xFFFF6B9D) // Pink
                    2 -> Color(0xFF64B5F6) // Blue
                    3 -> Color(0xFFBA68C8) // Purple
                    else -> Color.Gray
                }
                val podiumHeight = when (actualRank) {
                    1 -> 200.dp
                    2 -> 160.dp
                    3 -> 140.dp
                    else -> 120.dp
                }

                PodiumCard(
                    user = user,
                    rank = actualRank,
                    color = podiumColor,
                    height = podiumHeight,
                    modifier = Modifier.weight(1f)
                )

                if (visualIndex < 2) {
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PodiumCard(
    user: LeaderboardUser,
    rank: Int,
    color: Color,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Crown for 1st place
        if (rank == 1) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = "Crown",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(4.dp))
        } else {
            Spacer(Modifier.height(36.dp))
        }

        // Avatar with rank badge
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = user.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            // Rank badge
            if (rank <= 3) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            when (rank) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> Color.Gray
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Medal",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Podium
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(height)
                .shadow(4.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(color, color.copy(alpha = 0.6f))
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = user.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${user.points} points",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun LeaderboardItem(
    user: LeaderboardUser,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank number
            Text(
                text = "${user.rank}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.width(32.dp)
            )

            Spacer(Modifier.width(12.dp))

            // Avatar
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = user.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(DeepBlue),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // Name and points
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "${user.points} points",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// ViewModel
class LeaderboardViewModel : androidx.lifecycle.ViewModel() {
    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    fun loadLeaderboard(tab: LeaderboardTab) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // TODO: Replace with actual API call
                // val users = repository.getLeaderboard(tab)

                delay(800)
                val mockUsers = listOf(
                    LeaderboardUser("1", "Davis Curtis", 2569, "https://via.placeholder.com/100", 1),
                    LeaderboardUser("2", "Alena Donin", 1469, "https://via.placeholder.com/100", 2),
                    LeaderboardUser("3", "Craig Gouse", 1053, "https://via.placeholder.com/100", 3),
                    LeaderboardUser("4", "Madelyn Dias", 590, "https://via.placeholder.com/100", 4),
                    LeaderboardUser("5", "Zain Vaccaro", 448, "https://via.placeholder.com/100", 5)
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    users = mockUsers
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}
