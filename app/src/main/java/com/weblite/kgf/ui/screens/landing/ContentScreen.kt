package com.mohit.kgfindia.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weblite.kgf.R
import com.weblite.kgf.data.RecentItem
import com.weblite.kgf.data.TabItem
import com.weblite.kgf.ui.screens.MainScreen
import com.weblite.kgf.ui.screens.auth.AuthFlowScreen
import com.weblite.kgf.ui.screens.landing.AuthDialog
import com.weblite.kgf.utils.AssetImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") {
            LandingScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("landing") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            Box(modifier = Modifier.fillMaxSize()) {
                var showLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(500)
                    showLoading = false
                }

                if (showLoading) {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF002051)), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFDEAF56))
                    }
                } else {
                    MainScreen()
                }
            }
        }
    }
}
@Composable
fun LandingScreen(onLoginSuccess: () -> Unit) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showAuthScreen by remember { mutableStateOf(false) }
    var authStartWithLogin by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(true) }

    //  Show AuthDialog only when needed
    if (showDialog) {
        AuthDialog(index = 0, onDismiss = { showDialog = false },
            onLoginSuccess = {
                showAuthScreen = false
                onLoginSuccess()
            })
    }
    if (showAuthScreen) {
        BackHandler(enabled = true) {
            showAuthScreen = false
        }

        AuthFlowScreen(
            startWithLogin = authStartWithLogin,
            onLoginSuccess = {
                showAuthScreen = false
                onLoginSuccess()
            },
            onClose = { showAuthScreen = false }
        )
    } else {
        Column(modifier = Modifier.background(Color(0xFF3f0110))) {
            MainAppBar(
                onLoginClick = {
                    authStartWithLogin = true
                    showAuthScreen = true
                },
                onRegisterClick = {
                    authStartWithLogin = false
                    showAuthScreen = true
                }
            )

            val bannerVisible by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 } }

            if (bannerVisible) {
                BannerSection(listOf(R.drawable.bann1, R.drawable.bann2, R.drawable.bann3))
            }

            TabSection(selectedTabIndex) { index ->
                selectedTabIndex = index
                coroutineScope.launch {
                    scrollState.animateScrollToItem(index)
                }
            }

            ContentList(scrollState, selectedTabIndex) { newIndex ->
                selectedTabIndex = newIndex
            }
        }
    }
}


@Composable
fun BannerSection(bannerImages: List<Int>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // 3 seconds
            currentIndex = (currentIndex + 1) % bannerImages.size
            coroutineScope.launch {
                listState.animateScrollToItem(currentIndex)
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.padding(vertical = 0.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(bannerImages) { imageResId ->
            BannerItem(imageResId)
        }
    }
}

@Composable
fun BannerItem(imageResId: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(1.dp)),
        color = Color.Gray
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Banner",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TabSection(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(
        TabItem("Recent", R.drawable.history),
        TabItem("Hot", R.drawable.ic_fire),
        TabItem("Lottery", R.drawable.ic_lottery_balls),
        TabItem("Favorite", R.drawable.ic_favorite_border)
    )
    Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
        SoundToggleIcon()
    }
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color(0xFF3f0110),
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(tab.icon),
                        contentDescription = tab.title,
                        tint = if (selectedTabIndex == index)
                            Color(0xFFDEAF56)
                        else
                            Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = tab.title,
                        color = if (selectedTabIndex == index)
                            Color(0xFFDEAF56)
                        else
                            Color.White
                    )
                }
            )
        }

    }
}
@Composable
fun SoundToggleIcon(iconColor: Color = Color(0xFFDEAF56)) {
    var isSoundOn by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    DisposableEffect(isSoundOn) {  // Track isSoundOn state
        mediaPlayer = MediaPlayer.create(context, R.raw.game_music).apply {
            isLooping = true  // Add looping if you want continuous playback
            if (isSoundOn) {
                start()
            }
        }

        onDispose {
            mediaPlayer?.apply {
                stop()
                release()
            }
            mediaPlayer = null
        }
    }

    Icon(
        imageVector = if (isSoundOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
        contentDescription = if (isSoundOn) "Sound On" else "Sound Off",
        tint = iconColor,
        modifier = Modifier.clickable {
            isSoundOn = !isSoundOn
            mediaPlayer?.let { player ->
                if (isSoundOn) {
                    if (!player.isPlaying) {
                        player.start()
                    }
                } else {
                    player.pause()
                }
            }
        }
    )
}

@Composable
fun DefoultBanner() {
    AssetImage(
        assetPath = "landingimg/d_banner.png",
        contentDescription = "Banner",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}



@Composable
fun ContentList(
    scrollState: LazyListState,
    selectedTabIndex: Int,
    onScrollChange: (Int) -> Unit
) {
    val sections = listOf("Recent", "Hot", "Lottery", "Favorite")

    val recentItems = listOf(
        RecentItem("landingimg/Aviator.png", "Aviator"),
        RecentItem("landingimg/ludo.png", "Ludo"),
        RecentItem("landingimg/Roulette.png", "Auto Roulette")
    )
    val hotItems = listOf(
        RecentItem("landingimg/jili.png", "Jili Slot"),
        RecentItem("landingimg/pg.png", "PG Slots"),
        RecentItem("landingimg/jdb.png", "JDB Slot")
    )
    val lotteryItems = listOf(
        RecentItem("landingimg/wingo30.png", "Wingo 30sec"),
        RecentItem("landingimg/wingo60.jpg", "Wingo 60sec"),
        RecentItem("landingimg/k3_30.jpg", "K3 30sec"),
        RecentItem("landingimg/k3_60.jpg", "K4 40Sec")
    )
    val favoriteItems = listOf(
        RecentItem("landingimg/wingo30.png", "Wingo 30sec"),
        RecentItem("landingimg/wingo60.jpg", "Wingo 60sec"),
        RecentItem("landingimg/k3_30.jpg", "K3 30sec")
    )

    LaunchedEffect(scrollState.firstVisibleItemIndex) {
        onScrollChange(scrollState.firstVisibleItemIndex)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        contentPadding = PaddingValues(8.dp)
    ) {

        item { DefoultBanner() }
        item { RecentSection(recentItems) }
        item { HotSection(hotItems) }
        item { LotterySection(lotteryItems) }
        item { FavoriteSection(favoriteItems) }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "K.G.F",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        },
        actions = {
            Row(modifier = Modifier.padding(4.dp)) {
                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDEAF56),
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onRegisterClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3f0110),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(2.dp, Color(0xFFDEAF56))
                ) {
                    Text("Register", color = Color(0xFFDEAF56))
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color(0xFF3f0110),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}



@Preview(showBackground = true)
@Composable
fun PreviewContentScreen() {
    ContentScreen(modifier = Modifier)
}
