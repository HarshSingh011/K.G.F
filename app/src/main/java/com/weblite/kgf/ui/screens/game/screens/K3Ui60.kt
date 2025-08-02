package com.weblite.kgf.ui.screens.game.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weblite.components.CompactExcelTableforK3
import com.example.weblite.components.HistoryTabButton
import com.example.weblite.components.K360BettingPopupDialog
import com.example.weblite.components.MyHistoryTableforK3
import com.example.weblite.components.SuccessMessage
import com.weblite.kgf.R
import com.weblite.kgf.ui.screens.KGFLogoText
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.data.K360GameHistoryItem
import com.weblite.kgf.data.K360MyHistoryItem
import com.weblite.kgf.data.models.games.K3PopupHistoryResponse
import com.weblite.kgf.ui.screens.game.viewmodel.K360GameViewModel
import kotlinx.coroutines.delay
import com.weblite.kgf.utils.K3Utils
import com.weblite.kgf.utils.K3Ball
import com.weblite.kgf.utils.K3BetOption
import com.weblite.kgf.ui.components.PaginationState
import com.weblite.kgf.ui.components.PaginationNavButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun K3Ui60(
    variant: String,
    onBack: () -> Unit,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    viewModel: K360GameViewModel = hiltViewModel()
) {
    // Pagination states for both tabs
    var gameHistoryPagination by remember { mutableStateOf(PaginationState()) }
    var myHistoryPagination by remember { mutableStateOf(PaginationState()) }

    // --- All state and variables at the top ---
    val k3PeriodId by viewModel.k3PeriodId.collectAsStateWithLifecycle()
    val timeRemaining by viewModel.k3TimeRemaining.collectAsStateWithLifecycle()
    val gameHistoryResource by viewModel.k3GameHistory.collectAsStateWithLifecycle()
    val myHistoryResource by viewModel.k3MyHistory.collectAsStateWithLifecycle()
    val selectedHistoryTab by viewModel.activeHistoryTab.collectAsStateWithLifecycle()

    // Data for tables (must be declared after state)
    val k3GameHistoryData = remember(gameHistoryResource) {
        when (val resource = gameHistoryResource) {
            is Resource.Loading<*> -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...")
            )
            is Resource.Success<*> -> {
                @Suppress("UNCHECKED_CAST")
                val data = (resource as? Resource.Success<List<K360GameHistoryItem>>)?.data
                data?.mapIndexed { index, historyItem ->
                    if (index == 0 && (historyItem.number.isEmpty() && historyItem.oddEven.isEmpty() && historyItem.bigSmall.isEmpty())) {
                        listOf(
                            historyItem.datetime,
                            "Loading...",
                            "Loading...",
                            "Loading..."
                        )
                    } else {
                        listOf(
                            historyItem.datetime,
                            historyItem.number,
                            historyItem.oddEven,
                            historyItem.bigSmall
                        )
                    }
                } ?: listOf(listOf("No Data", "No Data", "No Data", "No Data"))
            }
            is Resource.Error<*> -> listOf(
                listOf("Error", "Error", "Error", "Error")
            )
            else -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...")
            )
        }
    }

    val k3MyHistoryData = remember(myHistoryResource) {
        when (val resource = myHistoryResource) {
            is Resource.Loading<*> -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...", "Loading...")
            )
            is Resource.Success<*> -> {
                @Suppress("UNCHECKED_CAST")
                val data = (resource as? Resource.Success<List<K360MyHistoryItem>>)?.data
                data?.map { historyItem ->
                    listOf(
                        historyItem.period,
                        historyItem.bidNum,
                        historyItem.price,
                        historyItem.status,
                        historyItem.totalamount
                    )
                } ?: listOf(listOf("No Data", "No Data", "No Data", "No Data", "No Data"))
            }
            is Resource.Error<*> -> listOf(
                listOf("Error", "Error", "Error", "Error", "Error")
            )
            else -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...", "Loading...")
            )
        }
    }

    // Update totalPages when data changes
    LaunchedEffect(k3GameHistoryData) {
        val totalPages = (k3GameHistoryData.size + gameHistoryPagination.itemsPerPage - 1) / gameHistoryPagination.itemsPerPage
        if (gameHistoryPagination.totalPages != totalPages) {
            gameHistoryPagination = gameHistoryPagination.copy(totalPages = totalPages, currentPage = 1)
        }
    }
    LaunchedEffect(k3MyHistoryData) {
        val totalPages = (k3MyHistoryData.size + myHistoryPagination.itemsPerPage - 1) / myHistoryPagination.itemsPerPage
        if (myHistoryPagination.totalPages != totalPages) {
            myHistoryPagination = myHistoryPagination.copy(totalPages = totalPages, currentPage = 1)
        }
    }
    // ...existing code...

    var displayedDice by remember { mutableStateOf(listOf(1, 1, 1)) }
    var lastTimeRemainingForDice by remember { mutableStateOf(timeRemaining) }
    var showBettingPopup by remember { mutableStateOf(false) }
    var selectedNumberForBetting by remember { mutableStateOf<Int?>(null) }
    var selectedBetTypeForBetting by remember { mutableStateOf<String?>(null) }
    var totalBalance by remember { mutableStateOf(0.0) }
    var balanceString by remember { mutableStateOf("0.00") }
    var lastGoodBalanceString by remember { mutableStateOf("0.00") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showWinDialog by remember { mutableStateOf(false) }
    var winDialogData by remember { mutableStateOf<K3PopupHistoryResponse?>(null) }
    val popupHistoryResponse by viewModel.popupHistoryResponse.collectAsStateWithLifecycle()
    val mainViewModel: com.weblite.kgf.Api2.MainViewModel = hiltViewModel()
    val dashboardState = mainViewModel.dashboardState.value
    val userId = com.weblite.kgf.Api2.SharedPrefManager.getString("user_id", "0")
    var lastTimeRemaining by remember { mutableStateOf(0L) }
    val showCountdownOverlay = timeRemaining <= 10000L && timeRemaining > 0L

    val k3Balls = listOf(
        K3Ball(3, "207.36X", Color(0xFFE53935)),
        K3Ball(4, "69.12X", Color(0xFF4CAF50)),
        K3Ball(5, "34.56X", Color(0xFFE53935)),
        K3Ball(6, "20.74X", Color(0xFF4CAF50)),
        K3Ball(7, "13.83X", Color(0xFFE53935)),
        K3Ball(8, "9.88X", Color(0xFF4CAF50)),
        K3Ball(9, "8.3X", Color(0xFFE53935)),
        K3Ball(10, "7.64X", Color(0xFF4CAF50)),
        K3Ball(11, "7.64X", Color(0xFFE53935)),
        K3Ball(12, "8.3X", Color(0xFF4CAF50)),
        K3Ball(13, "9.88X", Color(0xFFE53935)),
        K3Ball(14, "13.83X", Color(0xFF4CAF50)),
        K3Ball(15, "20.74X", Color(0xFFE53935)),
        K3Ball(16, "34.56X", Color(0xFF4CAF50)),
        K3Ball(17, "69.12X", Color(0xFFE53935)),
        K3Ball(18, "207.36X", Color(0xFF4CAF50))
    )



    // --- All helper functions below variables ---
    fun updateDiceFromHistory() {
        // Use the second element (index 1) of the game history list for dice logic, like K3Ui30
        val (period, numberRaw, itemRaw) = when (val resource = gameHistoryResource) {
            is Resource.Success<*> -> {
                @Suppress("UNCHECKED_CAST")
                val item = (resource as? Resource.Success<List<K360GameHistoryItem>>)?.data?.getOrNull(1)
                if (item != null) {
                    Triple(item.datetime, item.number, item)
                } else Triple(null, null, null)
            }
            else -> Triple(null, null, null)
        }
        android.util.Log.d("Dice_Number", "Fetched history item (index 1): period=$period, number=$numberRaw, rawItem=$itemRaw")
        val bidNum = numberRaw?.toIntOrNull()
        if (bidNum != null && numberRaw != "Loading...") {
            val diceList = com.weblite.kgf.utils.DiceUtils.getDiceValues(bidNum)
            android.util.Log.d("Dice_Number", "Dice generated from number/bidNum $bidNum: $diceList")
            diceList.forEachIndexed { idx, value ->
                android.util.Log.d("Dice_Number", "Dice ${idx+1}: value=$value, drawable=${com.weblite.kgf.utils.DiceUtils.getDiceDrawable(value)}")
            }
            displayedDice = diceList
        } else {
            android.util.Log.d("Dice_Number", "No valid number/bidNum found, keeping previous dice: $displayedDice")
        }
    }

    // --- All LaunchedEffects and logic below functions ---
    LaunchedEffect(Unit) {
        viewModel.fetchK3GameHistory()
        updateDiceFromHistory()
    }

    LaunchedEffect(timeRemaining, gameHistoryResource) {
        if (lastTimeRemainingForDice < timeRemaining) {
            viewModel.fetchK3GameHistory()
            // Wait for gameHistoryResource to update, then update dice
            // (gameHistoryResource is a StateFlow, so this effect will rerun)
        }
        updateDiceFromHistory()
        lastTimeRemainingForDice = timeRemaining
    }

    LaunchedEffect(Unit) {
        mainViewModel.fetchDashboard(userId)
    }

    LaunchedEffect(dashboardState) {
        val successState = dashboardState as? com.weblite.kgf.Api2.Resource.Success<*>
        val data = successState?.data
        val validBalance = try {
            val resultField = data?.javaClass?.getDeclaredField("result")
            resultField?.isAccessible = true
            val resultObj = resultField?.get(data)
            val totalBalanceField = resultObj?.javaClass?.getDeclaredField("totalBalance")
            totalBalanceField?.isAccessible = true
            totalBalanceField?.get(resultObj) as? String
        } catch (e: Exception) {
            null
        }
        if (validBalance != null && validBalance != "0.00") {
            balanceString = validBalance
            lastGoodBalanceString = validBalance
            totalBalance = validBalance.toDoubleOrNull() ?: 0.0
        } else {
            balanceString = lastGoodBalanceString
        }
    }

    LaunchedEffect(timeRemaining) {
        if (lastTimeRemaining < timeRemaining) {
            mainViewModel.fetchDashboard(userId)
        }
        lastTimeRemaining = timeRemaining
    }

    LaunchedEffect(popupHistoryResponse) {
        android.util.Log.d("Full popup history API response", "$popupHistoryResponse")
        android.util.Log.d("K3WinDialog", "popupHistoryResponse: $popupHistoryResponse")
        val winAmount = popupHistoryResponse?.result?.total_winning_amount ?: 0.0
        android.util.Log.d("K3WinDialog", "total_winning_amount: $winAmount (from result.total_winning_amount)")
        if (winAmount > 0.0) {
            winDialogData = popupHistoryResponse
            showWinDialog = true
            android.util.Log.d("K3WinDialog", "Showing Win Dialog, total_winning_amount: $winAmount")
            delay(2000)
            showWinDialog = false
        } else {
            showWinDialog = false
            android.util.Log.d("K3WinDialog", "Not showing Win Dialog, total_winning_amount: $winAmount")
        }
    }

    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            delay(400)
            showSuccessMessage = false
        }
    }

    LaunchedEffect(showCountdownOverlay) {
        if (showCountdownOverlay && showBettingPopup) {
            android.util.Log.d("K3Ui60", "Auto-closing betting dialog due to countdown overlay")
            showBettingPopup = false
            selectedNumberForBetting = null
            selectedBetTypeForBetting = null
        }
    }

    val betOptions = listOf(
        K3BetOption("Big", "1.92X", Color(0xFFE53935)),
        K3BetOption("Small", "1.92X", Color(0xFF4CAF50)),
        K3BetOption("Odd", "1.92X", Color(0xFFFFC107)),
        K3BetOption("Even", "1.92X", Color(0xFF2196F3))
    )

    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(52.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
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
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 12.dp)
            ) {
                // Wallet Balance Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RectangleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "₹ $balanceString",
                                fontSize = 24.sp,
                                fontWeight = Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Wallet Balance",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFFE53935
                                        )
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                ) {
                                    Text("Withdraw", color = Color.White, fontSize = 14.sp)
                                }
                                Button(
                                    onClick = { },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF4CAF50
                                        )
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                ) {
                                    Text("Deposit", color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                // K3 Lotre 60s Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Card(
                                modifier = Modifier,
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B35)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "K3 Lotre 60sec",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // Game Info Card with timer and dice
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Period and Time Remaining Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Period",
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Text(
                                        text = k3PeriodId ?: "Loading...",
                                        color = Color.Black,
                                        fontSize = 18.sp,
                                        fontWeight = Bold
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Time Remaining",
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Text(
                                        text = String.format("00:%02d", timeRemaining / 1000),
                                        color = Color(0xFFFF6B35),
                                        fontSize = 18.sp,
                                        fontWeight = Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(
                                        color = Color(0xFF39C780),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            color = Color(0xFF2E7D32),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        displayedDice.forEachIndexed { index, diceValue ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .background(
                                                        Color.LightGray,
                                                        RoundedCornerShape(12.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(id = com.weblite.kgf.utils.DiceUtils.getDiceDrawable(diceValue)),
                                                    contentDescription = "Dice ${index + 1}",
                                                    modifier = Modifier.size(55.dp),
                                                    contentScale = ContentScale.Fit
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // COMBINED GAME SECTION - Balls and Bet Options
                item {
                    Box {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(4),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.height(330.dp),
                                    userScrollEnabled = false
                                ) {
                                    items(k3Balls) { ball ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable {
                                                if (!showCountdownOverlay) {
                                                    selectedNumberForBetting = ball.number
                                                    selectedBetTypeForBetting = null
                                                    showBettingPopup = true
                                                }
                                            }
                                        ) {
                                            Image(
                                                painter = painterResource(id = K3Utils.getK3BallDrawable(ball.number)),
                                                contentDescription = "Ball ${ball.number}",
                                                modifier = Modifier
                                                    .size(45.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = ball.multiplier,
                                                fontSize = 15.sp,
                                                fontWeight = Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    betOptions.forEach { option ->
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    if (!showCountdownOverlay) {
                                                        selectedNumberForBetting = null
                                                        selectedBetTypeForBetting = option.name
                                                        showBettingPopup = true
                                                    }
                                                },
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        option.backgroundColor,
                                                        RoundedCornerShape(8.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = option.name,
                                                        color = Color.White,
                                                        fontWeight = Bold,
                                                        fontSize = 16.sp
                                                    )
                                                    Text(
                                                        text = option.multiplier,
                                                        color = Color.White,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (showCountdownOverlay) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 80.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 20.dp, vertical = 16.dp)
                                    ) {
                                        Text(
                                            text = "0",
                                            color = Color(0xFFFF6B35),
                                            fontSize = 32.sp,
                                            fontWeight = Bold
                                        )
                                    }

                                    Text(
                                        text = ":",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = Bold
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(Color.White, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 20.dp, vertical = 16.dp)
                                    ) {
                                        Text(
                                            text = (timeRemaining / 1000).toString(),
                                            color = Color(0xFFFF6B35),
                                            fontSize = 32.sp,
                                            fontWeight = Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // History Tabs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HistoryTabButton(
                            text = "Game History",
                            isSelected = selectedHistoryTab == "Game History",
                            onClick = { viewModel.setActiveHistoryTab("Game History") },
                            modifier = Modifier.weight(1f)
                        )
                        HistoryTabButton(
                            text = "My History",
                            isSelected = selectedHistoryTab == "My History",
                            onClick = { viewModel.setActiveHistoryTab("My History") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Results Table
                item {
                    when (selectedHistoryTab) {
                        "Game History" -> {
                            CompactExcelTableforK3(data = gameHistoryPagination.pagedItems(k3GameHistoryData))
                        }

                        "My History" -> {
                            MyHistoryTableforK3(data = myHistoryPagination.pagedItems(k3MyHistoryData))
                        }
                    }
                }

                // Pagination
                item {
                    val (pagination, setPagination) = when (selectedHistoryTab) {
                        "Game History" -> Pair(gameHistoryPagination, { p: PaginationState -> gameHistoryPagination = p })
                        "My History" -> Pair(myHistoryPagination, { p: PaginationState -> myHistoryPagination = p })
                        else -> Pair(gameHistoryPagination, { p: PaginationState -> gameHistoryPagination = p })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PaginationNavButton(
                            text = "Previous",
                            onClick = {
                                if (pagination.currentPage > 1) setPagination(pagination.copy(currentPage = pagination.currentPage - 1))
                            },
                            enabled = pagination.currentPage > 1,
                            isPrimary = false
                        )
                        Text(
                            text = "Page ${pagination.currentPage} / ${pagination.totalPages}",
                            fontSize = 16.sp,
                            fontWeight = Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        PaginationNavButton(
                            text = "Next",
                            onClick = {
                                if (pagination.currentPage < pagination.totalPages) setPagination(pagination.copy(currentPage = pagination.currentPage + 1))
                            },
                            enabled = pagination.currentPage < pagination.totalPages,
                            isPrimary = true
                        )
                    }
                }
            }
        }

        LaunchedEffect(showSuccessMessage) {
            if (showSuccessMessage) {
                delay(400)
                showSuccessMessage = false
            }
        }

        if (showBettingPopup) {
            K360BettingPopupDialog(
                selectedNumber = selectedNumberForBetting,
                selectedBetType = selectedBetTypeForBetting,
                k3PeriodId = k3PeriodId,
                onDismiss = {
                    showBettingPopup = false
                    selectedNumberForBetting = null
                    selectedBetTypeForBetting = null
                },
                onConfirmBet = { isSuccess, errorMessage ->
                    if (isSuccess) {
                        showSuccessMessage = true
                        viewModel.fetchK3MyHistory()
                        mainViewModel.fetchDashboard(userId)
                    } else {
                        android.util.Log.e("K3Ui60", "Bet placement failed: $errorMessage")
                    }
                }
            )
        }

        if (showWinDialog && winDialogData != null) {
            val latest = winDialogData?.result?.getLatestWingoData?.firstOrNull()
            val winAmount = winDialogData?.result?.total_winning_amount ?: 0.0
            com.example.windialog.WinDialog(
                isVisible = showWinDialog,
                onDismiss = { showWinDialog = false },
                winAmount = "₹$winAmount",
                period = latest?.datetime ?: "",
                periodNumber = latest?.id ?: "",
                resultColors = listOfNotNull(
                    latest?.bidNum,
                    latest?.bidOddEven,
                    latest?.bidBigSmall
                ),
                autoCloseSeconds = 3
            )
        }

        if (showSuccessMessage) {
            SuccessMessage(
                message = "K3 bet saved successfully.",
                isVisible = showSuccessMessage,
                onDismiss = { showSuccessMessage = false }
            )
        }
    }
}