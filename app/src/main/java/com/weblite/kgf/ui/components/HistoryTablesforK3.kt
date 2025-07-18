package com.example.weblite.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Excel-style Game History Table
@Composable
fun CompactExcelTableforK3(data: List<List<String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Header Row - Increased height
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50))
                .height(60.dp) // Increased from 45.dp to 60.dp
        ) {
            CompactHeaderCell("Period", Modifier.weight(1.2f))
            CompactHeaderCell("Number", Modifier.weight(1f))
            CompactHeaderCell("Odd/Even", Modifier.weight(1f))
            CompactHeaderCell("Big/\nSmall", Modifier.weight(1f))
        }

        // Data Rows
        data.forEachIndexed { index, (period, number, oddEven, bigSmall) ->
            val backgroundColor = if (index % 2 == 0) Color.White else Color(0xFFF8F8F8)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .height(50.dp)
            ) {
                CompactPeriodCell(period = period, modifier = Modifier.weight(1.2f))

                val numberColor = when {
                    number == "Loading..." -> Color(0xFFD2691E)
                    number.toIntOrNull()?.let { it % 2 != 0 } == true -> Color(0xFFDC143C)
                    else -> Color(0xFF228B22)
                }
                CompactDataCell(text = number, modifier = Modifier.weight(1f), textColor = numberColor)

                val oddEvenColor = when(oddEven) {
                    "Even" -> Color(0xFF1E88E5)
                    "Odd" -> Color(0xFFD5C661)
                    else -> Color(0xFFD2691E)
                }

                CompactDataCell(text = oddEven, modifier = Modifier.weight(1f), textColor = oddEvenColor)

                val bigSmallColor = when (bigSmall) {
                    "Big" -> Color(0xFFE53935)
                    "Small" -> Color(0xFF43A047)
                    else -> Color(0xFFD2691E)
                }
                CompactDataCell(text = bigSmall, modifier = Modifier.weight(1f), textColor = bigSmallColor)
            }
        }
    }
}

@Composable
fun CompactHeaderCell(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .border(0.5.dp, Color.Gray)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun CompactDataCell(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .border(0.5.dp, Color.Gray)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CompactPeriodCell(
    period: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .border(0.5.dp, Color.Gray)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (period.length >= 8) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = period.substring(0, 7),
                    color = Color(0xFFD2691E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = period.substring(7),
                    color = Color(0xFFD2691E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = period,
                color = Color(0xFFD2691E),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}


// My History Table
@Composable
fun MyHistoryTableforK3(data: List<List<String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Header Row - Increased height
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50))
                .height(60.dp) // Increased from 45.dp to 60.dp
        ) {
            CompactHeaderCell("Period", Modifier.weight(1.2f))
            CompactHeaderCell("Bes On", Modifier.weight(1f))
            CompactHeaderCell("Bet\nAmount", Modifier.weight(1f))
            CompactHeaderCell("Win/Loss", Modifier.weight(1f))
            CompactHeaderCell("Winning\nAmount", Modifier.weight(1f))
        }

        // Data Rows
        data.forEachIndexed { index, row ->
            if (row.size >= 5) {
                val (period, besOn, betAmount, winLoss, winningAmount) = row
                val backgroundColor = if (index % 2 == 0) Color.White else Color(0xFFF8F8F8)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .height(50.dp)
                ) {
                    // Period Cell - Orange text, split into two lines
                    CompactPeriodCell(period = period, modifier = Modifier.weight(1.2f))

                    // Bes On Cell - Number or Color
                    val besOnColor = when {
                        besOn.equals("Big", ignoreCase = true) -> Color(0xFFDC143C)
                        besOn.equals("Small", ignoreCase = true) -> Color(0xFF228B22)
                        besOn.equals("Odd", ignoreCase = true) -> Color(0xFFE1B143)
                        besOn.equals("Even", ignoreCase = true) -> Color(0xFF1E88E5)
                        besOn.toIntOrNull()?.let { it % 2 != 0 } == true -> Color(0xFFDC143C)
                        else -> Color(0xFF228B22)
                    }
                    CompactDataCell(text = besOn, modifier = Modifier.weight(1f), textColor = besOnColor)

                    // Bet Amount Cell - Green text
                    CompactDataCell(text = betAmount, modifier = Modifier.weight(1f), textColor = Color(
                        0xFFE53935
                    )
                    )

                    // Win/Loss Cell - Red for Loss, Green for Win
                    // Determine color based on win or loss
                    val isWin = winLoss.equals("Win", ignoreCase = true)
                    val winLossColor = if (isWin) Color(0xFF228B22) else Color(0xFFDC143C) // Green for Win, Red for Loss

                    val winningAmountColor = winLossColor

                    CompactDataCell(text = winLoss, modifier = Modifier.weight(1f), textColor = winLossColor)
                    CompactDataCell(text = winningAmount, modifier = Modifier.weight(1f), textColor = winningAmountColor)

                }
            }
        }
    }
}
