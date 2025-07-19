package com.weblite.kgf.ui.screens.game

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
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.weblite.components.Wingo60BettingPopupDialog // Updated import
import com.example.weblite.components.BigSmallButton
import com.example.weblite.components.ColorButton
import com.example.weblite.components.CompactExcelTableforWingo
import com.example.weblite.components.HistoryTabButton
import com.example.weblite.components.ImageNumberButton
import com.example.weblite.components.MultiplierButton
import com.example.weblite.components.MyHistoryTableforWingo
import com.example.weblite.components.SuccessMessage
import com.weblite.kgf.ui.screens.KGFLogoText
import com.weblite.kgf.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weblite.kgf.Api2.Resource // Import the Resource sealed class
import com.weblite.kgf.viewmodel.Wingo60GameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wingo60Screen(
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    viewModel: Wingo60GameViewModel = hiltViewModel()
) {
    var selectedColor by remember { mutableStateOf("") }
    var selectedNumbers by remember { mutableStateOf(setOf<Int>()) }
    var selectedColors by remember { mutableStateOf(setOf<String>()) }
    var selectedMultiplier by remember { mutableStateOf("Random") }
    var selectedBigSmall by remember { mutableStateOf("Big") }
    var showBettingPopup by remember { mutableStateOf(false) }
    var selectedNumberForBetting by remember { mutableStateOf(0) }
    var totalBalance by remember { mutableStateOf(17510970.65) } // This should ideally come from a ViewModel or user session
    var showSuccessMessage by remember { mutableStateOf(false) }
    var selectedColorForBetting by remember { mutableStateOf("Green") }
    var colorSelected by remember { mutableStateOf(false) }

    // Observe period ID and timer from ViewModel
    val sixtySecPeriodId by viewModel.sixtySecPeriodId.collectAsStateWithLifecycle()
    val timeRemaining by viewModel.timeRemaining.collectAsStateWithLifecycle()

    // Observe history data from ViewModel
    val gameHistoryResource by viewModel.gameHistoryResponse.collectAsStateWithLifecycle()
    val myHistoryResource by viewModel.myHistoryResponse.collectAsStateWithLifecycle()
    val selectedHistoryTab by viewModel.selectedHistoryTab.collectAsStateWithLifecycle() // Observe selected tab

    // Convert Game History API response to table data
    val gameHistoryData = remember(gameHistoryResource) {
        when (val resource = gameHistoryResource) {
            is Resource.Loading -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...")
            )
            is Resource.Success -> {
                resource.data?.result?.history?.map { historyItem ->
                    listOf(
                        historyItem.period_60,
                        historyItem.number_result.ifEmpty { "Loading..." }, // Display "---" if empty
                        historyItem.color_result.ifEmpty { "Loading..." },   // Display "---" if empty
                        historyItem.big_small_result.ifEmpty { "Loading..." } // Display "---" if empty
                    )
                } ?: listOf(listOf("No Data", "No Data", "No Data", "No Data"))
            }
            is Resource.Error -> listOf(
                listOf("Error", "Error", "Error", "Error")
            )
            null -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...")
            )
            else -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...", "Loading...")
            )
        }
    }

    // Convert My History API response to table data
    val myHistoryData = remember(myHistoryResource) {
        when (val resource = myHistoryResource) {
            is Resource.Loading -> listOf(
                listOf("Loading...", "Loading...", "Loading...", "Loading...", "Loading...")
            )
            is Resource.Success -> {
                resource.data?.result?.history?.map { historyItem ->
                    listOf(
                        historyItem.period,
                        historyItem.bidNum,
                        historyItem.price,
                        historyItem.resultStatus,
                        historyItem.winning_amount ?: "0" // Ensure winning_amount is not null
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
                listOf("Loading...", "Loading...", "Loading...", "Loading...", "Loading...")
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

    val showCountdownOverlay = timeRemaining <= 10 && timeRemaining > 0
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(showCountdownOverlay) {
        if (showCountdownOverlay && showBettingPopup) {
            android.util.Log.d("Wingo30Screen", "Auto-closing betting dialog due to countdown overlay")
            showBettingPopup = false
            colorSelected = false
        }
    }

    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
        // ViewModel's init block already handles initial fetch and timer start.
        // Also, the ViewModel's init block starts game history polling by default.
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

            // Win Go 60s Card
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
                                text = "Win Go 60s", // Updated text
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
                                text = "Win Go 60s",
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
                                val minutes = timeRemaining / 60
                                val seconds = timeRemaining % 60
                                val formattedTime = String.format("%02d:%02d", minutes, seconds)
                                formattedTime.forEach { char ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = char.toString(),
                                            color = if (char == ':') Color.Black else Color(
                                                0xFFFF6B35
                                            ),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            // Display fetched period ID below the counter
                            Text(
                                text = sixtySecPeriodId ?: "Loading...",
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
                                        text = timeRemaining.toString(),
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
                            viewModel.onGameHistoryTabSelected() // Let ViewModel manage state and polling
                        },
                        modifier = Modifier.weight(1f)
                    )
                    HistoryTabButton(
                        text = "My History",
                        isSelected = selectedHistoryTab == "My History",
                        onClick = {
                            viewModel.onMyHistoryTabSelected() // Let ViewModel manage state and one-time fetch
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
                    // ADDED: This 'else' branch makes the 'when' expression exhaustive for String type
                    else -> {
                        // Fallback for any unexpected or unhandled selectedHistoryTab values
                        // You might want to log this case or show a specific error UI
                        CompactExcelTableforWingo(data = gameHistoryData) // Use gameHistoryData as a default fallback
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
            Wingo60BettingPopupDialog( // Changed to Wingo60BettingPopupDialog
                selectedNumber = if (colorSelected) null else selectedNumberForBetting,
                selectedColor = if (colorSelected) selectedColorForBetting else null,
                onDismiss = {
                    showBettingPopup = false
                    colorSelected = false // Reset color selection flag
                },
                // MODIFIED: onConfirmBet now receives the result from the dialog's internal bet placement
                onConfirmBet = { isSuccess, errorMessage ->
                    if (isSuccess) {
                        // Update balance on success (consider moving this to ViewModel if balance is fetched from API)
                        // Note: The amount and multiplier are not passed back here, so balance update needs to be handled differently
                        // or fetched from API after bet.
                        showSuccessMessage = true
                    } else {
                        // Handle error, e.g., show a toast or update an error state
                        android.util.Log.e("Wingo60Screen", "Bet placement failed: $errorMessage")
                        // You might want to show a SnackBar or Toast here for the user
                    }
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
