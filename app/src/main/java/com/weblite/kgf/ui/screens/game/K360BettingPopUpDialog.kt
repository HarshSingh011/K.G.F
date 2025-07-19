package com.example.weblite.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.viewmodel.K3BettingViewModel // Import the new ViewModel
import kotlinx.coroutines.launch

@Composable
fun K3BettingPopupDialog(
    selectedNumber: Int? = null, // For K3 balls (3-18)
    selectedBetType: String? = null, // For Big, Small, Odd, Even
    onDismiss: () -> Unit,
    onConfirmBet: (Boolean, String?) -> Unit, // (isSuccess, errorMessage)
    viewModel: K3BettingViewModel = hiltViewModel() // Use K3BettingViewModel
) {
    android.util.Log.d("K3BettingPopupDialog", "Dialog initialized with:")
    android.util.Log.d("K3BettingPopupDialog", "- selectedNumber: $selectedNumber")
    android.util.Log.d("K3BettingPopupDialog", "- selectedBetType: $selectedBetType")

    var selectedBalance by remember { mutableStateOf(1) }
    var betAmount by remember { mutableStateOf("1") }
    var selectedMultiplier by remember { mutableStateOf("X1") }
    var isAgreed by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val baseAmount = betAmount.toIntOrNull() ?: 1
    val multiplierValue = selectedMultiplier.removePrefix("X").toIntOrNull() ?: 1
    val totalAmount = baseAmount * multiplierValue

    var selectInputField by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    suspend fun placeBet() {
        isLoading = true
        errorMessage = null

        try {
            val (bidNum, bidType) = when {
                selectedNumber != null -> Pair(selectedNumber.toString(), "number")
                selectedBetType != null -> Pair(selectedBetType, "type") // K3 uses "type" for Big/Small/Odd/Even
                else -> {
                    errorMessage = "No number or bet type selected."
                    onConfirmBet(false, errorMessage)
                    return
                }
            }

            android.util.Log.d("K3BettingPopupDialog", "Placing K3 bet - bidNum: $bidNum, bidType: $bidType")
            android.util.Log.d("K3BettingPopupDialog", "Sending to API: quantity='1', price='$totalAmount'")

            val result = viewModel.placeBet(
                bidNum = bidNum,
                bidType = bidType,
                quantity = multiplierValue.toString(),
                price = totalAmount.toString()
            )

            if (result.isSuccess) {
                onConfirmBet(true, null)
                onDismiss()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Bet placement failed"
                onConfirmBet(false, errorMessage)
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "An error occurred"
            onConfirmBet(false, errorMessage)
        } finally {
            isLoading = false
        }
    }

    // Determine colors for the header based on selected number/bet type
    val (topColor, bottomColor) = when {
        selectedNumber != null -> {
            // K3 Ball colors (3-18)
            when (selectedNumber) {
                in 3..18 -> {
                    val isRed = listOf(3, 5, 7, 9, 11, 13, 15, 17).contains(selectedNumber)
                    if (isRed) Pair(Color(0xFFE53935), Color(0xFFE53935)) // Red
                    else Pair(Color(0xFF4CAF50), Color(0xFF4CAF50)) // Green
                }
                else -> Pair(Color(0xFFFF6B35), Color(0xFFFF6B35)) // Default
            }
        }
        selectedBetType != null -> {
            when (selectedBetType) {
                "Big", "Odd" -> Pair(Color(0xFFFFC107), Color(0xFFFFC107)) // Yellow
                "Small", "Even" -> Pair(Color(0xFF2196F3), Color(0xFF2196F3)) // Blue
                else -> Pair(Color(0xFFFF6B35), Color(0xFFFF6B35)) // Default
            }
        }
        else -> Pair(Color(0xFFFF6B35), Color(0xFFFF6B35)) // Default fallback
    }

    val displayText = when {
        selectedNumber != null -> "Select $selectedNumber"
        selectedBetType != null -> "Select $selectedBetType"
        else -> "Select"
    }

    Dialog(
        onDismissRequest = { /* Do nothing - prevent dismissal on outside click */ },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 90.dp),
            contentAlignment = if (selectInputField) Alignment.Center else Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
                    .background(Color.White, RectangleShape)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp)
                            .background(topColor),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "K3 Lotre 60sec", // Updated text for K3
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp)
                            .background(bottomColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(6.dp))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = displayText,
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Balance",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(1, 10, 100, 1000).forEach { balance ->
                            Button(
                                onClick = {
                                    selectedBalance = balance
                                    betAmount = balance.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedBalance == balance) Color(0xFFFF6B35) else Color(0xFFE0E0E0),
                                    contentColor = if (selectedBalance == balance) Color.White else Color(0xFFFF6B35)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = balance.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bet Amount",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = betAmount,
                        onValueChange = {
                            selectInputField = true
                            betAmount = it
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .padding(start = 12.dp),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 13.sp,
                            color = Color.Black
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B35),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(listOf("X1", "X5", "X10", "X20", "X50", "X100")) { multiplier ->
                        MultiplierPopupButton(
                            text = multiplier,
                            isSelected = selectedMultiplier == multiplier,
                            onClick = {
                                selectedMultiplier = multiplier
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable { isAgreed = !isAgreed }
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                if (isAgreed) Color.Black else Color.Transparent,
                                RoundedCornerShape(3.dp)
                            )
                            .border(
                                2.dp,
                                if (isAgreed) Color.Black else Color.Gray,
                                RoundedCornerShape(3.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAgreed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Checked",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "I agree",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEB3B)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        ),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            if (isAgreed && !isLoading) {
                                coroutineScope.launch {
                                    placeBet()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B35)
                        ),
                        shape = RectangleShape,
                        enabled = isAgreed && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Total amount: ₹ $totalAmount",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
