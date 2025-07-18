package com.weblite.kgf.ui.screens.dashboard

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.weblite.kgf.Api2.MainViewModel
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.Api2.WingoTimerService
import com.weblite.kgf.R
import com.weblite.kgf.data.EarningUser
import com.weblite.kgf.data.Winner
import com.weblite.kgf.ui.components.AVIATOR
import com.weblite.kgf.ui.components.AttractiveText
import com.weblite.kgf.ui.components.CardSection
import com.weblite.kgf.ui.components.DRAGON_TIGER
import com.weblite.kgf.ui.components.EarningInfoSection
import com.weblite.kgf.ui.components.EarningsChart
import com.weblite.kgf.ui.components.HOMESCREEN
import com.weblite.kgf.ui.components.K3_30
import com.weblite.kgf.ui.components.K3_60
import com.weblite.kgf.ui.components.UserWalletCards
import com.weblite.kgf.ui.components.WINGO_30
import com.weblite.kgf.ui.components.WINGO_60
import com.weblite.kgf.ui.components.WinningInfoSection
import com.weblite.kgf.ui.screens.game.K3Ui30
import com.weblite.kgf.ui.screens.game.K3Ui60
import com.weblite.kgf.ui.screens.game.TigerAndDragonGameScreen
import com.weblite.kgf.ui.screens.game.Wingo30Screen
import com.weblite.kgf.ui.screens.game.Wingo60Screen
import com.weblite.kgf.utils.AssetImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToProfile: () -> Unit,
    timerService: WingoTimerService = hiltViewModel<MainViewModel>().timerService
) {

    val context = LocalContext.current
    val localNavController = rememberNavController()

    NavHost(
        navController = localNavController,
        startDestination = HOMESCREEN
    ) {
        composable(HOMESCREEN) {
            LaunchedEffect(Unit) {
                onShowTopBar(true)
                onShowBottomBar(true)
            }

            HomeMainConten(
                navController = localNavController,
                onNavigateToWallet = onNavigateToWallet,
                onNavigateToProfile = onNavigateToProfile,
                timerService = timerService
            )
        }

        composable(WINGO_30) {
            // Call countdown APIs when navigating to Wingo30
//            LaunchedEffect(Unit) {
//                timerService.fetchInitialData()
//            }

            Wingo30Screen(
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }

        composable(WINGO_60) {
            // Call countdown APIs when navigating to Wingo60
//            LaunchedEffect(Unit) {
//                timerService.fetchInitialData()
//            }

            Wingo60Screen(
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }

        composable(K3_30) {
//            LaunchedEffect(Unit) {
//                Toast.makeText(context, "K3 30 opened", Toast.LENGTH_SHORT).show()
//                // Call countdown APIs when navigating to K3_30
//                timerService.fetchInitialData()
//            }
            K3Ui30(
                variant = "30",
                onBack = { localNavController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }

        composable(K3_60) {
//            LaunchedEffect(Unit) {
//                Toast.makeText(context, "K3 60 opened", Toast.LENGTH_SHORT).show()
//                // Call countdown APIs when navigating to K3_60
//                timerService.fetchInitialData()
//            }
            K3Ui60(
                variant = "60",
                onBack = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }

        composable(DRAGON_TIGER) {
//            LaunchedEffect(Unit) {
//                // Call countdown APIs when navigating to Dragon Tiger
//                timerService.fetchInitialData()
//            }

            TigerAndDragonGameScreen(
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }

        composable(AVIATOR) {
//            LaunchedEffect(Unit) {
//                Toast.makeText(context, "Aviator opened", Toast.LENGTH_SHORT).show()
//                // Call countdown APIs when navigating to Aviator
//                timerService.fetchInitialData()
//            }
        }
    }
}

@Composable
fun HomeMainConten(
    navController: NavController,
    onNavigateToWallet: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    timerService: WingoTimerService
){

    val dashboardState = viewModel.dashboardState.value

    var userMobile by remember { mutableStateOf("") }
    var joiningDate by remember { mutableStateOf("") }
    var userLevel by remember { mutableStateOf("") }
    var totalBalance by remember { mutableStateOf(0) }

    val userid = SharedPrefManager.getString("user_id","0")
    LaunchedEffect(key1 = userid) {
        viewModel.fetchDashboard(userid)
        // Fetch countdown data when home screen loads
//        timerService.fetchInitialData()
    }

    if (dashboardState is Resource.Success) {
        dashboardState.data?.result?.let { result ->
            userMobile = result.user.mobile.orEmpty()
            joiningDate = result.user.joiningDate.orEmpty()
            userLevel = result.levelUser.orEmpty()
            totalBalance = result.totalBalance
        }
    }

    val scrollState = rememberScrollState()
    val bannerImages = listOf("banners/Homepage_img1.jpg", "banners/Homepage_img2.jpg", "banners/Homepage_img3.jpg")
    val WingoItems = listOf("gameimg/Wingo_30.jpeg", "gameimg/Wingo_60.jpg")
    val K3Items = listOf("gameimg/K3_30.jpg", "gameimg/K3_60.jpg")
    val DragAviItems = listOf("gameimg/Dragon_tiger.jpeg", "gameimg/Aviator_img.png")
    val sampleWinners = listOf(
        Winner(R.drawable.ic_launcher_background, "MEM***AWG", "702.20", R.drawable.ic_win1,isTop = true),
        Winner(R.drawable.ic_launcher_background, "MEM***KEY", "122.28", R.drawable.ic_win2,isTop = true),
        Winner(R.drawable.ic_launcher_background, "MEM***RXG", "122.90", R.drawable.ic_win2,isTop = true),
        Winner(R.drawable.ic_launcher_background, "MEM***AWG", "903.44", R.drawable.ic_win3,isTop = true),
        Winner(R.drawable.ic_launcher_background, "MEM***MKW", "620.50", R.drawable.ic_win3,isTop = true),
        Winner(R.drawable.ic_launcher_background, "MEM***RAG", "620.50", R.drawable.ic_win2,isTop = true)
    )

    val earningList = listOf(
        Winner(R.drawable.ic_launcher_background, "MEM***AWG", "2,112,323.27", R.drawable.ic_win1,isTop = true),
        Winner(R.drawable.ic_launcher_background, "MEM***AXY", "1,235,323.27", R.drawable.ic_win1,isTop = true),
        Winner(R.drawable.ic_launcher_background, "MEM***AQU", "702.20", R.drawable.ic_win1,isTop = true),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF002051))
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {

        BannerSection(bannerImages)

        // Wallet
        val lastFourDigits = userMobile.takeLast(4)
        UserWalletCards(
            mobileNumber = "XXXXXX$lastFourDigits",
            userId = "$userid",
            walletBalance = "$totalBalance",
            onUSRCardClick = { onNavigateToProfile() },
            onWLTCardClick = { onNavigateToWallet() }
        )

        // Sound Toggle + Title Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SoundToggleIcon()
            Spacer(modifier = Modifier.width(48.dp))
            AttractiveText(
                text = "Lottery for you",
                primaryColor = Color(0xFFfefefe),
                shadowColor = Color(0xFF606060),
                fontSize = 28.sp,
                strokeWidth = 1.5f
            )
        }

        // Sections
        CardSection(
            items = WingoItems,
            onItemClick = { imagePath ->
                when (imagePath) {
                    "gameimg/Wingo_30.jpeg" -> navController.navigate(WINGO_30)
                    "gameimg/Wingo_60.jpg" -> navController.navigate(WINGO_60)
                }
            }
        )

        CardSection(
            items = K3Items,
            onItemClick = { imagePath ->
                when (imagePath) {
                    "gameimg/K3_30.jpg" -> navController.navigate(K3_30)
                    "gameimg/K3_60.jpg" -> navController.navigate(K3_60)
                }
            }
        )

        CardSection(
            items = DragAviItems,
            onItemClick = { imagePath ->
                when (imagePath) {
                    "gameimg/Dragon_tiger.jpeg" -> navController.navigate(DRAGON_TIGER)
                    "gameimg/Aviator_img.png" -> navController.navigate(AVIATOR)
                }
            }
        )

        WinningInfoSection(sampleWinners)
        // earing chart
        val dummyData = remember { getDummyEarningUsers() }

        // Call the chart with dummy data
        EarningsChart(users = dummyData)

        EarningInfoSection(earnings = earningList)
    }
}

fun navigateToGame(navController: NavController, gameName: String) {
    when (gameName.lowercase()) {
        "k3" -> navController.navigate("k3_screen")
        "Wingo 30" -> navController.navigate("wingo_screen")
        "dragon_tiger" -> navController.navigate("dragon_tiger_screen")
        "aviator" -> navController.navigate("aviator_screen")
    }
}

@Composable
fun BannerSection(bannerImages: List<String>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }

    // Auto scroll every 3 sec
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentIndex = (currentIndex + 1) % bannerImages.size
            coroutineScope.launch {
                listState.animateScrollToItem(currentIndex)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(horizontal = 16.dp), // spacing at start/end
            horizontalArrangement = Arrangement.spacedBy(28.dp), // gap between banners
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            items(bannerImages.size) { index ->
                BannerCard(imageResId = bannerImages[index])
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dot indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            bannerImages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (index == currentIndex) 10.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (index == currentIndex) Color.Blue else Color.LightGray)
                )
            }
        }
    }
}

