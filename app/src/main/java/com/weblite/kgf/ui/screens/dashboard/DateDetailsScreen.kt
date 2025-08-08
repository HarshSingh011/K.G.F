package com.weblite.kgf.ui.screens.dashboard

// This file was renamed from CommissionDetailsScreen.kt to DateDetailsScreen.kt
// The composable is now DateDetailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.ui.screens.dashboard.viewmodel.PromotionViewModel
import com.weblite.kgf.Api.Resource
import com.weblite.kgf.util.DateFormatUtil

@Composable
fun DateDetailsScreen(
    date: String,
    viewModel: PromotionViewModel = hiltViewModel()
) {
    val commissionDetailsState = viewModel.commissionDateDetailsState.value
    val userId = com.weblite.kgf.Api.SharedPrefManager.getString("user_id", "0") ?: "0"

    val apiDate = DateFormatUtil.formatDisplayDateToApi(date)

    LaunchedEffect(userId, apiDate) {
        viewModel.fetchPromotionCommissionDateDetails(userId, apiDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF002051))
            .padding(8.dp)
    ) {
        // AppBar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "K • G • F",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFFFFD600),
                letterSpacing = 2.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Date Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFF028bed), shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = apiDate,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                color = Color(0xFF002051),
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Table Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                )
                .border(2.dp, Color(0xFF028bed), shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                .padding(8.dp)
        ) {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sr", fontWeight = FontWeight.Bold, color = Color(0xFF002051), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                    Text("Amount", fontWeight = FontWeight.Bold, color = Color(0xFF002051), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("GiverId", fontWeight = FontWeight.Bold, color = Color(0xFF002051), modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
                    Text("Created\nAt", fontWeight = FontWeight.Bold, color = Color(0xFF002051), modifier = Modifier.weight(1.8f), textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(2.dp))
                when (commissionDetailsState) {
                    is Resource.Loading -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Error -> {
                        Text("Error: ${(commissionDetailsState as Resource.Error).message}", color = Color.Red)
                    }
                    is Resource.Success -> {
                        val details = (commissionDetailsState).data?.result?.myCommissions
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            details?.forEachIndexed { idx, item ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(if (idx % 2 == 0) Color(0xFFeaf6ff) else Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${idx + 1}", modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center, color = Color.Black)
                                    Text(item.commission_amount, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.Black)
                                    Text(item.giver_user_id, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center, color = Color.Black)
                                    Column(modifier = Modifier.weight(1.8f), horizontalAlignment = Alignment.CenterHorizontally) {
                                        val createdAt = item.created_at
                                        val parts = createdAt.split(" ")
                                        if (parts.size == 2) {
                                            Text(parts[0], color = Color.Black, fontSize = 13.sp)
                                            Text(parts[1], color = Color.Black, fontSize = 13.sp)
                                        } else {
                                            Text(createdAt, color = Color.Black, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    null -> Text("No data", color = Color.White)
                    else -> {}
                }
            }
        }
    }
}
