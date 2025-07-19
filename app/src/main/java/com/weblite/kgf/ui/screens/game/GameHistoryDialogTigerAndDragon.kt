package com.weblite.kgf.ui.screens.game

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

// Updated data class to include fields for "My History"
data class GameHistoryItem(
    val period: String,
    val result: String, // For Game History tab (Tiger, Dragon, Draw, Running)
    val betOn: Int? = null, // For My History tab (e.g., 1000, 500)
    val coinType: String? = null, // For My History tab (e.g., Tiger, Dragon, Draw)
    val winLossStatus: String? = null, // For My History tab (Win, Loss)
    val winningAmount: Double? = null // For My History tab (e.g., 2000.00, 0.00)
)

@Composable
fun GameHistoryDialogTigerAndDragon(onDismissRequest: () -> Unit) {
    var activeTab by remember { mutableStateOf("game-history") }
    var currentPageGameHistory by remember { mutableStateOf(1) }
    var currentPageMyHistory by remember { mutableStateOf(1) }

    // Mock data for Game History tab
    val gameHistoryData = remember {
        listOf(
            GameHistoryItem("2507191217", "Running"),
            GameHistoryItem("2507191216", "Dragon"),
            GameHistoryItem("2507191215", "Tiger"),
            GameHistoryItem("2507191214", "Tiger"),
            GameHistoryItem("2507191213", "Dragon"),
            GameHistoryItem("2507191212", "Dragon"),
            GameHistoryItem("2507191211", "Dragon"),
            GameHistoryItem("2507191210", "Draw"),
            GameHistoryItem("2507191209", "Dragon"),
            GameHistoryItem("2507191208", "Draw"),
            GameHistoryItem("2507191207", "Tiger"),
            GameHistoryItem("2507191206", "Dragon"),
            GameHistoryItem("2507191205", "Tiger"),
            GameHistoryItem("2507191204", "Draw"),
            GameHistoryItem("2507191203", "Dragon"),
            GameHistoryItem("2507191202", "Tiger"),
        )
    }

    // Mock data for My History tab
    val myHistoryData = remember {
        listOf(
            GameHistoryItem("2507191216", "Tiger", 1000, "Tiger", "Loss", 0.00),
            GameHistoryItem("2507191216", "Tiger", 1000, "Tiger", "Loss", 0.00),
            GameHistoryItem("2507191216", "Tiger", 1000, "Tiger", "Loss", 0.00),
            GameHistoryItem("2507191216", "Tiger", 1000, "Tiger", "Loss", 0.00),
            GameHistoryItem("2507191215", "Dragon", 500, "Dragon", "Loss", 0.00),
            GameHistoryItem("2507081797", "Draw", 10, "Draw", "Loss", 0.00),
            GameHistoryItem("2507081797", "Draw", 10, "Draw", "Loss", 0.00),
            GameHistoryItem("2507081797", "Draw", 10, "Draw", "Loss", 0.00),
            GameHistoryItem("2507191216", "Dragon", 1000, "Dragon", "Win", 2000.00),
            GameHistoryItem("2507191216", "Tiger", 1000, "Tiger", "Loss", 0.00),
            GameHistoryItem("2507191216", "Tiger", 1000, "Tiger", "Loss", 0.00),
            GameHistoryItem("2507191216", "Tiger", 1000, "Tiger", "Loss", 0.00),
            GameHistoryItem("2507191215", "Dragon", 500, "Dragon", "Loss", 0.00),
            GameHistoryItem("2507081797", "Draw", 10, "Draw", "Loss", 0.00),
            GameHistoryItem("2507081797", "Draw", 10, "Draw", "Loss", 0.00),
            GameHistoryItem("2507081797", "Draw", 10, "Draw", "Loss", 0.00),
        )
    }

    val itemsPerPage = 8

    val currentData = if (activeTab == "game-history") gameHistoryData else myHistoryData
    val currentPage = if (activeTab == "game-history") currentPageGameHistory else currentPageMyHistory
    val setCurrentPage: (Int) -> Unit = { newPage ->
        if (activeTab == "game-history") currentPageGameHistory = newPage else currentPageMyHistory = newPage
    }

    val totalPages = (currentData.size + itemsPerPage - 1) / itemsPerPage
    val startIndex = (currentPage - 1) * itemsPerPage
    val endIndex = (startIndex + itemsPerPage).coerceAtMost(currentData.size)
    val visibleHistory = currentData.subList(startIndex, endIndex)

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
                            onClick = { activeTab = "game-history" },
                            modifier = Modifier.width(buttonWidth) // Apply calculated width
                        )
                        TabButton(
                            text = "My History",
                            isSelected = activeTab == "my-history",
                            onClick = { activeTab = "my-history" },
                            modifier = Modifier.width(buttonWidth) // Apply calculated width
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
                                        text = item.period,
                                        modifier = Modifier.width(columnWidth), // Apply calculated width
                                        color = Color(0xFF333333),
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = item.result,
                                        modifier = Modifier.width(columnWidth), // Apply calculated width
                                        color = when (item.result) {
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
            } else { // My History tab content
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Use BoxWithConstraints to calculate column widths for My History table
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val totalWidth = maxWidth
                        val columnPadding = 16.dp // Horizontal padding on the Row
                        // Explicitly convert Dp to value, perform arithmetic, convert back to Dp
                        val availableWidth = (totalWidth.value - (columnPadding.value * 2)).dp
                        val totalWeightSum = 1f + 0.8f + 0.8f + 1f + 1.2f // Sum of original weights: 4.8f

                        // Explicitly convert Dp to value, perform arithmetic, convert back to Dp
                        val periodWidth = ((1f / totalWeightSum) * availableWidth.value).dp
                        val betOnCoinWidth = ((0.8f / totalWeightSum) * availableWidth.value).dp
                        val winLossWidth = ((1f / totalWeightSum) * availableWidth.value).dp
                        val winningAmountWidth = ((1.2f / totalWeightSum) * availableWidth.value).dp

                        // Table Header for My History
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF43A047)) // Green header as per image
                                .padding(vertical = 8.dp, horizontal = columnPadding)
                        ) {
                            Text(
                                text = "Period",
                                modifier = Modifier.width(periodWidth),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Bes On",
                                modifier = Modifier.width(betOnCoinWidth),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Coin",
                                modifier = Modifier.width(betOnCoinWidth),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Win/Loss",
                                modifier = Modifier.width(winLossWidth),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Winning Amount",
                                modifier = Modifier.width(winningAmountWidth),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Table Body for My History
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
                                        text = item.period,
                                        modifier = Modifier.width(periodWidth),
                                        color = Color(0xFF333333),
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = item.betOn?.toString() ?: "",
                                        modifier = Modifier.width(betOnCoinWidth),
                                        color = Color(0xFF333333),
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = item.coinType ?: "",
                                        modifier = Modifier.width(betOnCoinWidth),
                                        color = Color(0xFF333333),
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = item.winLossStatus ?: "",
                                        modifier = Modifier.width(winLossWidth),
                                        color = when (item.winLossStatus) {
                                            "Win" -> Color(0xFF43A047) // Green
                                            "Loss" -> Color(0xFFED6A5A) // Red/Orange
                                            else -> Color(0xFF666666)
                                        },
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = String.format("%.2f", item.winningAmount ?: 0.00),
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

            // Pagination (common for both tabs)
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
