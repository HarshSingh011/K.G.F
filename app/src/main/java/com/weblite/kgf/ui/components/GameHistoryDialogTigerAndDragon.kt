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
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.data.models.games.MyHistoryApiResponse
import com.weblite.kgf.data.models.games.TigerGameHistoryResponse

@Composable
fun GameHistoryDialogTigerAndDragon(
    onDismissRequest: () -> Unit,
    activeTab: String,
    onTabChange: (String) -> Unit,
    viewModel: TigerAndDragonViewModel = hiltViewModel()
) {
    var currentPageGameHistory by remember { mutableStateOf(1) }
    var currentPageMyHistory by remember { mutableStateOf(1) }

    val gameHistoryState by viewModel.gameHistory.collectAsState()
    val myHistoryState by viewModel.myHistory.collectAsState()

    val itemsPerPage = 8

    // Always fetch both histories on dialog open
    LaunchedEffect(Unit) {
        viewModel.startGameHistoryPolling()
        viewModel.fetchGameHistory()
        viewModel.fetchMyHistory()
    }

    // Fetch myHistory and win result every time periodId changes (i.e., timer completes and new periodId is fetched)
    val currentPeriodId = gameHistoryState?.result?.history?.firstOrNull()?.period
    // (No new dialog state here; keep previous image/result dialog logic only)

    LaunchedEffect(currentPeriodId) {
        if (currentPeriodId != null) {
            viewModel.fetchMyHistory()
            viewModel.fetchWinResult()
        }
    }
    // (No new WinDialog/image logic here; keep previous dialog/image logic only)

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
                        .padding(start = 80.dp, end = 16.dp)
                ) {
                    val totalWidth = maxWidth
                    val buttonCount = 2
                    val buttonSpacing = 8.dp
                    val availableWidthForButtons = (totalWidth.value - (buttonSpacing.value * (buttonCount - 1))).dp
                    val buttonWidth = (availableWidthForButtons.value / buttonCount).dp

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
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

            // Only show one tab's content at a time
            if (activeTab == "game-history") {
                val response = gameHistoryState
                val history = response?.result?.history ?: emptyList()
                val totalPages = if (history.isEmpty()) 1 else (history.size + itemsPerPage - 1) / itemsPerPage
                val startIndex = (currentPageGameHistory - 1) * itemsPerPage
                val endIndex = (startIndex + itemsPerPage).coerceAtMost(history.size)
                val visibleHistory = if (history.isNotEmpty()) history.subList(startIndex, endIndex) else emptyList()
                Box(modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        // Table header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF18804B), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Period", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                            Text("Win/Loss", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                        }
                        if (response == null) {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Loading...", color = Color.Gray, fontSize = 16.sp)
                            }
                        } else if (visibleHistory.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No game history found.", color = Color.Gray, fontSize = 16.sp)
                            }
                        } else {
                            visibleHistory.forEachIndexed { idx, item ->
                                val bgColor = if (idx % 2 == 0) Color.White else Color(0xFFF8F8F8)
                                val isWin = item.result.equals("win", ignoreCase = true)
                                val isLoss = item.result.equals("lose", ignoreCase = true)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(bgColor)
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.period ?: "-",
                                        color = Color(0xFFFFC700), // yellow
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        modifier = Modifier.weight(1.5f),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = if (isWin) "Win" else if (isLoss) "Loss" else (item.result ?: "-"),
                                        color = if (isWin) Color(0xFF18804B) else if (isLoss) Color(0xFFFF0000) else Color(0xFF333333),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        modifier = Modifier.weight(1.5f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PaginationButton(
                                text = "Prev",
                                onClick = { if (currentPageGameHistory > 1) currentPageGameHistory-- },
                                enabled = currentPageGameHistory > 1,
                                isPrimary = false
                            )
                            Text(
                                text = "Page $currentPageGameHistory of $totalPages",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                fontSize = 16.sp
                            )
                            PaginationButton(
                                text = "Next",
                                onClick = { if (currentPageGameHistory < totalPages) currentPageGameHistory++ },
                                enabled = currentPageGameHistory < totalPages,
                                isPrimary = false
                            )
                        }
                    }
                }
            } else if (activeTab == "my-history") {
                when (val state = myHistoryState) {
                    is Resource.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f, fill = false), contentAlignment = Alignment.Center) {
                            Text("Loading...", color = Color.Gray, fontSize = 16.sp)
                        }
                    }
                    is Resource.Error -> {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f, fill = false), contentAlignment = Alignment.Center) {
                            Text("Failed to load.", color = Color.Red, fontSize = 16.sp)
                        }
                    }
                    is Resource.Success -> {
                        val response = state.data 
                        val history = response?.result?.history ?: emptyList()
                        val totalPages = if (history.isEmpty()) 1 else (history.size + itemsPerPage - 1) / itemsPerPage
                        val startIndex = (currentPageMyHistory - 1) * itemsPerPage
                        val endIndex = (startIndex + itemsPerPage).coerceAtMost(history.size)
                        val visibleHistory = if (history.isNotEmpty()) history.subList(startIndex, endIndex) else emptyList()
                        Box(modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
                            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                // Table header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF18804B), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Period", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
                                    Text("Bes On", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    Text("Coin", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    Text("Win/Loss", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    Text("Winning Amount", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
                                }
                                if (visibleHistory.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text("No my history found.", color = Color.Gray, fontSize = 16.sp)
                                    }
                                } else {
                                    visibleHistory.forEachIndexed { idx, item ->
                                        val bgColor = if (idx % 2 == 0) Color.White else Color(0xFFF8F8F8)
                                        val isWin = item.result.equals("win", ignoreCase = true)
                                        val isLoss = item.result.equals("lose", ignoreCase = true)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(bgColor)
                                                .padding(vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.periodId ?: "-",
                                                color = Color(0xFFFFC700), // yellow
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                modifier = Modifier.weight(1.2f),
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = item.betChoice ?: "-",
                                                color = Color(0xFF0091FF), // blue
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = item.coins ?: "-",
                                                color = when (item.betChoice?.lowercase()) {
                                                    "dragon" -> Color(0xFFFF8000)
                                                    "tiger" -> Color(0xFF18804B)
                                                    "draw" -> Color(0xFF0091FF)
                                                    else -> Color(0xFF333333)
                                                },
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = if (isWin) "Win" else if (isLoss) "Loss" else (item.result ?: "-"),
                                                color = if (isWin) Color(0xFF18804B) else if (isLoss) Color(0xFFFF0000) else Color(0xFF333333),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = item.totalAmount ?: "0.00",
                                                color = if (isWin) Color(0xFF18804B) else Color(0xFFFF0000),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                modifier = Modifier.weight(1.2f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PaginationButton(
                                        text = "Prev",
                                        onClick = { if (currentPageMyHistory > 1) currentPageMyHistory-- },
                                        enabled = currentPageMyHistory > 1,
                                        isPrimary = false
                                    )
                                    Text(
                                        text = "Page $currentPageMyHistory of $totalPages",
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        fontSize = 16.sp
                                    )
                                    PaginationButton(
                                        text = "Next",
                                        onClick = { if (currentPageMyHistory < totalPages) currentPageMyHistory++ },
                                        enabled = currentPageMyHistory < totalPages,
                                        isPrimary = false
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f, fill = false), contentAlignment = Alignment.Center) {
                            Text("No my history found.", color = Color.Gray, fontSize = 16.sp)
                        }
                    }
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
