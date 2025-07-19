package com.weblite.kgf.ui.screens.game

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weblite.components.Wingo30BettingPopupDialog // Updated import
import com.example.weblite.components.BigSmallButton
import com.example.weblite.components.ColorButton
import com.example.weblite.components.CompactExcelTableforWingo
import com.example.weblite.components.HistoryTabButton
import com.example.weblite.components.ImageNumberButton
import com.example.weblite.components.MultiplierButton
import com.example.weblite.components.MyHistoryTableforWingo
import com.example.weblite.components.SuccessMessage
import com.weblite.kgf.Api2.PeriodIdUIEvent
import com.weblite.kgf.Api2.Resource
import kotlin.collections.forEach
import com.weblite.kgf.R
import com.weblite.kgf.ui.screens.KGFLogoText
import com.weblite.kgf.viewmodel.WingoGameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.text.format

data class NumberItem(
    val number: Int,
    val backgroundColor: Color,
    val textColor: Color = Color.White
)

// Function to get drawable resource for numbers
fun getNumberDrawable(number: Int): Int {
    return when (number) {
        0 -> R.drawable.zero
        1 -> R.drawable.one
        2 -> R.drawable.two
        3 -> R.drawable.three
        4 -> R.drawable.four
        5 -> R.drawable.five
        6 -> R.drawable.six
        7 -> R.drawable.seven
        8 -> R.drawable.eight
        9 -> R.drawable.nine
        else -> R.drawable.one
    }
}

