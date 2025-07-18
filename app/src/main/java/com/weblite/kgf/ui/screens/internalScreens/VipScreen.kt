package com.weblite.kgf.ui.screens.internalScreens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import kotlin.math.abs

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.weblite.kgf.ui.components.AttractiveText
import kotlin.collections.find
import kotlin.collections.isNotEmpty
import kotlin.collections.minByOrNull
import kotlin.let

data class VipLevel(
    val level: Int,
    val requiredExp: Long,
    val currentExp: Long = 0,
    val isLocked: Boolean = true,
    val levelUpReward: Int,
    val monthlyReward: Int,
    val betRatio: String = "₹1=1EXP",
    val gradientColors: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    var selectedVipLevel by remember { mutableIntStateOf(1) }
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
    }
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFFFF6B35),
            darkIcons = false
        )
    }


    val vipLevels = remember {
        listOf(
            VipLevel(
                1, 3000, 0, true, 60, 30, "Bet ₹1=1EXP",
                listOf(Color(0xFF6B9BD1), Color(0xFF4A7BA7))
            ),
            VipLevel(
                2, 30000, 0, true, 180, 90, "Bet ₹1=1EXP",
                listOf(Color(0xFFE67E22), Color(0xFFD35400))
            ),
            VipLevel(
                3, 30000, 0, true, 300, 150, "Bet ₹1=1EXP",
                listOf(Color(0xFFE74C3C), Color(0xFFC0392B))
            ),
            VipLevel(
                4, 2000000, 0, true, 500, 250, "Bet ₹1=1EXP",
                listOf(Color(0xFF9B59B6), Color(0xFF8E44AD))
            ),
            VipLevel(
                5, 2000000, 0, true, 800, 400, "Bet ₹1=1EXP",
                listOf(Color(0xFF1ABC9C), Color(0xFF16A085))
            ),
            VipLevel(
                6, 2000000, 0, true, 1200, 600, "Bet ₹1=1EXP",
                listOf(Color(0xFFF39C12), Color(0xFFE67E22))
            ),
            VipLevel(
                7, 5000000, 0, true, 2000, 1000, "Bet ₹1=1EXP",
                listOf(Color(0xFF34495E), Color(0xFF2C3E50))
            ),
            VipLevel(
                8, 10000000, 0, true, 3500, 1750, "Bet ₹1=1EXP",
                listOf(Color(0xFFE91E63), Color(0xFFC2185B))
            ),
            VipLevel(
                9, 10000000, 0, true, 6000, 3000, "Bet ₹1=1EXP",
                listOf(Color(0xFF673AB7), Color(0xFF512DA8))
            ),
            VipLevel(
                10, 50000000, 0, true, 10000, 5000, "Bet ₹1=1EXP",
                listOf(Color(0xFFFFD700), Color(0xFFFFA000))
            )
        )
    }

    val selectedVip = vipLevels.find { it.level == selectedVipLevel } ?: vipLevels[0]

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color(0xFFFF6B35)).height(52.dp),
                windowInsets = WindowInsets(0),
                title = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        AttractiveText(
                            text = "VIP",
                            primaryColor = Color.White,
                            shadowColor = Color(0xFFee7b45),
                            fontSize = 24.sp,
                            strokeWidth = 1.5f,
                        )
                    }

                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B35)
                )
            )
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFE6E6FA))
                .verticalScroll(rememberScrollState())
        ) {
            // User Info Section
            UserInfoSection()

            // VIP Level Rewards Info
            VipRewardsInfo()

            // VIP Cards Horizontal Scroll
            VipCardsSection(
                vipLevels = vipLevels,
                selectedLevel = selectedVipLevel,
                onLevelSelected = { selectedVipLevel = it }
            )

            // Benefits Section
            BenefitsSection(selectedVip = selectedVip)

            // My Benefits Section
            MyBenefitsSection()
        }
    }
}

