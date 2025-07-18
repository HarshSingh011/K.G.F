package com.weblite.kgf.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.weblite.kgf.R
import com.weblite.kgf.ui.screens.dashboard.ActivityScreen
import com.weblite.kgf.ui.screens.dashboard.HomeScreen
import com.weblite.kgf.ui.screens.dashboard.MainWalletScreen
import com.weblite.kgf.ui.screens.dashboard.ProfileScreen
import com.weblite.kgf.ui.screens.dashboard.PromotionScreen
import com.weblite.kgf.ui.screens.dashboard.WalletScreen

sealed class BottomNavItem(val title: String, @DrawableRes val iconRes: Int) {
    object Home : BottomNavItem("Home", R.drawable.img)
    object Activity : BottomNavItem("Activity", R.drawable.activity)
    object Promotion : BottomNavItem("Promotion", R.drawable.promotion)
    object Wallet : BottomNavItem("Wallet", R.drawable.wallet)
    object Profile : BottomNavItem("Profile", R.drawable.userpro)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    val systemUiController = rememberSystemUiController()

    // ✅ These control visibility of Top and Bottom bars
    var showTopBar by remember { mutableStateOf(true) }
    var showBottomBar by remember { mutableStateOf(true) }

    SideEffect {
        systemUiController.setStatusBarColor(Color.White, darkIcons = true)
    }

    // 🎯 Dynamic Top Bar
    val topBar: @Composable (() -> Unit)? = if (showTopBar) {
        {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(58.dp)
                                .padding(end = 8.dp)
                        )
                        KGFLogoText()
                    }

                }
            }
        }
    } else null

    // 🎯 Dynamic Bottom Bar
    val bottomBar: @Composable (() -> Unit)? = if (showBottomBar) {
        {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .navigationBarsPadding()
                    .height(64.dp)
            ) {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Activity,
                    BottomNavItem.Promotion,
                    BottomNavItem.Wallet,
                    BottomNavItem.Profile
                )

                items.forEach { item ->
                    val selected = selectedItem == item
                    NavigationBarItem(
                        icon = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(modifier = Modifier.height(1.dp))
                                val iconSize by animateDpAsState(
                                    targetValue = if (selected) 28.dp else 24.dp,
                                    animationSpec = tween(200)
                                )
                                Image(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(iconSize)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 9.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) Color(0xFF28CDE2) else Color.Gray,
                                lineHeight = 10.sp
                            )
                        },
                        selected = selected,
                        onClick = { selectedItem = item },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color(0xFF28CDE2),
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color(0xFF28CDE2),
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }
    } else null

    // Scaffold with dynamic bars
    Scaffold(
        topBar = { topBar?.invoke() },
        bottomBar = { bottomBar?.invoke() }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItem) {
                is BottomNavItem.Home -> HomeScreen(
                    modifier = Modifier,
                    navController = navController,
                    onShowTopBar = { showTopBar = it },
                    onShowBottomBar = { showBottomBar = it },
                    onNavigateToWallet = { selectedItem = BottomNavItem.Wallet },
                    onNavigateToProfile = { selectedItem = BottomNavItem.Profile }
                )
                is BottomNavItem.Activity -> ActivityScreen(
                    navController = navController,
                    onShowTopBar = { showTopBar = it },
                    onShowBottomBar = { showBottomBar = it }
                )
                is BottomNavItem.Promotion -> PromotionScreen(
                    navController = navController,
                    onShowTopBar = { showTopBar = it },
                    onShowBottomBar = { showBottomBar = it }
                )
                is BottomNavItem.Wallet -> MainWalletScreen(
                    navController = navController,
                    onShowTopBar = { showTopBar = it },
                    onShowBottomBar = { showBottomBar = it }
                )
                is BottomNavItem.Profile -> ProfileScreen(
                    navController = navController,
                    onShowTopBar = { showTopBar = it },
                    onShowBottomBar = { showBottomBar = it },
                    onNavigateToWallet = { selectedItem = BottomNavItem.Wallet }
                )
            }
        }
    }
}

@Composable
fun KGFLogoText() {
    Box(
        modifier = Modifier
            .height(36.dp)
            .padding(vertical = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        val logoText = "K•G•F"

        // Stroke Text
        Text(
            text = logoText,
            style = TextStyle(
                fontSize = 30.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.Transparent,
                drawStyle = Stroke(width = 2f),
                letterSpacing = 1.5.sp
            )
        )

        // Filled Glow Text
        Text(
            text = logoText,
            style = TextStyle(
                fontSize = 30.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFC300),
                shadow = Shadow(
                    color = Color(0xFF000000).copy(alpha = 0.7f),
                    blurRadius = 6f
                ),
                letterSpacing = 1.5.sp
            )
        )
    }
}


//########################
@Preview
@Composable
fun MainScr(){
    MainScreen()
}