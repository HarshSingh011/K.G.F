package com.weblite.kgf.ui.screens.internalScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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


@Composable
fun CommissionDetails(
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
){

    val commissions = listOf(
        Triple("0.0840", "24-May-2025", 1),
        Triple("232.0600", "23-May-2025", 2),
        Triple("55.1916", "22-May-2025", 3),
        Triple("3.9108", "17-May-2025", 4),
        Triple("1.1572", "14-May-2025", 5),
        Triple("13.8600", "13-May-2025", 6),
        Triple("0.0032", "06-Feb-2025", 7),
        Triple("0.0220", "05-Feb-2025", 8),
        Triple("0.4000", "28-Jan-2025", 9),
        Triple("0.4000", "28-Jan-2025", 9),
        Triple("0.4000", "28-Jan-2025", 9),
        Triple("0.4000", "28-Jan-2025", 9),
        Triple("0.4000", "28-Jan-2025", 9),
        Triple("0.4000", "28-Jan-2025", 9)
    )

    val totalCommission = commissions.sumOf { it.first.toDouble() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF001B40), Color(0xFF002D60))
    )
    LaunchedEffect(Unit) {
        onShowTopBar(true)
        onShowBottomBar(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(16.dp)

    ){

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
        ){
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
            ){

                Spacer(modifier = Modifier.height(4.dp))

                // Table Rows
                commissions.forEach { (amount, date, index) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$index", color = textclr, fontSize = 14.sp, modifier = Modifier.weight(0.48f))
                        Text(amount, color = textclr, fontSize = 14.sp, modifier = Modifier.weight(0.89f))
                        Text(date, color = textclr, fontSize = 14.sp, modifier = Modifier.weight(0.98f))
                        Spacer(modifier = Modifier.width(3.dp))
                        CommissionDetailButton(Modifier.weight(0.8f))
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
fun CommissionDetailButton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFCEDC00)) // Yellow-Green button
            .padding(vertical = 6.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Details", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

