package com.example.windialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun WinDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    winAmount: String = "₹1079.96",
    period: String = "30 Seconds",
    periodNumber: String = "2504251540",
    resultColors: List<String> = listOf("GREEN", "VIOLET", "5", "BIG"),
    autoCloseSeconds: Int = 3
) {
    var countdown by remember { mutableStateOf(autoCloseSeconds) }

    // Auto-close countdown effect
    LaunchedEffect(isVisible) {
        if (isVisible) {
            countdown = autoCloseSeconds
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            onDismiss()
        }
    }

    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopCenter
            ) {
                // Main dialog content
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp), // Space for the trophy icon
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B35),
                                        Color(0xFFFF8E53)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(40.dp)) // Space for trophy

                            // Congratulations text
                            Text(
                                text = "Congratulations",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Results row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Results",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                resultColors.forEach { result ->
                                    val backgroundColor = when (result) {
                                        "GREEN" -> Color(0xFF4CAF50)
                                        "VIOLET" -> Color(0xFF9C27B0)
                                        "5" -> Color(0xFF4CAF50)
                                        "BIG" -> Color(0xFFFFEB3B)
                                        else -> Color.Gray
                                    }

                                    val textColor = when (result) {
                                        "BIG" -> Color.Black
                                        else -> Color.White
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = backgroundColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = result,
                                            color = textColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Winning amount card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.9f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Winning",
                                        fontSize = 16.sp,
                                        color = Color(0xFFFF6B35),
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = winAmount,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF6B35)
                                    )

                                    Text(
                                        text = "Period: $period $periodNumber",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Auto-close indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            color = Color.White,
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Auto-close in $countdown seconds",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Trophy icon at the top
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.TopCenter),
                    contentAlignment = Alignment.Center
                ) {
                    // You can replace this with your actual trophy image
                    // For now, using a placeholder background
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700),
                                        Color(0xFFFF8C00)
                                    )
                                ),
                                shape = androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // If you have the trophy image resource, uncomment this:
                        /*
                        Image(
                            painter = painterResource(id = R.drawable.trophy_icon),
                            contentDescription = "Trophy",
                            modifier = Modifier.size(60.dp),
                            contentScale = ContentScale.Fit
                        )
                        */

                        // Placeholder trophy emoji
                        Text(
                            text = "🏆",
                            fontSize = 40.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun WinDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { showDialog = true }) {
                Text("Show Win Dialog")
            }

            WinDialog(
                isVisible = showDialog,
                onDismiss = { showDialog = false },
                winAmount = "₹1079.96",
                period = "30 Seconds",
                periodNumber = "2504251540",
                resultColors = listOf("GREEN", "VIOLET", "5", "BIG"),
                autoCloseSeconds = 3
            )
        }
    }
}

// Usage example in your Activity or other Composable
@Composable
fun MainScreen() {
    var showWinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { showWinDialog = true }
        ) {
            Text("Show Win Dialog")
        }

        WinDialog(
            isVisible = showWinDialog,
            onDismiss = { showWinDialog = false }
        )
    }
}