@Composable
fun BannerCard(imageResId: String) {
    Card(
        modifier = Modifier
            .width(382.dp) // Centered fixed width card
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        AssetImage(
            assetPath = imageResId,
            contentDescription = "Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun SoundToggleIcon(iconColor: Color = Color(0xFFDEAF56)) {
    var isSoundOn by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(isSoundOn) {
        mediaPlayer = MediaPlayer.create(context, R.raw.game_music).apply {
            isLooping = true
            if (isSoundOn) start()
        }

        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Icon(
        imageVector = if (isSoundOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
        contentDescription = if (isSoundOn) "Sound On" else "Sound Off",
        tint = iconColor,
        modifier = Modifier
            .size(30.dp)
            .clickable {
                isSoundOn = !isSoundOn
                mediaPlayer?.let { player ->
                    if (isSoundOn) {
                        if (!player.isPlaying) player.start()
                    } else {
                        player.pause()
                    }
                }
            }
    )
}

fun getDummyEarningUsers(): List<EarningUser> {
    return listOf(
        EarningUser(
            position = "NO 2",
            name = "MEM**AXY",
            amount = "₹5,599,610.20",
            crownColor = Color.Gray,
            backgroundColor = Color(0xFFD1B2FF),
            isTop = false
        ),
        EarningUser(
            position = "NO 1",
            name = "MEM**KLK",
            amount = "₹10,703,323.50",
            crownColor = Color(0xFFFFD700),
            backgroundColor = Color(0xFFFFA726),
            isTop = true
        ),
        EarningUser(
            position = "NO 3",
            name = "MEM**AQU",
            amount = "₹4,435,323.27",
            crownColor = Color(0xFFCD7F32),
            backgroundColor = Color(0xFFFF8A80),
            isTop = false
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val dummyData = remember { getDummyEarningUsers() }
    EarningsChart(users = dummyData)
}
