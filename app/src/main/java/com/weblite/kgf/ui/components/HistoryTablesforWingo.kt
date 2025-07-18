package com.example.weblite.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompactExcelTableforWingo(data: List<List<String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color.Gray) // Outer border
    ) {
        // Header Row - Increased height
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50))
                .height(55.dp) // Increased from 45.dp to 55.dp
        ) {
            CompactHeaderCellWithBorder("Period", Modifier.weight(1.2f))
            CompactHeaderCellWithBorder("Number", Modifier.weight(1f))
            CompactHeaderCellWithBorder("Color", Modifier.weight(1f))
            CompactHeaderCellWithBorder("Big/\nSmall", Modifier.weight(1f), isLast = true)
        }

        // Data Rows
        data.forEachIndexed { index, (period, number, color, bigSmall) ->
            val backgroundColor = if (index % 2 == 0) Color.White else Color(0xFFF8F8F8)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .height(50.dp)
            ) {
                CompactPeriodCellWithBorder(period = period, modifier = Modifier.weight(1.2f))

                // Number Cell with colored text (considering mixed colors)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .border(0.5.dp, Color.Gray), // Right border
                    contentAlignment = Alignment.Center
                ) {
                    if (number == "Loading...") {
                        Text(
                            text = number,
                            color = Color(0xFFFF6B35),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        ColoredNumberText(number = number, colorInfo = color)
                    }
                }

                // Color Cell with equal-sized dots
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .border(0.5.dp, Color.Gray), // Right border
                    contentAlignment = Alignment.Center
                ) {
                    if (color == "Loading...") {
                        Text(
                            text = color,
                            color = Color(0xFFFF6B35),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        ColorDots(colorText = color)
                    }
                }

                // Big/Small Cell - Fixed border issue
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .border(0.5.dp, Color.Gray), // Added border for last column
                    contentAlignment = Alignment.Center
                ) {
                    val bigSmallColor = when (bigSmall) {
                        "BIG" -> Color(0xFFDAC054)
                        "SMALL" -> Color(0xFF0FA7F5)
                        "Loading..." -> Color(0xFFFF6B35)
                        else -> Color(0xFFFF6B35)
                    }
                    Text(
                        text = bigSmall,
                        color = bigSmallColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CompactHeaderCellWithBorder(text: String, modifier: Modifier = Modifier, isLast: Boolean = false) {
    Box(
        modifier = modifier
            .height(60.dp) // Increased height
            .border(0.5.dp, Color.Gray), // Border for header cells
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp, // Slightly increased font size
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun CompactPeriodCellWithBorder(period: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(50.dp)
            .border(0.5.dp, Color.Gray), // Right border
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
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = period.substring(7),
                    color = Color(0xFFD2691E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = period,
                color = Color(0xFFD2691E),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ColoredNumberText(number: String, colorInfo: String = "") {
    val numberInt = number.toIntOrNull() ?: 0

    // Check if it's a dual-color case
    val isDualColor = (numberInt == 0 || numberInt == 5) && colorInfo.contains("RED + VIOLET")

    if (isDualColor) {
        // Use Box for better alignment
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Left half - Red (positioned slightly left)
            Text(
                text = number,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium, // Changed from Bold to Medium
                color = Color(0xFFE53935), // Red
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(x = (-1).dp) // Reduced offset
            )
            // Right half - Violet (positioned slightly right)
            Text(
                text = number,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium, // Changed from Bold to Medium
                color = Color(0xFF9C27B0), // Violet
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(x = 1.dp) // Reduced offset
            )
        }
    } else {
        // Regular single-color text
        val textColor = when {
            numberInt == 0 -> Color(0xFF9C27B0) // Purple for 0
            numberInt == 5 -> Color(0xFF9C27B0) // Purple for 5
            numberInt % 2 == 0 -> Color(0xFFE53935) // Red for even numbers
            else -> Color(0xFF4CAF50) // Green for odd numbers
        }

        Text(
            text = number,
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium, // Changed from Bold to Medium
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ColorDots(colorText: String) {
    when {
        // Handle all dual color combinations (RED+VIOLET, GREEN+VIOLET, RED+GREEN)
        colorText.contains("RED + VIOLET") || colorText.contains("RED+VIOLET") || 
        colorText.contains("RED + VOILET") || colorText.contains("RED+VOILET") -> {
            // Red + Violet combination
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFFE53935), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF9C27B0), CircleShape)
                )
            }
        }
        colorText.contains("GREEN + VIOLET") || colorText.contains("GREEN+VIOLET") || 
        colorText.contains("GREEN + VOILET") || colorText.contains("GREEN+VOILET") -> {
            // Green + Violet combination
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF9C27B0), CircleShape)
                )
            }
        }
        colorText.contains("RED + GREEN") || colorText.contains("RED+GREEN") -> {
            // Red + Green combination (if this exists)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFFE53935), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                )
            }
        }
        colorText.contains("RED") -> {
            // Single red dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFFE53935), CircleShape)
            )
        }
        colorText.contains("GREEN") -> {
            // Single green dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
            )
        }
        colorText.contains("VIOLET") || colorText.contains("VOILET") -> {
            // Single violet dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFF9C27B0), CircleShape)
            )
        }
        else -> {
            // Loading or unknown state
            Text(
                text = colorText,
                color = Color(0xFFD2691E),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MyHistoryTableforWingo(data: List<List<String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color.Gray) // Outer border
    ) {
        // Header Row - Increased height
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50))
                .height(55.dp) // Increased from 45.dp to 55.dp
        ) {
            CompactHeaderCellWithBorder("Period", Modifier.weight(1.2f))
            CompactHeaderCellWithBorder("Bet On", Modifier.weight(1f))
            CompactHeaderCellWithBorder("Bet\nAmount", Modifier.weight(1f))
            CompactHeaderCellWithBorder("Win/Loss", Modifier.weight(1f))
            CompactHeaderCellWithBorder("Winning\nAmount", Modifier.weight(1f), isLast = true)
        }

        // Data Rows
        data.forEachIndexed { index, row ->
            if (row.size >= 5) {
                val (period, betOn, betAmount, winLoss, winningAmount) = row
                val backgroundColor = if (index % 2 == 0) Color.White else Color(0xFFF8F8F8)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .height(50.dp)
                ) {
                    // Period Cell
                    CompactPeriodCellWithBorder(period = period, modifier = Modifier.weight(1.2f))

                    // Bet On Cell
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(0.5.dp, Color.Gray), // Right border
                        contentAlignment = Alignment.Center
                    ) {
                        val numberValue = betOn.toIntOrNull()
                        if (numberValue != null) {
                            // It's a number, show colored text
                            ColoredNumberText(number = betOn)
                        } else {
                            // It's a color name, show colored dot instead of text
                            when {
                                betOn.equals("Red", ignoreCase = true) -> {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(Color(0xFFE53935), CircleShape)
                                    )
                                }
                                betOn.equals("Violet", ignoreCase = true) -> {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(Color(0xFF9C27B0), CircleShape)
                                    )
                                }
                                betOn.equals("Green", ignoreCase = true) -> {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(Color(0xFF4CAF50), CircleShape)
                                    )
                                }
                                betOn.equals("Big", ignoreCase = true) -> {
                                    Text(
                                        text = betOn,
                                        color = Color(0xFFE1B143),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                betOn.equals("Small", ignoreCase = true) -> {
                                    Text(
                                        text = betOn,
                                        color = Color(0xFF1E88E5),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                else -> {
                                    Text(
                                        text = betOn,
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Bet Amount Cell
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(0.5.dp, Color.Gray), // Right border
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = betAmount,
                            color = Color(0xFF43A047),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Win/Loss Cell
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(0.5.dp, Color.Gray), // Right border
                        contentAlignment = Alignment.Center
                    ) {
                        val isWin = winLoss.equals("Win", ignoreCase = true)
                        val winLossColor = if (isWin) Color(0xFF228B22) else Color(0xFFDC143C)

                        Text(
                            text = winLoss,
                            color = winLossColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Winning Amount Cell - Fixed border issue
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(0.5.dp, Color.Gray), // Added border for last column
                        contentAlignment = Alignment.Center
                    ) {
                        val isWin = winLoss.equals("Win", ignoreCase = true)
                        val winningAmountColor = if (isWin) Color(0xFF228B22) else Color(0xFFDC143C)

                        Text(
                            text = winningAmount,
                            color = winningAmountColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
