package com.weblite.kgf.ui.screens.dashboard

import androidx.compose.foundation.background
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
import com.weblite.kgf.ui.viewmodel.PromotionViewModel
import com.weblite.kgf.Api2.Resource

@Composable
fun DateDetailsScreen(
    userId: String,
    date: String,
    viewModel: PromotionViewModel = hiltViewModel()
) {
    val commissionDetailsState = viewModel.commissionDateDetailsState.value

    LaunchedEffect(userId, date) {
        viewModel.fetchPromotionCommissionDateDetails(userId, date)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF002051))
            .padding(16.dp)
    ) {
        Text(
            text = "Commission Details for $date",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
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
                    Row(Modifier.fillMaxWidth().background(Color(0xFF003366)).padding(vertical = 6.dp)) {
                        Text("Sr", color = Color.White, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                        Text("Amount", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Time", color = Color.White, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                        Text("Giver User ID", color = Color.White, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                        Text("Status", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                    details?.forEachIndexed { idx, item ->
                        Row(Modifier.fillMaxWidth().background(if (idx % 2 == 0) Color(0xFFeaf6ff) else Color.White).padding(vertical = 4.dp)) {
                            Text("${idx + 1}", modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                            Text(item.commission_amount, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(item.created_at.substringAfter(" "), modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                            Text(item.giver_user_id, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                            Text(if (item.is_added_to_wallet == "1") "Added" else "Pending", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            null -> Text("No data", color = Color.White)
            else -> {}
        }
    }
}
