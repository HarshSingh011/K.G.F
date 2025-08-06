package com.weblite.kgf.ui.screens.internalScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawCacheModifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.domain.model.MyCommissionsResponse
import com.weblite.kgf.domain.model.MyCommission
import com.weblite.kgf.util.DateFormatUtil
import com.weblite.kgf.ui.viewmodel.PromotionViewModel


@Composable
fun CommissionDetails(
    navController: NavController,
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    viewModel: PromotionViewModel = hiltViewModel()
){
    val commissionState = viewModel.commissionsState.value
    val scrollState = rememberScrollState()
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF001B40), Color(0xFF002D60))
    )
    LaunchedEffect(Unit) {
        onShowTopBar(true)
        onShowBottomBar(false)
        val userId = com.weblite.kgf.Api2.SharedPrefManager.getString("user_id", "0") ?: "0"
        viewModel.fetchMyCommissions(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth().height(50.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = Color(0xff00EBEF),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(32.dp))
                .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF007BFF))
            }
            Text("Commission Details",
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Total Commission Card
        if (commissionState is Resource.Success) {
            val totalCommission = commissionState.data?.result?.totalCommission?.toDoubleOrNull() ?: 0.0
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Commission: %.4f".format(totalCommission),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF003366)
                )
            }
        }
        if (commissionState is Resource.Loading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
        if (commissionState is Resource.Error) {
            Text("Error: ${commissionState.message}", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(22.dp))

        Column(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth()
                .height(550.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0xff00EBEF),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, Color(0xFF29659A), RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            val textclr = Color.Black
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(2.dp))
                Text("Sr", modifier = Modifier.weight(0.48f), color = textclr, fontWeight = FontWeight.Bold)
                Text("Amount", modifier = Modifier.weight(1f), color = textclr, fontWeight = FontWeight.Bold)
                Text("Date", modifier = Modifier.weight(1f), color = textclr, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Act", modifier = Modifier.weight(0.68f), color = textclr, fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier.verticalScroll(scrollState)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                if (commissionState is Resource.Success) {
                    val commissions = commissionState.data?.result?.myCommissions ?: emptyList()
                    commissions.forEachIndexed { idx, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${idx + 1}", color = textclr, fontSize = 14.sp, modifier = Modifier.weight(0.48f))
                            Text(item.dayCommissionAmount, color = textclr, fontSize = 14.sp, modifier = Modifier.weight(0.89f))
                            Text(DateFormatUtil.formatApiDateToDisplay(item.date), color = textclr, fontSize = 14.sp, modifier = Modifier.weight(0.98f))
                            Spacer(modifier = Modifier.width(3.dp))
                            CommissionDetailButton(
                                modifier = Modifier.weight(0.8f),
                                onClick = {
                                    navController.navigate("commission_details/${item.date}")
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CommissionDetailButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFCEDC00))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Details", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

