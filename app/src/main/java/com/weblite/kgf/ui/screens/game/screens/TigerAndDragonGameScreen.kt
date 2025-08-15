package com.weblite.kgf.ui.screens.game.screens

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.graphicsLayer
import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.weblite.kgf.ui.screens.game.viewmodel.TigerAndDragonViewModel
import com.weblite.kgf.data.models.games.TigerGameHistoryResponse
import com.weblite.kgf.Api.Resource
import com.weblite.kgf.PreviewInterface.ITigerAndDragonViewModel
import com.weblite.kgf.data.models.games.MyHistoryApiResponse
import com.weblite.kgf.data.models.games.TigerGameHistoryResult
import com.weblite.kgf.data.models.games.TigerPeriodIdResponse
import com.weblite.kgf.ui.components.GameHistoryDialogTigerAndDragon
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@Composable
fun TigerAndDragonGameScreen(
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    viewModel: TigerAndDragonViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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

    // State for placed coins in each section (visible on UI)
    var dragonCoins by remember { mutableStateOf(listOf<Int>()) }
    var tigerCoins by remember { mutableStateOf(listOf<Int>()) }
    var tieCoins by remember { mutableStateOf(listOf<Int>()) }

    // State for my history
    val myHistoryState by viewModel.myHistory.collectAsState()

    // Track all coins placed (even those not visible)
    var dragonAllCoins by remember { mutableStateOf(listOf<Int>()) }
    var tigerAllCoins by remember { mutableStateOf(listOf<Int>()) }
    var tieAllCoins by remember { mutableStateOf(listOf<Int>()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Define chipValues here so it's available for addCoinToSection
    val chipValues = listOf(10, 50, 500, 1000, 5000)

    // Betting state - disable betting when timer ends until new Period ID is updated
    var isBettingEnabled by remember { mutableStateOf(true) }
    
    // Track last period ID to detect when new period is updated
    var lastPeriodId by remember { mutableStateOf("") }

    // Popup state for bet placed
    var showBetPlacedPopup by remember { mutableStateOf(false) }
    var betPlacedText by remember { mutableStateOf("") }
    var betPlacedPopupKey by remember { mutableStateOf(0) }

    // Handle app lifecycle to refresh data when returning from background
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh data when app resumes from background
                viewModel.fetchPeriodId()
                viewModel.fetchGameHistory()
                viewModel.fetchMyHistory()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Helper to add coin to a section and call bet API immediately
    fun addCoinToSection(section: String) {
        // Check if betting is enabled before allowing bet placement
        if (!isBettingEnabled) {
            Log.d("TigerAndDragonGameScreen", "Betting blocked - waiting for new Period ID")
            return
        }
        
        val value = chipValues.getOrNull(selectedChipIndex) ?: return
        when (section) {
            "dragon" -> {
                dragonAllCoins = dragonAllCoins + value
                dragonCoins = dragonAllCoins
                scope.launch {
                    viewModel.placeBet("dragon", value)
                    viewModel.fetchMyHistory()
                }
                betPlacedText = "Bet placed on Dragon: ₹$value"
                showBetPlacedPopup = true
                betPlacedPopupKey++
            }
            "tiger" -> {
                tigerAllCoins = tigerAllCoins + value
                tigerCoins = tigerAllCoins
                scope.launch {
                    viewModel.placeBet("tiger", value)
                    viewModel.fetchMyHistory()
                }
                betPlacedText = "Bet placed on Tiger: ₹$value"
                showBetPlacedPopup = true
                betPlacedPopupKey++
            }
            "tie" -> {
                tieAllCoins = tieAllCoins + value
                tieCoins = tieAllCoins
                scope.launch {
                    viewModel.placeBet("tie", value)
                    viewModel.fetchMyHistory()
                }
                betPlacedText = "Bet placed on Tie: ₹$value"
                showBetPlacedPopup = true
                betPlacedPopupKey++
            }
        }
        Log.d("TigerAndDragonGameScreen", "Placed coin $value on $section and called bet API.")
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

    // Initial fetch and setup
    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
        Log.d("TigerAndDragonGameScreen", "Initial fetch: Period ID and Game History.")
        viewModel.fetchPeriodId()
        viewModel.fetchGameHistory()
    }

    // Handle period ID updates and control betting state
    LaunchedEffect(Unit) {
        viewModel.periodId.collect { resourceEvent ->
            when (resourceEvent) {
                is Resource.Success<*> -> {
                    val response = resourceEvent.data
                    if (response is TigerPeriodIdResponse && response.result != null && response.result.isNotEmpty()) {
                        val datetime = response.result[0].periodId
                        if (showPeriodId != datetime) {
                            // New period ID received - enable betting
                            if (lastPeriodId.isNotEmpty() && lastPeriodId != datetime) {
                                isBettingEnabled = true
                                Log.d("TigerAndDragonGameScreen", "New Period ID received - Betting ENABLED")
                            }
                            lastPeriodId = datetime
                            showPeriodId = datetime
                            Log.d("TigerAndDragonGameScreen", "Period ID updated: $datetime")
                        }
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
                    if (showPeriodId.isEmpty()) showPeriodId = "Fetching Period Id..."
                    Log.d("TigerAndDragonGameScreen", "Fetching Period ID (Loading state).")
                }
                else -> {
                    showPeriodId = "Unknown State"
                }
            }
        }
    }

    // Handle screen orientation and status bar
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        // Hide status bar immediately and ensure it stays hidden
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val window = activity?.window
            val decorView = window?.decorView
            val controller = decorView?.windowInsetsController
            controller?.hide(android.view.WindowInsets.Type.statusBars())
            controller?.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            Log.d("TigerAndDragonGameScreen", "Status bar hidden")
            
            onDispose {
                // Restore orientation and status bar when leaving
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                controller?.show(android.view.WindowInsets.Type.statusBars())
                Log.d("TigerAndDragonGameScreen", "Status bar restored")
            }
        } else {
            @Suppress("DEPRECATION")
            activity?.window?.decorView?.systemUiVisibility = 
                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            Log.d("TigerAndDragonGameScreen", "Status bar hidden (legacy)")
            
            onDispose {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                @Suppress("DEPRECATION")
                activity?.window?.decorView?.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_VISIBLE
                Log.d("TigerAndDragonGameScreen", "Status bar restored (legacy)")
            }
        }
    }

    val timerEnded by viewModel.timerEnded.collectAsState()
    val winResult by viewModel.winResult.collectAsState()
    val showWinResult by viewModel.showWinResult.collectAsState()
    
    // Display state for WinDialog - independent from GameHistory dialog
    var displayWinResult by remember { mutableStateOf<String?>(null) }
    var isWinDialogFromTimer by remember { mutableStateOf(false) }
    var winDialogTriggerKey by remember { mutableStateOf(0) } // Key to trigger separate dismissal effect
    
    // Clear WinDialog when GameHistory dialog opens/closes to prevent interference
    LaunchedEffect(showHistoryDialog) {
        if (showHistoryDialog) {
            Log.d("TigerAndDragonGameScreen", "GameHistory dialog opened - clearing any existing WinDialog")
            displayWinResult = null
            isWinDialogFromTimer = false
            winDialogTriggerKey = 0 // Reset trigger key
        } else {
            Log.d("TigerAndDragonGameScreen", "GameHistory dialog closed - ensuring WinDialog is cleared")
            displayWinResult = null
            isWinDialogFromTimer = false
            winDialogTriggerKey = 0 // Reset trigger key
        }
    }

    // When timer ends, disable betting and start API sequence
    LaunchedEffect(timerEnded) {
        if (timerEnded) {
            Log.d("TigerAndDragonGameScreen", "Timer ended - DISABLING betting until new Period ID")
            // Disable betting immediately when timer ends
            isBettingEnabled = false
            
            Log.d("TigerAndDragonGameScreen", "Timer ended - starting API sequence")
            // Set flag to indicate this will be a timer-triggered WinDialog
            isWinDialogFromTimer = true
            // First fetch the latest periodId (for betting control only, timer updates independently)
            viewModel.fetchPeriodId()
            Log.d("TigerAndDragonGameScreen", "Fetched period ID after timer ended")
            // Wait a moment to ensure periodId is updated
            delay(500)
            // Now fetch the win result (WinDialog API)
            viewModel.fetchWinResult()
            Log.d("TigerAndDragonGameScreen", "Fetched win result after timer ended")
        }
    }

    // WinDialog trigger logic - only checks conditions and sets display state
    LaunchedEffect(showWinResult, winResult) {
        Log.d("TigerAndDragonGameScreen", "showWinResult changed to: $showWinResult, winResult: $winResult")
        
        if (showWinResult && winResult != null && isWinDialogFromTimer && !showHistoryDialog) {
            Log.d("TigerAndDragonGameScreen", "WinDialog triggered for: $winResult (Timer-based)")
            
            // Clear all coins immediately
            dragonCoins = emptyList(); tigerCoins = emptyList(); tieCoins = emptyList()
            dragonAllCoins = emptyList(); tigerAllCoins = emptyList(); tieAllCoins = emptyList()
            selectedChipIndex = -1
            
            // Show the dialog
            displayWinResult = winResult
            Log.d("TigerAndDragonGameScreen", "WinDialog displayed for result: $winResult")
            
            // Hide the ViewModel result immediately
            viewModel.hideWinResult()
            
            // Trigger the separate dismissal effect
            winDialogTriggerKey++
            
        } else if (showWinResult && winResult != null && !isWinDialogFromTimer) {
            Log.d("TigerAndDragonGameScreen", "WinDialog blocked - triggered by dialog, not timer")
            // Just hide the ViewModel result without showing dialog
            viewModel.hideWinResult()
        } else if (showWinResult && winResult != null && showHistoryDialog) {
            Log.d("TigerAndDragonGameScreen", "WinDialog blocked - GameHistory dialog is open")
            // Just hide the ViewModel result without showing dialog
            viewModel.hideWinResult()
        }
    }
    
    // Separate LaunchedEffect for WinDialog dismissal - runs independently of timer
    LaunchedEffect(winDialogTriggerKey) {
        if (winDialogTriggerKey > 0 && displayWinResult != null) {
            val currentResult = displayWinResult
            Log.d("TigerAndDragonGameScreen", "Starting dismissal timer for: $currentResult")
            
            // Auto-dismiss after 500ms - this runs independently
            delay(500)
            
            if (displayWinResult == currentResult) { // Only dismiss if it's still the same result
                displayWinResult = null
                Log.d("TigerAndDragonGameScreen", "WinDialog dismissed after 500ms for: $currentResult")
                
                // Reset flag immediately when dialog dismisses
                isWinDialogFromTimer = false
                
                // Timer updates independently via ViewModel's fetchPeriodIdAndSyncTimer()
                // No need to force timer update here - it will happen automatically
                Log.d("TigerAndDragonGameScreen", "WinDialog cycle completed for: $currentResult - timer updates independently")
            }
        }
    }
    
    // Clear coins when timer ends for new game cycle
    LaunchedEffect(timerEnded) {
        if (timerEnded) {
            Log.d("TigerAndDragonGameScreen", "Timer ended - clearing coins and preparing for next cycle")
            // Clear coins
            dragonCoins = emptyList(); tigerCoins = emptyList(); tieCoins = emptyList()
            dragonAllCoins = emptyList(); tigerAllCoins = emptyList(); tieAllCoins = emptyList()
            selectedChipIndex = -1
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
        // Bet placed popup
        if (showBetPlacedPopup) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp)
                    .zIndex(100f),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1B263B), shape = RoundedCornerShape(24.dp))
                        .border(2.dp, Color.White, RoundedCornerShape(24.dp))
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = betPlacedText,
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            // Hide popup after 1.2 seconds, using unique key for each placement
            LaunchedEffect(betPlacedPopupKey) {
                delay(400)
                showBetPlacedPopup = false
            }
        }
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
                }
            }

    // ...removed My History UI from main screen, now only in dialog...
            val overlayHeight = maxHeight * 0.75f
            val overlayWidth = maxWidth * 0.18f

            // Main game table area (increased size and better positioning)
            val tableWidth = maxWidth * 0.78f
            val tableHeight = maxHeight * 0.75f
            val borderThickness = tableWidth * 0.04f // Slightly reduced border
            val chipSize = tableWidth * 0.10f
            val avatarSize = chipSize * 0.8f
            val cornerRadius = tableHeight * 0.5f
            val borderCornerRadius = tableHeight * 0.38f

            Box(
                modifier = Modifier
                    .size(tableWidth, tableHeight)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = maxHeight * 0.16f) // Adjusted bottom padding
            ) {
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
                                    .clickable(enabled = isBettingEnabled) { addCoinToSection("dragon") }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painterResource(R.drawable.dragon),
                                        contentDescription = "Dragon",
                                        modifier = Modifier
                                            .size(avatarSize * 2.0f)
                                            .alpha(0.65f)
                                    )
                                    Spacer(modifier = Modifier.height(tableHeight * 0.01f))
                                    Text(
                                        text = "Dragon\n1:2",
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontSize = (tableHeight * 0.055f).value.sp,
                                        lineHeight = (tableHeight * 0.06f).value.sp,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(top = 0.dp)
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
                                    .clickable(enabled = isBettingEnabled) { addCoinToSection("tie") }
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
                                    .clickable(enabled = isBettingEnabled) { addCoinToSection("tiger") }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painterResource(R.drawable.tiger),
                                        contentDescription = "Tiger",
                                        modifier = Modifier
                                            .size(avatarSize * 2.0f)
                                            .alpha(0.65f)
                                    )
                                    Spacer(modifier = Modifier.height(tableHeight * 0.01f))
                                    Text(
                                        text = "Tiger\n1:2",
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontSize = (tableHeight * 0.055f).value.sp,
                                        lineHeight = (tableHeight * 0.06f).value.sp,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(top = 0.dp)
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
                    .height(overlayHeight * 1.0f)
                    .width(overlayWidth * 1.5f)
                    .padding(start = 1.dp, bottom = maxHeight * 0.13f)
                    .align(Alignment.BottomStart)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top: User 1 (closer to table)
                    UserChip(
                        "User 1", 500.0, R.drawable.user1,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.TopEnd)
                            .padding(top = overlayHeight * 0.02f, end = overlayWidth * 0.60f)
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
                    // Bottom: User 3 (closer to table)
                    UserChip(
                        "User 3", 100.0, R.drawable.user3,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.BottomEnd)
                            .padding(bottom = overlayHeight * 0.02f, end = overlayWidth * 0.60f)
                    )
                }
            }

            // Right user chips overlay (move closer to table)
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .height(overlayHeight * 1.0f)
                    .width(overlayWidth * 1.5f)
                    .padding(end = 1.dp, bottom = maxHeight * 0.16f)
                    .align(Alignment.BottomEnd)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top: User 4 (closer to table)
                    UserChip(
                        "User 4", 500.0, R.drawable.user4,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.TopStart)
                            .padding(top = overlayHeight * 0.02f, start = overlayWidth * 0.60f)
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
                    // Bottom: User 6 (closer to table)
                    UserChip(
                        "User 6", 100.0, R.drawable.user6,
                        Modifier
                            .width(overlayWidth * 1.1f)
                            .wrapContentHeight()
                            .align(Alignment.BottomStart)
                            .padding(bottom = overlayHeight * 0.02f, start = overlayWidth * 0.60f)
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
                        .height(55.dp)
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
                    .fillMaxWidth(0.60f)
                    .height(60.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFB3C7F7),
                                Color(0xFFB39DDB)
                            )
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally)
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
                            targetValue = if (isSelected) 1.2f else 1f,
                            label = "chipScale"
                        )
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable { selectedChipIndex = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(chipImages[idx % chipImages.size]),
                                contentDescription = "Chip $value",
                                modifier = Modifier.size(54.dp)
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

                // Column containing timer and period box
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Timer box
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
                            .padding(horizontal = 32.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Use ViewModel timer instead of local timer
                        Text(
                            text = secondsRemaining.toString(),
                            color = Color(0xFF1B263B),
                            fontSize = 28.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Period box
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF1976D2), // Blue left
                                        Color(0xFF64B5F6)  // Blue right
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (showPeriodId.isNotEmpty()) "Period: $showPeriodId" else "Period: ...",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        )
                    }
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
        
        // WinDialog - only shows result when timer completes
        if (displayWinResult != null) {
            androidx.compose.material3.Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000))
                    .zIndex(10f),
                color = Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    when (displayWinResult?.lowercase()) {
                        "dragon" -> Image(
                            painter = painterResource(R.drawable.dragon),
                            contentDescription = "Dragon Wins",
                            modifier = Modifier.size(180.dp)
                        )
                        "tiger" -> Image(
                            painter = painterResource(R.drawable.tiger),
                            contentDescription = "Tiger Wins",
                            modifier = Modifier.size(180.dp)
                        )
                        "draw", "tie" -> Text(
                            text = "It's a Draw!",
                            color = Color.White,
                            fontSize = 38.sp
                        )
                        else -> Text(
                            text = "No Result",
                            color = Color.White,
                            fontSize = 32.sp
                        )
                    }
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
        modifier = modifier
            .padding(vertical = 2.dp), // Add vertical padding to prevent overlap
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = userName,
            modifier = Modifier
                .size(24.dp) // Smaller image
                .background(Color.White, shape = CircleShape)
                .padding(1.dp)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "$userName\n₹${String.format("%.2f", amount)}", // Combined text in two lines
            color = Color(0xFF39FF14), // more opaque green
            fontSize = 8.sp, // Smaller font
            lineHeight = 9.sp, // Tight line spacing
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x5539FF14), // subtle glassy effect
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(3.dp)
                )
                .padding(horizontal = 2.dp, vertical = 1.dp) // Smaller padding
        )
    }
}


class PreviewTigerAndDragonViewModel : ITigerAndDragonViewModel {
    override val secondsRemaining: StateFlow<Int> = MutableStateFlow(10)
    override val gameHistory: StateFlow<Resource<TigerGameHistoryResponse?>> =
        MutableStateFlow(Resource.Success(TigerGameHistoryResponse(result = null, status = "", statusCode = 200, msg = "")))
    override val timerEnded: StateFlow<Boolean> = MutableStateFlow(false)
    override val periodId: SharedFlow<Resource<TigerPeriodIdResponse>> = MutableSharedFlow()
    override fun startGameHistoryPolling() {}
    override fun fetchGameHistory() {}
    override fun stopGameHistoryPolling() {}
    override fun fetchMyHistory() {}
    override fun fetchPeriodId() {}
    override fun placeBet(type: String, amount: Int) {}
}