@Composable
fun UserInfoSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Orange section with profile info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6B35))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "UID = 6763043294",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Levels = 0.0",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Gradient section from orange to light purple
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF6B35), // Orange
                            Color(0xFFE6E6FA)  // Light purple
                        )
                    )
                )
                .padding(12.dp)
        ) {
            // Experience and Payout Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    title = "1432120 EXP",
                    subtitle = "My Experience",
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    title = "4 Days",
                    subtitle = "Payout time",
                    modifier = Modifier.weight(1f),
                    titleColor = Color.Black
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    titleColor: Color = Color(0xFFFF6B35)
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun VipRewardsInfo() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp) // Adjust the height as needed
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RectangleShape
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RectangleShape
    ) {
        Text(
            text = "VIP level rewards are settled at 2:00 am on the 1st of every month",
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun VipCardsSection(
    vipLevels: List<VipLevel>,
    selectedLevel: Int,
    onLevelSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    // Automatically update selected level based on scroll position
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val centerX = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.width / 2
                val centerItem = visibleItems.minByOrNull {
                    abs((it.offset + it.size / 2) - centerX)
                }
                centerItem?.let { item ->
                    val newLevel = vipLevels[item.index].level
                    if (newLevel != selectedLevel) {
                        onLevelSelected(newLevel)
                    }
                }
            }
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // reduced from 16.dp
        horizontalArrangement = Arrangement.spacedBy(0.dp), // no spacing between items
        contentPadding = PaddingValues(horizontal = 12.dp), // minimal padding
        state = listState
    ) {
        items(vipLevels) { vipLevel ->
            VipCard(
                vipLevel = vipLevel,
                isSelected = vipLevel.level == selectedLevel,
                onClick = { onLevelSelected(vipLevel.level) }
            )
        }
    }
}

@Composable
fun VipCard(
    vipLevel: VipLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Card(
        modifier = Modifier.padding(4.dp)
            .width(screenWidth - 24.dp) // Full width minus minimal margins
            .height(160.dp),
        shape = RectangleShape// reduced height
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(vipLevel.gradientColors)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp) // reduced from 16.dp
            ) {
                // Header with VIP level and lock status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "👑",
                            fontSize = 18.sp // reduced size
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // reduced spacing
                        Text(
                            text = "VIP${vipLevel.level}",
                            color = Color.White,
                            fontSize = 20.sp, // reduced size
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (vipLevel.isLocked) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    Color.Black.copy(alpha = 0.3f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp) // reduced padding
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp) // reduced size
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Locked",
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp)) // reduced spacing

                // Requirements text
                Text(
                    text = "Upgrading VIP${vipLevel.level} requires\n${vipLevel.requiredExp}EXP",
                    color = Color.White,
                    fontSize = 12.sp, // reduced size
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp)) // reduced spacing

                // Bet ratio
                Text(
                    text = vipLevel.betRatio,
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp) // reduced padding
                )

                Spacer(modifier = Modifier.weight(1f))

                // Progress bar
                Column {
                    LinearProgressIndicator(
                        progress = { vipLevel.currentExp.toFloat() / vipLevel.requiredExp.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp) // reduced height
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(2.dp)) // reduced spacing

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${vipLevel.currentExp} / ${vipLevel.requiredExp}",
                            color = Color.White,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "${vipLevel.requiredExp} EXP can be leveled up",
                            color = Color.White,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // VIP badge in corner
            Text(
                text = "VIP${vipLevel.level}",
                color = Color.White.copy(alpha = 0.2f),
                fontSize = 24.sp, // reduced size
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp) // reduced padding
            )
        }
    }
}

@Composable
fun BenefitsSection(selectedVip: VipLevel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "💎",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VIP${selectedVip.level} Benefits level",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(color = Color.Black.copy(alpha = 1f))

            Spacer(modifier = Modifier.height(8.dp))

            // Level up rewards
            BenefitItem(
                icon = "🎁",
                title = "Level up rewards",
                description = "Each account can only receive 1 time",
                coinAmount = selectedVip.levelUpReward,
                diamondAmount = 0
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Monthly reward
            BenefitItem(
                icon = "⭐",
                title = "Monthly reward",
                description = "Each account can only receive 1 time per month",
                coinAmount = selectedVip.monthlyReward,
                diamondAmount = 0
            )
        }
    }
}

@Composable
fun BenefitItem(
    icon: String,
    title: String,
    description: String,
    coinAmount: Int,
    diamondAmount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 32.sp,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Black
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            if (coinAmount > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = coinAmount.toString(),
                            color = Color(0xFFFF8C00),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE5CC)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "💎", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = diamondAmount.toString(),
                        color = Color(0xFFFF6B35),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MyBenefitsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Main Benefits Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "👑",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My benefits",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = Color.Black.copy(alpha = 1f))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // New Card for Row with Light Red Background
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), // Light red background
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "History",
                    color = Color(0xFFFF6B35),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { }
                )
                Text(
                    text = "Rules",
                    color = Color(0xFFFF6B35),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}
