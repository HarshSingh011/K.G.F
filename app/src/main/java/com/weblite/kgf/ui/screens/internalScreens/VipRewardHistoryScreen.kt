package com.weblite.kgf.ui.screens.internalScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.domain.model.RewardHistory
import com.weblite.kgf.presentation.vip.VipViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VipRewardHistoryScreen(onBackClick: () -> Unit = {}) {
    val viewModel: VipViewModel = hiltViewModel()
    val rewardHistory = viewModel.rewardHistory.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.fetchRewardHistory()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF001848), Color(0xFF003366))
                )
            )
            .padding(12.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rewards History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (rewardHistory.isEmpty()) {
                Text(
                    text = "No reward history found.",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                rewardHistory.forEach { item ->
                    RewardHistoryCard(item)
                }
            }
        }
    }
}

@Composable
fun RewardHistoryCard(item: RewardHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF4CAF50), RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Reward History",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextRow(label = "UserID:", value = item.userId)
            TextRow(label = "Level:", value = item.level.toString())
            TextRow(label = "Bonus Amount:", value = String.format("%.2f", item.bonusAmount))
            TextRow(label = "Type:", value = item.type)
            TextRow(label = "Date:", value = item.date)
        }
    }
}

@Composable
fun TextRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, color = Color.Black)
    }
}
