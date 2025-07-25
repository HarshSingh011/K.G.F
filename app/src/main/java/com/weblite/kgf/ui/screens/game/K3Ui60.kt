package com.weblite.kgf.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
import com.weblite.kgf.viewmodel.K360GameViewModel
import com.weblite.kgf.ui.screens.game.getK3BallDrawable // Import from K3Common
import com.weblite.kgf.utils.formatTimestamp // Import from K3GameUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun K3Ui60(
    variant: String,
    onBack: () -> Unit,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    viewModel: K360GameViewModel = hiltViewModel()
) {
    var showBettingPopup by remember { mutableStateOf(false) }
    var selectedNumberForBetting by remember { mutableStateOf<Int?>(null) }
    var selectedBetTypeForBetting by remember { mutableStateOf<String?>(null) }
    var totalBalance by remember { mutableStateOf(17436677.65) } // This should ideally come from a ViewModel or user session
    var showSuccessMessage by remember { mutableStateOf(false) }

    val k3PeriodId by viewModel.k3PeriodId.collectAsStateWithLifecycle()
    val timeRemaining by viewModel.k3TimeRemaining.collectAsStateWithLifecycle() // Use k3TimeRemaining

    val gameHistoryResource by viewModel.k3GameHistory.collectAsStateWithLifecycle() // Use k3GameHistory
    val myHistoryResource by viewModel.k3MyHistory.collectAsStateWithLifecycle() // Observe my history
    val selectedHistoryTab by viewModel.activeHistoryTab.collectAsStateWithLifecycle() // Use activeHistoryTab

    val showCountdownOverlay = timeRemaining <= 10000L && timeRemaining > 0L // 10 seconds in millis

    LaunchedEffect(showCountdownOverlay) {
        if (showCountdownOverlay && showBettingPopup) {
            android.util.Log.d("K3Ui60", "Auto-closing betting dialog due to countdown overlay")
            showBettingPopup = false
            selectedNumberForBetting = null
            selectedBetTypeForBetting = null
        }
    }

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

    val betOptions = listOf(
        K3BetOption("Big", "1.92X", Color(0xFFE53935)),
        K3BetOption("Small", "1.92X", Color(0xFF4CAF50)),
        K3BetOption("Odd", "1.92X", Color(0xFFFFC107)),
        K3BetOption("Even", "1.92X", Color(0xFF2196F3))
    )

    val k3GameHistoryData = remember(gameHistoryResource) {
        when (val resource = gameHistoryResource) {
            is Resource.Loading -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...")
            )
            is Resource.Success -> {
                resource.data?.mapIndexed { index, historyItem ->
                    if (index == 0 && historyItem.number.isEmpty() && historyItem.oddEven.isEmpty() && historyItem.bigSmall.isEmpty()) {
                        listOf(
                            historyItem.datetime,
                            "Loading...",
                            "Loading...",
                            "Loading..."
                        )
                    } else {
                        listOf(
                            historyItem.datetime, // Period ID
                            historyItem.number, // Ball number
                            historyItem.oddEven, // Only Odd/Even
                            historyItem.bigSmall // Only Big/Small
                        )
                    }
                } ?: listOf(listOf("No Data", "No Data", "No Data", "No Data"))
            }
            is Resource.Error -> listOf(
                listOf("Error", "Error", "Error", "Error")
            )
            else -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...")
            )
        }
    }

    val k3MyHistoryData = remember(myHistoryResource) {
        when (val resource = myHistoryResource) {
            is Resource.Loading -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...", "Loading...")
            )
            is Resource.Success -> {
                resource.data?.map { historyItem ->
                    listOf(
                        historyItem.period,
                        historyItem.bidNum,
                        historyItem.price,
                        historyItem.status, // Use 'status' for win/loss/pending
                        historyItem.totalamount // Use 'totalamount' for winning amount
                    )
                } ?: listOf(listOf("No Data", "No Data", "No Data", "No Data", "No Data"))
            }
            is Resource.Error -> listOf(
                listOf("Error", "Error", "Error", "Error", "Error")
            )
            else -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...", "Loading...")
            )
        }
    }


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
                                text = "₹ ${String.format("%,.2f", totalBalance)}",
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

                // K3 Lotre 30s Card
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
                                    // Display fetched period ID
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
                                    // Display fetched and calculated time remaining
                                    Text(
                                        text = String.format("00:%02d", timeRemaining / 1000), // Convert millis to seconds
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
                                        repeat(3) { index ->
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
                                                    painter = painterResource(id = R.drawable.dice),
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
                                                painter = painterResource(id = getK3BallDrawable(ball.number)),
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
                                            text = (timeRemaining / 1000).toString(), // Convert millis to seconds
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
                            onClick = { viewModel.setActiveHistoryTab("Game History") }, // Use setActiveHistoryTab
                            modifier = Modifier.weight(1f)
                        )
                        HistoryTabButton(
                            text = "My History",
                            isSelected = selectedHistoryTab == "My History",
                            onClick = { viewModel.setActiveHistoryTab("My History") }, // Use setActiveHistoryTab
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Results Table
                item {
                    when (selectedHistoryTab) {
                        "Game History" -> {
                            CompactExcelTableforK3(data = k3GameHistoryData)
                        }

                        "My History" -> {
                            MyHistoryTableforK3(data = k3MyHistoryData)
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
                            text = "Page 1 / 27",
                            fontSize = 16.sp,
                            fontWeight = Bold,
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
                        // Refresh My History after a successful bet
                        viewModel.fetchK3MyHistory()
                    } else {
                        android.util.Log.e("K3Ui60", "Bet placement failed: $errorMessage")
                    }
                }
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
