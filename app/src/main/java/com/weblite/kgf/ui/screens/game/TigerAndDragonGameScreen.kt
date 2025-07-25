package com.weblite.kgf.ui.screens.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.graphicsLayer
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.weblite.kgf.ui.screens.game.GameHistoryDialogTigerAndDragon
import com.weblite.kgf.viewmodel.TigerAndDragonViewModel
import com.weblite.kgf.data.TigerAndDragonDataClasses.TigerGameHistoryResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast // For showing messages
import android.util.Log // Import for logging
import com.weblite.kgf.Api2.Resource
import kotlinx.coroutines.Job

@Composable
fun TigerAndDragonGameScreen(
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onBackClick: () -> Unit, // Added new parameter for back
    viewModel: TigerAndDragonViewModel = hiltViewModel(),
) {
    var showTigerOverlay by remember { mutableStateOf(false) }
    var showPeriodId by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf("game-history") } // Track active tab

    // Use ViewModel timer instead of local timer
    val secondsRemaining by viewModel.secondsRemaining.collectAsState()

    // Collect game history state
    val gameHistoryState by viewModel.gameHistory.collectAsState()
    var selectedChipIndex by remember { mutableStateOf(-1) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // State for placed coins in each section (visible on UI)
    var dragonCoins by remember { mutableStateOf(listOf<Int>()) }
    var tigerCoins by remember { mutableStateOf(listOf<Int>()) }
    var tieCoins by remember { mutableStateOf(listOf<Int>()) }

    // Track all coins placed (even those not visible)
    var dragonAllCoins by remember { mutableStateOf(listOf<Int>()) }
    var tigerAllCoins by remember { mutableStateOf(listOf<Int>()) }
    var tieAllCoins by remember { mutableStateOf(listOf<Int>()) }

    // Define chipValues here so it's available for addCoinToSection
    val chipValues = listOf(10, 50, 500, 1000, 5000)

    // Helper to add coin to a section (use section param)
    fun addCoinToSection(section: String) {
        val value = chipValues.getOrNull(selectedChipIndex) ?: return
        when (section) {
            "dragon" -> {
                dragonAllCoins = dragonAllCoins + value
                dragonCoins = dragonAllCoins // Show all coins for correct sum
            }
            "tiger" -> {
                tigerAllCoins = tigerAllCoins + value
                tigerCoins = tigerAllCoins
            }
            "tie" -> {
                tieAllCoins = tieAllCoins + value
                tieCoins = tieAllCoins
            }
        }
        val dragonSum = dragonAllCoins.sum()
        val tigerSum = tigerAllCoins.sum()
        val tieSum = tieAllCoins.sum()
        Log.d("TigerAndDragonGameScreen", "Added coin $value to $section. Dragon: $dragonSum, Tiger: $tigerSum, Tie: $tieSum")
    }

    // Tab-specific logic for fetching data
    LaunchedEffect(activeTab) {
        if (activeTab == "game-history") {
            viewModel.startGameHistoryPolling()
            viewModel.fetchGameHistory()
        } else {
            viewModel.stopGameHistoryPolling()
            viewModel.fetchMyHistory()
        }
    }

    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
    }

    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Initial fetch
    LaunchedEffect(Unit) {
        Log.d("TigerAndDragonGameScreen", "Initial fetch: Period ID and Game History.")
        viewModel.fetchPeriodId()
        viewModel.fetchGameHistory()
    }

    // UI Update for period ID
    LaunchedEffect(Unit) {
        viewModel.periodId.collect { resourceEvent ->
            when (resourceEvent) {
                is Resource.Success<*> -> {
                    val response = resourceEvent.data
                    // Only handle TigerPeriodIdResponse, not TigerPeriodIdResult
                    if (response is com.weblite.kgf.data.TigerAndDragonDataClasses.TigerPeriodIdResponse && response.result != null && response.result.isNotEmpty()) {
                        val datetime = response.result[0].periodId
                        showPeriodId = datetime
                        Log.d("TigerAndDragonGameScreen", "Period ID fetched: $datetime")
                    } else {
                        showPeriodId = "No Period Id Data"
                        Log.w("TigerAndDragonGameScreen", "Period ID response empty, null, or wrong type.")
                    }
                }
                is Resource.Error<*> -> {
                    showPeriodId = "Error Fetching Period Id: ${resourceEvent.message}"
                    Log.e("TigerAndDragonGameScreen", "Error fetching Period ID: ${resourceEvent.message}")
                }
                is Resource.Loading<*> -> {
                    showPeriodId = "Fetching Period Id..."
                    Log.d("TigerAndDragonGameScreen", "Fetching Period ID (Loading state).")
                }
                else -> {
                    showPeriodId = "Unknown State"
                }
            }
        }
    }

    val timerEnded by viewModel.timerEnded.collectAsState()
    var resultOverlayType by remember { mutableStateOf<String?>(null) } // "dragon", "tiger", "draw", or null

    // Call bet API when timer reaches 5 seconds remaining, with duplicate prevention and correct amount check
    var betSentDragon by remember { mutableStateOf(false) }
    var betSentTiger by remember { mutableStateOf(false) }
    var betSentTie by remember { mutableStateOf(false) }

    LaunchedEffect(secondsRemaining) {
        if (secondsRemaining == 5) {
            Log.d("TigerAndDragonGameScreen", "[BET LOGIC] Timer reached 5s. Placing bets if any.")
            val dragonSum = dragonAllCoins.sum()
            val tigerSum = tigerAllCoins.sum()
            val tieSum = tieAllCoins.sum() 
            Log.d("TigerAndDragonGameScreen", "[BET AMOUNT] Dragon: $dragonSum, Tiger: $tigerSum, Tie: $tieSum")
            // Only send bet if not already sent in this cycle and amount > 0
            if (dragonSum > 0 && !betSentDragon) {
                Log.d("TigerAndDragonGameScreen", "Calling placeBet for dragon with amount: $dragonSum")
                viewModel.placeBet("dragon", dragonSum)
                betSentDragon = true
            }
            if (tigerSum > 0 && !betSentTiger) {
                Log.d("TigerAndDragonGameScreen", "Calling placeBet for tiger with amount: $tigerSum")
                viewModel.placeBet("tiger", tigerSum)
                betSentTiger = true
            }
            if (tieSum > 0 && !betSentTie) {
                Log.d("TigerAndDragonGameScreen", "Calling placeBet for tie with amount: $tieSum")
                viewModel.placeBet("tie", tieSum)
                betSentTie = true
            }

            // Clear coins after bet
            dragonCoins = emptyList(); tigerCoins = emptyList(); tieCoins = emptyList();
            dragonAllCoins = emptyList(); tigerAllCoins = emptyList(); tieAllCoins = emptyList();
            selectedChipIndex = -1
        }
        // Reset sent flags when timer resets (i.e., when secondsRemaining increases again)
        if (secondsRemaining > 5) {
            betSentDragon = false
            betSentTiger = false
            betSentTie = false
        }
    }

    // Show result overlay for 400ms after timer ends
    LaunchedEffect(timerEnded) {
        if (timerEnded) {
            val latestResult = try {
                val response = (gameHistoryState as? Resource.Success<*>)?.data
                val historyList = when (response) {
                    is TigerGameHistoryResponse -> response.result?.history
                    is com.weblite.kgf.data.TigerAndDragonDataClasses.TigerGameHistoryResult -> response.history
                    else -> null
                }
                var resultItem = historyList?.getOrNull(1)
                var resultValue = resultItem?.result?.trim()?.lowercase()
                Log.d("TigerAndDragonGameScreen", "[OVERLAY LOGIC] Second history item result: ${resultItem?.result}")
                if (resultValue == null) {
                    // Fallback to first item if second is null
                    resultItem = historyList?.getOrNull(0)
                    resultValue = resultItem?.result?.trim()?.lowercase()
                    Log.d("TigerAndDragonGameScreen", "[OVERLAY LOGIC] Fallback to first history item result: ${resultItem?.result}")
                }
                when (resultValue) {
                    "tiger" -> "tiger"
                    "dragon" -> "dragon"
                    "draw", "tie" -> "draw"
                    else -> null
                }
            } catch (e: Exception) {
                Log.e("TigerAndDragonGameScreen", "[OVERLAY LOGIC] Exception: ${e.message}")
                null
            }
            resultOverlayType = latestResult
            if (resultOverlayType != null) {
                kotlinx.coroutines.delay(400)
                resultOverlayType = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1B2A), // dark blue top
                        Color(0xFF1B263B), // mid
                        Color(0xFF274472)  // dark blue bottom
                    )
                )
            )
    ) {
        // Top bar with back button (left) and history button (right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, start = 18.dp, end = 18.dp)
                .zIndex(20f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            androidx.compose.material3.IconButton(
                onClick = onBackClick, // Used the new onBackClick parameter
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF1B263B), shape = CircleShape)
                    .border(2.dp, Color(0xFFED6A5A), CircleShape)
            ) {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = R.drawable.backarrow),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            // History button (image)
            androidx.compose.material3.IconButton(
                onClick = { showHistoryDialog = true }, // MODIFIED: Open the dialog
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF1B263B), shape = CircleShape)
                    .border(2.dp, Color(0xFF43A047), CircleShape)
            ) {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = R.drawable.history),
                    contentDescription = "History",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        // Main game area with lowest z-index
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f) // Lowest z-index
        ) {
            // Blur overlay when 5s remain
            if (secondsRemaining in 1..5) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xAA000000))
                        .zIndex(100f),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = 0.7f },
                        color = Color.Transparent
                    ) {}
                    Text(
                        text = "Bet Placed",
                        color = Color.White,
                        fontSize = 32.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            val overlayHeight = maxHeight * 0.75f
            val overlayWidth = maxWidth * 0.18f

            // Main game table area (increased size and better positioning)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(0.78f) // Increased width
                    .fillMaxHeight(0.75f) // Increased height
                    .align(Alignment.BottomCenter)
                    .padding(bottom = maxHeight * 0.16f) // Adjusted bottom padding
            ) {
                val tableWidth = maxWidth
                val tableHeight = maxHeight
                val borderThickness = tableWidth * 0.04f // Slightly reduced border
                val chipSize = tableWidth * 0.10f
                val avatarSize = chipSize * 0.8f
                val cornerRadius = tableHeight * 0.5f
                val borderCornerRadius = tableHeight * 0.38f

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(tableWidth, tableHeight)
                ) {
                    // Outer border
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0A2342), // deep blue
                                        Color(0xFF274472)
                                    )
                                ),
                                shape = RoundedCornerShape(borderCornerRadius)
                            )
                    ) {}
                    // Inner border
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(tableWidth - borderThickness, tableHeight - borderThickness)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF1B263B), // lighter blue
                                        Color(0xFF415A77)
                                    )
                                ),
                                shape = RoundedCornerShape(borderCornerRadius)
                            )
                    ) {}
                    // Main table area
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(tableWidth - borderThickness * 2, tableHeight - borderThickness * 2)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFE0E7FF), // soft blue white
                                        Color(0xFFB3C7F7), // pastel blue
                                        Color(0xFFB39DDB)  // soft purple
                                    )
                                ),
                                shape = RoundedCornerShape(cornerRadius)
                            )
                    ) {
                        // Removed the inner Box wrapper and padding to maximize space
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center
                        ) {

                            // Dragon section
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(
                                        topStart = cornerRadius,
                                        bottomStart = cornerRadius,
                                        topEnd = 0.dp,
                                        bottomEnd = 0.dp
                                    ))
                                    .background(Color(0xFF2B3A4B))
                                    .clickable { addCoinToSection("dragon") }
                            ) {
                                // Image and text first
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painterResource(R.drawable.dragon),
                                        contentDescription = "Dragon",
                                        modifier = Modifier
                                            .size(avatarSize * 2.8f)
                                            .alpha(0.65f)
                                    )
                                    Spacer(modifier = Modifier.height(tableHeight * 0.02f))
                                    Text(
                                        text = "Dragon\n1:2",
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontSize = (tableHeight * 0.08f).value.sp,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                                // Coins at the bottom, with higher z-index
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .zIndex(2f)
                                        .padding(bottom = 12.dp)
                                ) {
                                    val dragonTopRow = dragonCoins.take(6)
                                    val dragonBottomRow = dragonCoins.drop(6)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            dragonTopRow.forEach { value ->
                                                Image(
                                                    painter = painterResource(
                                                        when (value) {
                                                            10 -> R.drawable.chip_10
                                                            50 -> R.drawable.chip_50
                                                            500 -> R.drawable.chip_500
                                                            1000 -> R.drawable.chip_1000
                                                            5000 -> R.drawable.chip_5000
                                                            else -> R.drawable.chip_10
                                                        }
                                                    ),
                                                    contentDescription = "Coin $value",
                                                    modifier = Modifier.size(28.dp).padding(2.dp)
                                                )
                                            }
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            dragonBottomRow.forEach { value ->
                                                Image(
                                                    painter = painterResource(
                                                        when (value) {
                                                            10 -> R.drawable.chip_10
                                                            50 -> R.drawable.chip_50
                                                            500 -> R.drawable.chip_500
                                                            1000 -> R.drawable.chip_1000
                                                            5000 -> R.drawable.chip_5000
                                                            else -> R.drawable.chip_10
                                                        }
                                                    ),
                                                    contentDescription = "Coin $value",
                                                    modifier = Modifier.size(28.dp).padding(2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }


                            // Tie section
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .background(Color(0xFF43A047))
                                    .clickable { addCoinToSection("tie") }
                            ) {
                                // Image and text first
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "TIE\n1:8",
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontSize = (tableHeight * 0.10f).value.sp,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                                // Coins at the bottom, with higher z-index
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .zIndex(2f)
                                        .padding(bottom = 12.dp)
                                ) {
                                    val tieTopRow = tieCoins.take(6)
                                    val tieBottomRow = tieCoins.drop(6)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            tieTopRow.forEach { value ->
                                                Image(
                                                    painter = painterResource(
                                                        when (value) {
                                                            10 -> R.drawable.chip_10
                                                            50 -> R.drawable.chip_50
                                                            500 -> R.drawable.chip_500
                                                            1000 -> R.drawable.chip_1000
                                                            5000 -> R.drawable.chip_5000
                                                            else -> R.drawable.chip_10
                                                        }
                                                    ),
                                                    contentDescription = "Coin $value",
                                                    modifier = Modifier.size(28.dp).padding(2.dp)
                                                )
                                            }
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            tieBottomRow.forEach { value ->
                                                Image(
                                                    painter = painterResource(
                                                        when (value) {
                                                            10 -> R.drawable.chip_10
                                                            50 -> R.drawable.chip_50
                                                            500 -> R.drawable.chip_500
                                                            1000 -> R.drawable.chip_1000
                                                            5000 -> R.drawable.chip_5000
                                                            else -> R.drawable.chip_10
                                                        }
                                                    ),
                                                    contentDescription = "Coin $value",
                                                    modifier = Modifier.size(28.dp).padding(2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }


                            // Tiger section
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(
                                        topStart = 0.dp,
                                        bottomStart = 0.dp,
                                        topEnd = cornerRadius,
                                        bottomEnd = cornerRadius
                                    ))
                                    .background(Color(0xFFED6A5A))
                                    .clickable { addCoinToSection("tiger") }
                            ) {
                                // Image and text first
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painterResource(R.drawable.tiger),
                                        contentDescription = "Tiger",
                                        modifier = Modifier
                                            .size(avatarSize * 2.8f)
                                            .alpha(0.65f)
                                    )
                                    Spacer(modifier = Modifier.height(tableHeight * 0.02f))
                                    Text(
                                        text = "Tiger\n1:2",
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontSize = (tableHeight * 0.08f).value.sp,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                                // Coins at the bottom, with higher z-index
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .zIndex(2f)
                                        .padding(bottom = 12.dp)
                                ) {
                                    val tigerTopRow = tigerCoins.take(6)
                                    val tigerBottomRow = tigerCoins.drop(6)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            tigerTopRow.forEach { value ->
                                                Image(
                                                    painter = painterResource(
                                                        when (value) {
                                                            10 -> R.drawable.chip_10
                                                            50 -> R.drawable.chip_50
                                                            500 -> R.drawable.chip_500
                                                            1000 -> R.drawable.chip_1000
                                                            5000 -> R.drawable.chip_5000
                                                            else -> R.drawable.chip_10
                                                        }
                                                    ),
                                                    contentDescription = "Coin $value",
                                                    modifier = Modifier.size(28.dp).padding(2.dp)
                                                )
                                            }
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            tigerBottomRow.forEach { value ->
                                                Image(
                                                    painter = painterResource(
                                                        when (value) {
                                                            10 -> R.drawable.chip_10
                                                            50 -> R.drawable.chip_50
                                                            500 -> R.drawable.chip_500
                                                            1000 -> R.drawable.chip_1000
                                                            5000 -> R.drawable.chip_5000
                                                            else -> R.drawable.chip_10
                                                        }
                                                    ),
                                                    contentDescription = "Coin $value",
                                                    modifier = Modifier.size(28.dp).padding(2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Left user chips overlay (move closer to table)
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .height(overlayHeight * 1.0f) // taller so all chips fit
                    .width(overlayWidth * 1.5f)
                    .padding(start = 1.dp, bottom = maxHeight * 0.13f) // Adjusted padding
                    .align(Alignment.BottomStart)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top: User 1
                    UserChip(
                        "User 1", 500.0, R.drawable.user1,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.TopEnd)
                            .padding(top = overlayHeight * 0.02f, end = overlayWidth * 0.38f)
                    )
                    // Center: User 2
                    UserChip(
                        "User 2", 200.0, R.drawable.user2,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.CenterEnd)
                            .padding(end = overlayWidth * 0.80f)
                    )
                    // Bottom: User 3
                    UserChip(
                        "User 3", 100.0, R.drawable.user3,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.BottomEnd)
                            .padding(bottom = overlayHeight * 0.02f, end = overlayWidth * 0.48f)
                    )
                }
            }

            // Right user chips overlay (move closer to table)
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .height(overlayHeight * 1.0f) // taller so all chips fit
                    .width(overlayWidth * 1.5f)
                    .padding(end = 1.dp, bottom = maxHeight * 0.16f) // Adjusted padding
                    .align(Alignment.BottomEnd)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top: User 4
                    UserChip(
                        "User 4", 500.0, R.drawable.user4,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.TopStart)
                            .padding(top = overlayHeight * 0.02f, start = overlayWidth * 0.38f)
                    )
                    // Center: User 5
                    UserChip(
                        "User 5", 200.0, R.drawable.user5,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.CenterStart)
                            .padding(start = overlayWidth * 0.80f)
                    )
                    // Bottom: User 6
                    UserChip(
                        "User 6", 100.0, R.drawable.user6,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.BottomEnd)
                            .padding(bottom = overlayHeight * 0.02f, start = overlayWidth * 0.38f)
                    )
                }
            }

            // History coins box with medium z-index, align just above black border
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .absoluteOffset(y = (this@BoxWithConstraints.maxHeight * 0.19f)) // Adjusted position
                    .zIndex(2f),
                contentAlignment = Alignment.TopCenter
            ) {
                // Cylindrical background for all coins
                Box(
                    modifier = Modifier
                        .padding(horizontal = this@BoxWithConstraints.maxWidth * 0.10f)
                        .fillMaxWidth(0.80f)
                        .height(48.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1B263B),
                                    Color(0xFF415A77)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally)
                    ) {
                        val historyRaw = listOf("T", "T", "Tie", "D", "D", "Tie", "T", "Tie")
                        val visibleHistory = buildList {
                            add("...")
                            val count = historyRaw.size
                            if (count >= 8) {
                                addAll(historyRaw.take(8))
                            } else {
                                addAll(historyRaw)
                                repeat(8 - count) { add("...") }
                            }
                        }
                        visibleHistory.forEachIndexed { idx, it ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        when (it) {
                                            "T" -> Color(0xAAED6A5A)
                                            "D" -> Color(0xAA3CB371)
                                            "Tie" -> Color(0xAA4FC3F7)
                                            "..." -> Color(0xAA888888)
                                            else -> Color(0xAA888888)
                                        },
                                        shape = GenericShape { size, _ ->
                                            // Octagon shape
                                            val w = size.width
                                            val h = size.height
                                            val midX = w / 2f
                                            val midY = h / 2f
                                            val r = w / 2f
                                            for (i in 0..7) {
                                                val angle = Math.toRadians((45 * i - 22.5).toDouble())
                                                val x = midX + r * Math.cos(angle).toFloat()
                                                val y = midY + r * Math.sin(angle).toFloat()
                                                if (i == 0) moveTo(x, y) else lineTo(x, y)
                                            }
                                            close()
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (idx == 0) "..." else it,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom betting chips with medium z-index (move a bit up)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 19.dp)
                .zIndex(2f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.48f)
                    .height(44.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFB3C7F7),
                                Color(0xFFB39DDB)
                            )
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val chipImages = listOf(
                        R.drawable.chip_10,
                        R.drawable.chip_50,
                        R.drawable.chip_500,
                        R.drawable.chip_1000,
                        R.drawable.chip_5000
                    )
                    val chipValues = listOf(10, 50, 500, 1000, 5000)

                    chipValues.forEachIndexed { idx, value ->
                        val isSelected = idx == selectedChipIndex
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.3f else 1f,
                            label = "chipScale"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(36.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .background(
                                    Brush.verticalGradient(
                                        colors = when (value) {
                                            10 -> listOf(Color(0xFFED6A5A), Color(0xFFF7B267))
                                            50 -> listOf(Color(0xFF3CB371), Color(0xFFB2F7EF))
                                            500 -> listOf(Color(0xFF4FC3F7), Color(0xFFB39DDB))
                                            1000 -> listOf(Color(0xFFD1B06B), Color(0xFFB39DDB))
                                            5000 -> listOf(Color(0xFF415A77), Color(0xFFB39DDB))
                                            else -> listOf(Color.LightGray, Color.Gray)
                                        }
                                    ),
                                    shape = CircleShape
                                )
                                .clickable { selectedChipIndex = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(chipImages[idx % chipImages.size]),
                                contentDescription = "Chip $value",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        // Timer and animated images box at the very top of the screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 0.dp)
                .zIndex(10f),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Dragon image with vibration animation
                val dragonVibe = remember { androidx.compose.animation.core.Animatable(0f) }
                LaunchedEffect(Unit) {
                    while (true) {
                        dragonVibe.animateTo(
                            targetValue = 18f,
                            animationSpec = tween(
                                durationMillis = 1000, // Increased duration for slower animation
                                easing = LinearEasing
                            )
                        )
                        dragonVibe.animateTo(
                            targetValue = -18f,
                            animationSpec = tween(
                                durationMillis = 1000, // Increased duration for slower animation
                                easing = LinearEasing
                            )
                        )
                    }
                }
                Image(
                    painter = painterResource(R.drawable.dragon),
                    contentDescription = "Dragon Side",
                    modifier = Modifier
                        .size(110.dp)
                        .offset(x = dragonVibe.value.dp)
                )
                Spacer(modifier = Modifier.width(40.dp))

                // Redesigned timer box - now uses ViewModel timer
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF43A047), // Green top
                                    Color(0xFFB3C7F7), // Blue middle
                                    Color(0xFFED6A5A)  // Orange bottom
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(3.dp, Color(0xFF1B263B), RoundedCornerShape(24.dp))
                        .shadow(10.dp, RoundedCornerShape(24.dp))
                        .padding(horizontal = 38.dp, vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Use ViewModel timer instead of local timer
                    Text(
                        text = secondsRemaining.toString(),
                        color = Color(0xFF1B263B),
                        fontSize = 38.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))

                // Tiger image with vibration animation
                val tigerVibe = remember { androidx.compose.animation.core.Animatable(0f) }
                LaunchedEffect(Unit) {
                    while (true) {
                        tigerVibe.animateTo(
                            targetValue = -18f,
                            animationSpec = tween(
                                durationMillis = 1000, // Increased duration for slower animation
                                easing = LinearEasing
                            )
                        )
                        tigerVibe.animateTo(
                            targetValue = 18f,
                            animationSpec = tween(
                                durationMillis = 1000, // Increased duration for slower animation
                                easing = LinearEasing
                            )
                        )
                    }
                }
                Image(
                    painter = painterResource(R.drawable.tiger),
                    contentDescription = "Tiger Side",
                    modifier = Modifier
                        .size(110.dp)
                        .offset(x = tigerVibe.value.dp)
                )
            }
        }

        // Result overlay after timer ends (400ms)
        if (resultOverlayType != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000))
                    .zIndex(10f),
                contentAlignment = Alignment.Center
            ) {
                when (resultOverlayType) {
                    "dragon" -> Image(
                        painter = painterResource(R.drawable.dragon),
                        contentDescription = "Dragon Overlay",
                        modifier = Modifier.size(220.dp)
                    )
                    "tiger" -> Image(
                        painter = painterResource(R.drawable.tiger),
                        contentDescription = "Tiger Overlay",
                        modifier = Modifier.size(220.dp)
                    )
                    "draw" -> Text(
                        text = "Draw",
                        color = Color.White,
                        fontSize = 64.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // MODIFIED: Conditionally display the GameHistoryDialogTigerAndDragon
        if (showHistoryDialog) {
            GameHistoryDialogTigerAndDragon(
                onDismissRequest = { showHistoryDialog = false },
                activeTab = activeTab,
                onTabChange = { tab -> activeTab = tab }
            )
        }
    }
}

@Composable
fun UserChip(userName: String, amount: Double, avatarRes: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = userName,
            modifier = Modifier
                .size(32.dp)
                .background(Color.White, shape = CircleShape)
                .padding(1.dp)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = userName,
            color = Color(0xFF39FF14), // more opaque green
            fontSize = 10.sp,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x5539FF14), // subtle glassy effect
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 3.dp, vertical = 1.dp)
        )
        Text(
            text = String.format("%.2f", amount),
            color = Color(0xFF39FF14), // more opaque green
            fontSize = 10.sp,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x5539FF14),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 3.dp, vertical = 1.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 480)
@Composable
fun TigerAndDragonGameScreenPreview() {
    TigerAndDragonGameScreen(
        onShowTopBar = {},
        onShowBottomBar = {},
        onBackClick = {} // Provide a dummy lambda for preview
    )
}
