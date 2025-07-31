package com.weblite.kgf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.ui.screens.game.viewmodel.TigerAndDragonViewModel
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.weblite.kgf.data.models.games.GameHistoryItem

@Composable
fun GameHistoryDialogTigerAndDragon(
    onDismissRequest: () -> Unit,
    activeTab: String,
    onTabChange: (String) -> Unit,
    viewModel: TigerAndDragonViewModel = hiltViewModel()
) {
    var currentPageGameHistory by remember { mutableStateOf(1) }
    var currentPageMyHistory by remember { mutableStateOf(1) }

    // Collect game history from ViewModel
    val gameHistoryState by viewModel.gameHistory.collectAsState()
    val gameHistoryData = remember(gameHistoryState) {
        gameHistoryState?.result?.history?.map {
            GameHistoryItem(
                period = it.period,
                result = it.result
            )
        } ?: emptyList()
    }

    // Collect my history from ViewModel and map to table
    val myHistoryState by viewModel.myHistory.collectAsState()

    // Debug: Log raw myHistoryState when MyHistory tab is active
    LaunchedEffect(activeTab, myHistoryState) {
        if (activeTab == "my-history") {
            Log.d("GameHistoryDialog", "Raw myHistoryState: ${myHistoryState}")
            Log.d("GameHistoryDialog", "History result: ${myHistoryState?.result}")
            Log.d("GameHistoryDialog", "History list size: ${myHistoryState?.result?.history?.size ?: 0}")
        }
    }

    // Fixed: Access the nested history properly and map the actual API fields
    val myHistoryData = remember(myHistoryState) {
        val safeHistory = myHistoryState?.result?.history ?: emptyList()
        Log.d("GameHistoryDialog", "Processing ${safeHistory.size} history items")

        safeHistory.mapIndexed { index, item ->
            Log.d("GameHistoryDialog", "Item $index: periodId=${item.periodId}, betChoice=${item.betChoice}, coins=${item.coins}, result=${item.result}, betChoiceResult=${item.betChoiceResult}")

            // Map bet choice string to integer for display
            val betOn = when (item.betChoice?.lowercase()) {
                "tiger" -> 1
                "dragon" -> 2
                "tie", "draw" -> 3
                else -> null
            }

            // Map result to win/loss status - use the 'result' field from API
            val winLossStatus = when (item.result?.lowercase()) {
                "win" -> "Win"
                "loss" -> "Loss"
                "running" -> "Running"
                else -> item.result ?: "Unknown"
            }

            // Parse winning amount from totalamount field
            val winningAmount = try {
                item.totalAmount?.toDoubleOrNull() ?: 0.0
            } catch (e: Exception) {
                0.0
            }

            GameHistoryItem(
                period = item.periodId ?: "",
                betOn = betOn,
                coinType = item.coins ?: "",
                winLossStatus = winLossStatus,
                winningAmount = winningAmount
            )
        }
    }

    val itemsPerPage = 8

    // Use separate paging and visibleHistory for each tab to avoid mapping issues
    val gameHistoryTotalPages = if (gameHistoryData.isEmpty()) 1 else (gameHistoryData.size + itemsPerPage - 1) / itemsPerPage
    val myHistoryTotalPages = if (myHistoryData.isEmpty()) 1 else (myHistoryData.size + itemsPerPage - 1) / itemsPerPage
    val gameHistoryStartIndex = (currentPageGameHistory - 1) * itemsPerPage
    val gameHistoryEndIndex = (gameHistoryStartIndex + itemsPerPage).coerceAtMost(gameHistoryData.size)
    val myHistoryStartIndex = (currentPageMyHistory - 1) * itemsPerPage
    val myHistoryEndIndex = (myHistoryStartIndex + itemsPerPage).coerceAtMost(myHistoryData.size)
    val gameHistoryVisible = if (gameHistoryData.isNotEmpty()) gameHistoryData.subList(gameHistoryStartIndex, gameHistoryEndIndex) else emptyList()
    val myHistoryVisible = if (myHistoryData.isNotEmpty()) myHistoryData.subList(myHistoryStartIndex, myHistoryEndIndex) else emptyList()

    val visibleHistory = if (activeTab == "game-history") gameHistoryVisible else myHistoryVisible
    val currentPage = if (activeTab == "game-history") currentPageGameHistory else currentPageMyHistory
    val totalPages = if (activeTab == "game-history") gameHistoryTotalPages else myHistoryTotalPages
    val setCurrentPage: (Int) -> Unit = { newPage ->
        if (activeTab == "game-history") currentPageGameHistory = newPage else currentPageMyHistory = newPage
    }

    // Tab-specific logic - handles initial calls and polling
    LaunchedEffect(activeTab) {
        Log.d("GameHistoryDialog", "Tab changed to: $activeTab")
        if (activeTab == "game-history") {
            viewModel.startGameHistoryPolling()
            viewModel.fetchGameHistory() // Initial call when switching to game-history tab
        } else {
            viewModel.stopGameHistoryPolling()
            viewModel.fetchMyHistory() // Initial call when switching to my-history tab
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Make the entire dialog content scrollable
        ) {
            // Header with close button and tabs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(32.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF666666)
                    )
                }

                // Use BoxWithConstraints to calculate tab button widths
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Adjusted padding to prevent overlap with close button
                        .padding(start = 80.dp, end = 16.dp) // Increased start padding
                ) {
                    val totalWidth = maxWidth
                    val buttonCount = 2
                    val buttonSpacing = 8.dp // Small gap between buttons
                    // Explicitly convert Dp to value, perform arithmetic, convert back to Dp
                    val availableWidthForButtons = (totalWidth.value - (buttonSpacing.value * (buttonCount - 1))).dp
                    val buttonWidth = (availableWidthForButtons.value / buttonCount).dp

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround, // This will distribute the buttons
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TabButton(
                            text = "Game History",
                            isSelected = activeTab == "game-history",
                            onClick = { onTabChange("game-history") },
                            modifier = Modifier.width(buttonWidth)
                        )
                        TabButton(
                            text = "My History",
                            isSelected = activeTab == "my-history",
                            onClick = { onTabChange("my-history") },
                            modifier = Modifier.width(buttonWidth)
                        )
                    }
                }
            }

            // Content based on active tab
            if (activeTab == "game-history") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Use BoxWithConstraints to calculate column widths for Game History table
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val totalWidth = maxWidth
                        val columnPadding = 16.dp // Horizontal padding on the Row
                        // Explicitly convert Dp to value, perform arithmetic, convert back to Dp
                        val availableWidth = (totalWidth.value - (columnPadding.value * 2)).dp
                        val columnWidth = (availableWidth.value / 2f).dp // Two equal columns

                        Column {
                            // Table Header for Game History
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF0F0F0)) // Light gray header
                                    .padding(vertical = 8.dp, horizontal = columnPadding)
                            ) {
                                Text(
                                    text = "Period",
                                    modifier = Modifier.width(columnWidth), // Apply calculated width
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF666666),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Result",
                                    modifier = Modifier.width(columnWidth), // Apply calculated width
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF666666),
                                    fontSize = 16.sp
                                )
                            }

                            // Table Body for Game History
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp)
                            ) {
                                itemsIndexed(visibleHistory) { index, item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (index % 2 == 0) Color.White else Color(0xFFF9F9F9))
                                            .padding(vertical = 8.dp, horizontal = columnPadding)
                                    ) {
                                        Text(
                                            text = item.period ?: "",
                                            modifier = Modifier.width(columnWidth), // Apply calculated width
                                            color = Color(0xFF333333),
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = item.result ?: "",
                                            modifier = Modifier.width(columnWidth), // Apply calculated width
                                            color = when (item.result ?: "") {
                                                "Dragon" -> Color(0xFF43A047) // Green
                                                "Tiger" -> Color(0xFFED6A5A) // Red/Orange
                                                "Draw" -> Color(0xFF4FC3F7) // Light Blue
                                                else -> Color(0xFF666666)
                                            },
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else { // My History tab content
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Debug info: show item count
                    Text(
                        text = "MyHistory items: ${myHistoryData.size}",
                        color = if (myHistoryData.isEmpty()) Color.Red else Color.Green,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    if (myHistoryData.isEmpty()) {
                        Text(
                            text = "No betting history found.",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val totalWidth = maxWidth
                            val columnPadding = 16.dp
                            val availableWidth = (totalWidth.value - (columnPadding.value * 2)).dp
                            val totalWeightSum = 1f + 0.8f + 0.8f + 1f + 1.2f

                            val periodWidth = ((1f / totalWeightSum) * availableWidth.value).dp
                            val betOnCoinWidth = ((0.8f / totalWeightSum) * availableWidth.value).dp
                            val winLossWidth = ((1f / totalWeightSum) * availableWidth.value).dp
                            val winningAmountWidth = ((1.2f / totalWeightSum) * availableWidth.value).dp

                            Column {
                                // Table Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF43A047))
                                        .padding(vertical = 8.dp, horizontal = columnPadding)
                                ) {
                                    Text(
                                        text = "Period ID",
                                        modifier = Modifier.width(periodWidth),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Bet Choice",
                                        modifier = Modifier.width(betOnCoinWidth),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Coin Type",
                                        modifier = Modifier.width(betOnCoinWidth),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Result",
                                        modifier = Modifier.width(winLossWidth),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Total Amount",
                                        modifier = Modifier.width(winningAmountWidth),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Table Body
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 300.dp)
                                ) {
                                    itemsIndexed(visibleHistory) { index, item ->
                                        // Map betOn to readable string if possible
                                        val betChoice = when (item.betOn) {
                                            1 -> "Tiger"
                                            2 -> "Dragon"
                                            3 -> "Draw"
                                            else -> item.betOn?.toString() ?: "-"
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (index % 2 == 0) Color.White else Color(0xFFF9F9F9))
                                                .padding(vertical = 8.dp, horizontal = columnPadding)
                                        ) {
                                            Text(
                                                text = item.period.ifEmpty { "-" },
                                                modifier = Modifier.width(periodWidth),
                                                color = Color(0xFF333333),
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = betChoice,
                                                modifier = Modifier.width(betOnCoinWidth),
                                                color = Color(0xFF333333),
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = item.coinType?.ifEmpty { "-" } ?: "-",
                                                modifier = Modifier.width(betOnCoinWidth),
                                                color = Color(0xFF333333),
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = item.winLossStatus?.ifEmpty { "-" } ?: "-",
                                                modifier = Modifier.width(winLossWidth),
                                                color = when (item.winLossStatus ?: "") {
                                                    "Win" -> Color(0xFF43A047)
                                                    "Loss" -> Color(0xFFED6A5A)
                                                    "Running" -> Color(0xFF2196F3)
                                                    else -> Color(0xFF666666)
                                                },
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = if ((item.winningAmount ?: 0.0) > 0.0) String.format("₹%.2f", item.winningAmount) else "-",
                                                modifier = Modifier.width(winningAmountWidth),
                                                color = Color(0xFF333333),
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Pagination (common for both tabs)
            if (totalPages > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PaginationButton(
                        text = "Previous",
                        onClick = { setCurrentPage(currentPage - 1) },
                        enabled = currentPage > 1,
                        isPrimary = false
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Page $currentPage / $totalPages",
                        color = Color(0xFF666666),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    PaginationButton(
                        text = "Next",
                        onClick = { setCurrentPage(currentPage + 1) },
                        enabled = currentPage < totalPages,
                        isPrimary = true
                    )
                }
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (isSelected) Color(0xFFFF8000) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color(0xFFFF8000)
    val borderColor = if (isSelected) Color.Transparent else Color(0xFFFF8000)

    Box(
        modifier = modifier // Use the passed modifier for width
            .height(48.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PaginationButton(text: String, onClick: () -> Unit, enabled: Boolean, isPrimary: Boolean) {
    val backgroundColor = if (isPrimary) Color(0xFFFF8000) else Color(0xFFE0E0E0)
    val textColor = if (isPrimary) Color.White else Color(0xFF333333)

    Box(
        modifier = Modifier
            .height(40.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .alpha(if (enabled) 1f else 0.5f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