// Function to get background color based on number
fun getNumberBackgroundColor(number: Int): Color {
    return when {
        number == 0 -> Color(0xFF9C27B0) // Purple for 0
        number == 5 -> Color(0xFF9C27B0) // Purple for 5
        number % 2 == 0 -> Color(0xFFE53935) // Red for even numbers
        else -> Color(0xFF4CAF50) // Green for odd numbers
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wingo30Screen(
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    viewModel: WingoGameViewModel = hiltViewModel()
) {
    var selectedColor by remember { mutableStateOf("") }
    var selectedNumbers by remember { mutableStateOf(setOf<Int>()) }
    var selectedColors by remember { mutableStateOf(setOf<String>()) }
    var selectedMultiplier by remember { mutableStateOf("Random") }
    var selectedBigSmall by remember { mutableStateOf("Big") }
    var selectedHistoryTab by remember { mutableStateOf("Game History") }
    var showBettingPopup by remember { mutableStateOf(false) }
    var selectedNumberForBetting by remember { mutableStateOf(0) }
    var totalBalance by remember { mutableStateOf(17510970.65) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var selectedColorForBetting by remember { mutableStateOf("Green") }
    var colorSelected by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Timer logic
    var secondsRemaining by remember { mutableIntStateOf(30) }
    var showPeriodId by remember { mutableStateOf("") }
    var serverEpochTimeBase by remember { mutableStateOf<Long?>(null) } // Server time at API fetch
    var clientTimeAtFetch by remember { mutableStateOf<Long?>(null) } // Client time when API response received
    var lastFetchedMinute by remember { mutableStateOf<Long?>(null) }

    // Collect state from ViewModel for API data
    val gameHistoryResponse by viewModel.gameHistoryResponse.collectAsStateWithLifecycle()
    val myHistoryResponse by viewModel.myHistoryResponse.collectAsStateWithLifecycle()

    // Initial fetch of period ID
    LaunchedEffect(Unit) {
        viewModel.fetchPeriodId()
    }

    // UI Update for period ID and timer synchronization
    LaunchedEffect(Unit) {
        viewModel.periodId.collect { event ->
            when (event) {
                is PeriodIdUIEvent.Success -> {
                    val datetime = event.response.result[0].dateTime
                    val current = event.response.result[0].currentTime
                    showPeriodId = datetime

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val serverTimeAtApiCall = LocalDateTime.parse(current, formatter)
                        .atZone(ZoneId.systemDefault())
                        .toEpochSecond()
                    val currentClientTime = Instant.now().epochSecond

                    // Store the server time and the client time when it was received
                    serverEpochTimeBase = serverTimeAtApiCall
                    clientTimeAtFetch = currentClientTime

                    // Reset lastFetchedMinute to ensure re-sync on new period
                    lastFetchedMinute = null
                }
                is PeriodIdUIEvent.Failure -> {
                    showPeriodId = "Error Fetching Period Id"
                }
                else -> {}
            }
        }
    }

    // Timer countdown logic
    LaunchedEffect(true) {
        while (true) {
            serverEpochTimeBase?.let { serverBase ->
                clientTimeAtFetch?.let { clientFetchTime ->
                    val now = Instant.now().epochSecond
                    // Calculate elapsed time from the server's perspective, adjusted for network latency
                    val elapsedSinceServerTime = (now - clientFetchTime) + (clientFetchTime - serverBase)

                    // Calculate seconds remaining in the current 30-second cycle
                    val calculatedSeconds = (30 - (elapsedSinceServerTime % 30)).toInt()
                    secondsRemaining = if (calculatedSeconds <= 0) 30 + calculatedSeconds else calculatedSeconds

                    // Check if a new 30-second cycle has started (or is about to start)
                    // This is for re-fetching period ID and history data
                    val currentCycleIdentifier = elapsedSinceServerTime / 30
                    if (lastFetchedMinute == null || lastFetchedMinute != currentCycleIdentifier) {
                        lastFetchedMinute = currentCycleIdentifier
                        viewModel.fetchPeriodId() // Re-fetch server time and period ID
                        viewModel.fetchGameHistory() // Refresh game history
                        viewModel.fetchMyHistory() // Refresh my history when countdown completes
                    }
                }
            }
            delay(1000) // Update every second
        }
    }

    // Show countdown overlay in last 5 seconds
    val showCountdownOverlay = secondsRemaining <= 5 && secondsRemaining > 0

    // Auto-close betting dialog when countdown overlay appears
    LaunchedEffect(showCountdownOverlay) {
        if (showCountdownOverlay && showBettingPopup) {
            android.util.Log.d("Wingo30Screen", "Auto-closing betting dialog due to countdown overlay")
            showBettingPopup = false
            colorSelected = false
        }
    }

    // Convert Game History API response to table data
    val gameHistoryData = remember(gameHistoryResponse, showPeriodId) {
        when (val response = gameHistoryResponse) {
            is Resource.Loading -> listOf(
                listOf(showPeriodId, "Loading...", "Loading...", "Loading...")
            )
            is Resource.Success -> {
                response.data?.result?.history?.map { item ->
                    listOf(
                        item.datetime,
                        if (item.numberResult.isEmpty()) "Loading..." else item.numberResult,
                        if (item.colorResult.isEmpty()) "Loading..." else item.colorResult,
                        if (item.bigSmallResult.isEmpty()) "Loading..." else item.bigSmallResult
                    )
                } ?: listOf(listOf(showPeriodId, "No Data", "No Data", "No Data"))
            }
            is Resource.Error -> listOf(
                listOf(showPeriodId, "Error", "Error", "Error")
            )
            null -> listOf(
                listOf(showPeriodId, "Loading...", "Loading...", "Loading...")
            )
            else -> listOf(
                listOf(showPeriodId, "Unknown", "Unknown", "Unknown")
            )
        }
    }

    // Convert My History API response to table data
    val myHistoryData = remember(myHistoryResponse) {
        when (val response = myHistoryResponse) {
            is Resource.Loading -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...", "Loading...")
            )
            is Resource.Success -> {
                response.data?.result?.history?.map { item ->
                    listOf(
                        item.period,
                        item.bidNum,
                        item.price,
                        item.resultStatus,
                        item.winningAmount.toString()
                    )
                } ?: listOf(listOf("No Data", "No Data", "No Data", "No Data", "No Data"))
            }
            is Resource.Error -> listOf(
                listOf("Error", "Error", "Error", "Error", "Error")
            )
            null -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...", "Loading...")
            )
            else -> listOf(
                listOf("Unknown", "Unknown", "Unknown", "Unknown", "Unknown")
            )
        }
    }

    val numberItems = remember {
        listOf(
            NumberItem(0, Color.Red),
            NumberItem(1, Color.Green),
            NumberItem(2, Color.Red),
            NumberItem(3, Color.Green),
            NumberItem(4, Color.Red),
            NumberItem(5, Color(0xFF9C27B0)),
            NumberItem(6, Color.Red),
            NumberItem(7, Color.Green),
            NumberItem(8, Color.Red),
            NumberItem(9, Color.Green)
        )
    }

    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
    }

    androidx.compose.material.Scaffold(
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
                    IconButton(onClick = onBackClick) {
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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                            text = "₹ ${String.format("%,.2f", totalBalance)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
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
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFFF6B35
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

            // Win Go 30s Card
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
                                text = "Win Go 30s",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // Game Info Card with timer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).height(140.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B35)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier, verticalArrangement = Arrangement.Center) {
                            OutlinedButton(
                                onClick = { },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("How to Play", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "Win Go 30s",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(0, 5, 1, 6, 3).forEach { number ->
                                    Image(
                                        painter = painterResource(id = getNumberDrawable(number)),
                                        contentDescription = "Number $number",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        // Dotted line separator
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(60.dp)
                                .padding(vertical = 16.dp)
                        ) {
                            Canvas(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                drawLine(
                                    color = Color.White,
                                    start = Offset(size.width / 2, 0f),
                                    end = Offset(size.width / 2, size.height),
                                    strokeWidth = 2.dp.toPx(),
                                    pathEffect = pathEffect
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Time remaining",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                // Timer display logic
                                val formatted = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60)
                                formatted.forEach { digit ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = digit.toString(),
                                            color = if (digit.toString() == ":") Color.Black else Color(
                                                0xFFFF6B35
                                            ),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = showPeriodId,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // COMBINED GAME SECTION
            item {
                Box {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Color Selection (Green, Violet, Red)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ColorButton(
                                    text = "Green",
                                    color = Color(0xFF4CAF50),
                                    isSelected = selectedColor == "Green",
                                    onClick = {
                                        if (!showCountdownOverlay) {
                                            selectedColor = "Green"
                                            colorSelected = true
                                            selectedColorForBetting = selectedColor
                                            showBettingPopup = true
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                ColorButton(
                                    text = "Violet",
                                    color = Color(0xFF9C27B0),
                                    isSelected = selectedColor == "Violet",
                                    onClick = {
                                        if (!showCountdownOverlay) {
                                            selectedColor = "Violet"
                                            colorSelected = true
                                            selectedColorForBetting = selectedColor
                                            showBettingPopup = true
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                ColorButton(
                                    text = "Red",
                                    color = Color(0xFFE53935),
                                    isSelected = selectedColor == "Red",
                                    onClick = {
                                        if (!showCountdownOverlay) {
                                            selectedColor = "Red"
                                            colorSelected = true
                                            selectedColorForBetting = selectedColor
                                            showBettingPopup = true
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Number Selection Grid - WITH POPUP TRIGGER
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(5),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.height(95.dp)
                            ) {
                                items(numberItems) { item ->
                                    ImageNumberButton(
                                        number = item.number,
                                        isSelected = selectedNumbers.contains(item.number),
                                        onClick = {
                                            if (!showCountdownOverlay) {
                                                selectedNumberForBetting = item.number
                                                colorSelected = false
                                                showBettingPopup = true
                                            }
                                        }
                                    )
                                }
                            }

                            // Multiplier Selection
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    listOf(
                                        "Random",
                                        "X1",
                                        "X5",
                                        "X10",
                                        "X50",
                                        "X100"
                                    )
                                ) { multiplier ->
                                    MultiplierButton(
                                        text = multiplier,
                                        isSelected = selectedMultiplier == multiplier,
                                        onClick = {
                                            if (!showCountdownOverlay) {
                                                selectedMultiplier = multiplier
                                            }
                                        }
                                    )
                                }
                            }

                            // Big/Small Selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BigSmallButton(
                                    text = "Big",
                                    color = Color(0xFFFFC107),
                                    isSelected = selectedBigSmall == "Big",
                                    onClick = {
                                        if (!showCountdownOverlay) {
                                            selectedBigSmall = "Big" // Set the selected Big/Small value
                                            colorSelected = true // Treat Big/Small as a color bet for popup
                                            selectedColorForBetting = selectedBigSmall // Pass to popup
                                            showBettingPopup = true // Open the betting popup
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                BigSmallButton(
                                    text = "Small",
                                    color = Color(0xFF2196F3),
                                    isSelected = selectedBigSmall == "Small",
                                    onClick = {
                                        if (!showCountdownOverlay) {
                                            selectedBigSmall = "Small" // Set the selected Big/Small value
                                            colorSelected = true // Treat Big/Small as a color bet for popup
                                            selectedColorForBetting = selectedBigSmall // Pass to popup
                                            showBettingPopup = true // Open the betting popup
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Countdown Overlay - appears only over Combined Game Section in last 5 seconds
                    if (showCountdownOverlay) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 80.dp)
                            ) {
                                // First digit (0)
                                Box(
                                    modifier = Modifier
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 20.dp, vertical = 16.dp)
                                ) {
                                    Text(
                                        text = "0",
                                        color = Color(0xFFFF6B35),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Colon
                                Text(
                                    text = ":",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                // Second digit (countdown)
                                Box(
                                    modifier = Modifier
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 20.dp, vertical = 16.dp)
                                ) {
                                    Text(
                                        text = secondsRemaining.toString(),
                                        color = Color(0xFFFF6B35),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
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
                        onClick = {
                            selectedHistoryTab = "Game History"
                            viewModel.onGameHistoryTabSelected() // Start frequent polling
                        },
                        modifier = Modifier.weight(1f)
                    )
                    HistoryTabButton(
                        text = "My History",
                        isSelected = selectedHistoryTab == "My History",
                        onClick = {
                            selectedHistoryTab = "My History"
                            viewModel.onMyHistoryTabSelected() // Stop polling and fetch my history once
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Results Table
            item {
                when (selectedHistoryTab) {
                    "Game History" -> {
                        CompactExcelTableforWingo(data = gameHistoryData)
                    }
                    "My History" -> {
                        MyHistoryTableforWingo(data = myHistoryData)
                    }
                }
            }

            // Pagination
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Previous", color = Color.White, fontSize = 14.sp)
                    }
                    Text(
                        text = "Page 1 / 2",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Next", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        // SHOW BETTING POPUP WHEN showBettingPopup IS TRUE
        if (showBettingPopup) {
            Wingo30BettingPopupDialog( // Changed to Wingo30BettingPopupDialog
                selectedNumber = if (colorSelected) null else selectedNumberForBetting,
                selectedColor = if (colorSelected) selectedColorForBetting else null,
                onDismiss = {
                    showBettingPopup = false
                    colorSelected = false // Reset color selection flag
                },
                onConfirmBet = { number, color, amount, multiplier ->
                    // The bet has already been placed successfully by the dialog's internal logic.
                    // This callback is only for UI updates on the parent screen.
                    showSuccessMessage = true
                    colorSelected = false // Reset color selection flag
                }
            )
        }

        if (showSuccessMessage) {
            SuccessMessage(
                message = "Wingo bet saved successfully.",
                isVisible = showSuccessMessage,
                onDismiss = { showSuccessMessage = false }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateSecondsRemaining(currentTimeString: String): Int {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val serverTime = LocalDateTime.parse(currentTimeString, formatter)
    val currentEpoch = serverTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    val secondsElapsed = currentEpoch % 30
    return (30 - secondsElapsed).toInt()
}
