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
import com.weblite.kgf.ui.screens.game.getNumberBackgroundColor
import com.weblite.kgf.viewmodel.Wingo60GameViewModel // Changed ViewModel
import kotlinx.coroutines.launch

@Composable
fun Wingo60BettingPopupDialog(
    selectedNumber: Int? = null,
    selectedColor: String? = null,
    onDismiss: () -> Unit,
    // MODIFIED: onConfirmBet now signals success/failure back to the parent
    onConfirmBet: (Boolean, String?) -> Unit, // (isSuccess, errorMessage)
    viewModel: Wingo60GameViewModel = hiltViewModel() // Changed ViewModel
) {
    // Log the initial values received by the dialog
    android.util.Log.d("Wingo60BettingPopupDialog", "Dialog initialized with:")
    android.util.Log.d("Wingo60BettingPopupDialog", "- selectedNumber: $selectedNumber")
    android.util.Log.d("Wingo60BettingPopupDialog", "- selectedColor: $selectedColor")

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

    // Function to place bet
    suspend fun placeBet() {
        isLoading = true
        errorMessage = null

        try {
            // Determine bidNum and bidType correctly
            val (bidNum, bidType) = when {
                selectedNumber != null -> {
                    // User selected a number (0-9)
                    Pair(selectedNumber.toString(), "number")
                }
                selectedColor != null -> {
                    // User selected a color (Green, Red, Violet, Big, Small)
                    // Assuming Big/Small are treated as 'color' type bets by the API
                    Pair(selectedColor, "color")
                }
                else -> {
                    // This case should ideally not be reached if UI ensures one is selected
                    errorMessage = "No number or color selected."
                    onConfirmBet(false, errorMessage) // Signal failure to parent
                    return
                }
            }

            // Log the betting details for debugging
            android.util.Log.d("Wingo60BettingPopupDialog", "Placing bet - bidNum: $bidNum, bidType: $bidType")
            android.util.Log.d("Wingo60BettingPopupDialog", "selectedNumber: $selectedNumber, selectedColor: $selectedColor")
            android.util.Log.d("Wingo60BettingPopupDialog", "Sending to API: quantity='1', price='$totalAmount'") // Added log for price and quantity

            val result = viewModel.placeBet( // Using Wingo60GameViewModel's placeBet
                bidNum = bidNum,
                bidType = bidType,
                quantity = multiplierValue.toString(), // Quantity is fixed to "1" as per current logic
                price = totalAmount.toString() // Price is the total amount (baseAmount * multiplier)
            )

            if (result.isSuccess) {
                // Bet was successful
                onConfirmBet(true, null) // Signal success to parent
                onDismiss() // Dismiss the dialog
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Bet placement failed"
                onConfirmBet(false, errorMessage) // Signal failure to parent
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "An error occurred"
            onConfirmBet(false, errorMessage) // Signal failure to parent
        } finally {
            isLoading = false
        }
    }

    // MODIFIED: Updated color logic for K3 balls and bet options to be exhaustive
    val (topColor, bottomColor) = when {
        selectedNumber != null -> {
            when (selectedNumber) {
                // Win Go specific numbers with two colors
                0 -> Pair(Color(0xFFE53935), Color(0xFF9C27B0)) // Red + Violet (for 0)
                5 -> Pair(Color(0xFF4CAF50), Color(0xFF9C27B0)) // Green + Violet (for 5)
                // K3 Ball colors (3-18) - assuming these are single colors
                3, 5, 7, 9, 11, 13, 15, 17 -> Pair(Color(0xFFE53935), Color(0xFFE53935)) // Red
                4, 6, 8, 10, 12, 14, 16, 18 -> Pair(Color(0xFF4CAF50), Color(0xFF4CAF50)) // Green
                // Other numbers (1, 2, 6, 7, 8, 9 from Wingo60Screen's NumberItem)
                else -> {
                    val singleColor = getNumberBackgroundColor(selectedNumber)
                    Pair(singleColor, singleColor)
                }
            }
        }
        selectedColor != null -> {
            when (selectedColor) {
                "Big", "Odd" -> Pair(Color(0xFFFFC107), Color(0xFFFFC107)) // Yellow for Big/Odd
                "Small", "Even" -> Pair(Color(0xFF2196F3), Color(0xFF2196F3)) // Blue for Small/Even
                "Green" -> Pair(Color(0xFF4CAF50), Color(0xFF4CAF50))
                "Violet" -> Pair(Color(0xFF9C27B0), Color(0xFF9C27B0))
                "Red" -> Pair(Color(0xFFE53935), Color(0xFFE53935))
                else -> Pair(Color(0xFFFF6B35), Color(0xFFFF6B35)) // Default for unknown colors
            }
        }
        // Default fallback if both selectedNumber and selectedColor are null
        else -> Pair(Color(0xFFFF6B35), Color(0xFFFF6B35))
    }

    // Update display text for K3
    val displayText = when {
        selectedNumber != null -> "Select $selectedNumber"
        selectedColor != null -> "Select $selectedColor"
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
        // Semi-transparent background overlay
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 90.dp),
            contentAlignment = if (selectInputField) Alignment.Center else Alignment.BottomEnd
        ) {
            // Dialog content positioned at bottom
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
                    .background(Color.White, RectangleShape)
            ) {
                // Header Section with Split Colors - REDUCED HEIGHT
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Top Row - "Win Go 60s" with first color - REDUCED HEIGHT
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp) // Reduced from 50dp to 35dp
                            .background(topColor),
                        contentAlignment = Alignment.Center
                    ) {
                        // Close button at top right
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp) // Reduced size
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp) // Reduced size
                            )
                        }

                        // Center text - Updated for Win Go
                        Text(
                            text = "Win Go 60s", // Updated text
                            color = Color.White, // Changed to white for better visibility
                            fontSize = 16.sp, // Reduced from 20sp
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Bottom Row - "Select [number]" with second color - REDUCED HEIGHT
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp) // Reduced from 50dp to 35dp
                            .background(bottomColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(6.dp)) // Reduced corner radius
                                .padding(horizontal = 16.dp, vertical = 4.dp) // Reduced padding
                        ) {
                            Text(
                                text = displayText,
                                color = Color.Black,
                                fontSize = 14.sp, // Reduced font size
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Balance Selection - REDUCED SPACING
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp), // Reduced padding
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Balance",
                        color = Color.Black,
                        fontSize = 16.sp, // Reduced from 18sp
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp), // Reduced spacing
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(1, 10, 100, 1000).forEach { balance ->
                            Button(
                                onClick = {
                                    selectedBalance = balance
                                    betAmount = balance.toString()
                                },
                                shape = RoundedCornerShape(10.dp), // Reduced corner radius
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedBalance == balance) Color(0xFFFF6B35) else Color(0xFFE0E0E0),
                                    contentColor = if (selectedBalance == balance) Color.White else Color(0xFFFF6B35)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp), // Reduced padding
                                modifier = Modifier.height(32.dp) // Reduced height
                            ) {
                                Text(
                                    text = balance.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp // Reduced font size
                                )
                            }
                        }
                    }
                }

                // Bet Amount Input - REDUCED SPACING
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp), // Reduced padding
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bet Amount",
                        color = Color.Black,
                        fontSize = 16.sp, // Reduced from 18sp
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = betAmount,
                        onValueChange = {
                            selectInputField = true
                            betAmount = it
                        },
                        modifier = Modifier
                            .height(50.dp) // Reduced from 50dp
                            .padding(start = 12.dp), // Reduced padding
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 13.sp, // Reduced font size
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
                        shape = RoundedCornerShape(5.dp) // Reduced corner radius
                    )
                }

                Spacer(modifier = Modifier.height(6.dp)) // Reduced spacing

                // Multiplier Selection - REDUCED SPACING
                LazyRow(
                    modifier = Modifier.padding(horizontal = 6.dp), // Reduced padding
                    horizontalArrangement = Arrangement.spacedBy(3.dp) // Reduced spacing
                ) {
                    items(listOf("X1", "X5", "X10", "X20", "X50", "X100")) { multiplier ->
                        MultiplierPopupButton(
                            text = multiplier,
                            isSelected = selectedMultiplier == multiplier,
                            onClick = {
                                selectedMultiplier = multiplier
                                // Only update the multiplier, don't change betAmount
                                // Total amount will automatically recalculate
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing

                // Agreement Checkbox - REDUCED SPACING
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 12.dp) // Reduced padding
                        .clickable { isAgreed = !isAgreed }
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp) // Reduced size
                            .background(
                                if (isAgreed) Color.Black else Color.Transparent,
                                RoundedCornerShape(3.dp) // Reduced corner radius
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
                                modifier = Modifier.size(14.dp) // Reduced size
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp)) // Reduced spacing

                    Text(
                        text = "I agree",
                        color = Color.Black,
                        fontSize = 14.sp, // Reduced font size
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp)) // Reduced spacing

                // Error message display
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

                // Buttons - REDUCED HEIGHT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.height(40.dp), // Reduced from 48dp
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        ),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White,
                            fontSize = 14.sp, // Reduced font size
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
                            .height(40.dp), // Reduced from 48dp
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
                                fontSize = 14.sp, // Reduced font size
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

