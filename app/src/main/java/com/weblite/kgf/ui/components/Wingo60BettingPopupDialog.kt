package com.example.weblite.components


import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
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
    selectedNumberBackgroundColor: Color? = null,
    onDismiss: () -> Unit,
    onConfirmBet: (Int?, String?, Int, Int) -> Unit, // (number, color, amount, multiplier)
//    viewModel: Wingo60GameViewModel? = null
    viewModel: Wingo60GameViewModel? = hiltViewModel()
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
            val (bidNum, bidType) = when {
                selectedNumber != null -> Pair(selectedNumber.toString(), "number")
                selectedColor != null -> Pair(selectedColor, "color")
                else -> Pair("", "number")
            }

            android.util.Log.d("Wingo60BettingPopupDialog", "Placing bet - bidNum: $bidNum, bidType: $bidType")
            android.util.Log.d("Wingo60BettingPopupDialog", "selectedNumber: $selectedNumber, selectedColor: $selectedColor")

            val result = viewModel?.placeBet(
                bidNum = bidNum,
                bidType = bidType,
                quantity = multiplierValue.toString(),
                price = totalAmount.toString()
            ) ?: Result.success("Preview")

            if (result.isSuccess) {
                onConfirmBet(selectedNumber, selectedColor, baseAmount, multiplierValue)
                onDismiss()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Bet placement failed"
                // No error callback, just show error
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "An error occurred"
        } finally {
            isLoading = false
        }
    }

    // MODIFIED: Updated color logic for K3 balls and bet options to be exhaustive
    val (topColor, bottomColor) = when {
        // Win Go numbers 0 and 5: split color
        selectedNumber == 0 -> Pair(Color(0xFFE53935), Color(0xFF9C27B0)) // Red + Violet (for 0)
        selectedNumber == 5 -> Pair(Color(0xFF4CAF50), Color(0xFF9C27B0)) // Green + Violet (for 5)
        // For numbers 0-9, use the coin's background color for both top and bottom
        selectedNumber != null && selectedNumber in 0..9 && selectedNumberBackgroundColor != null -> Pair(selectedNumberBackgroundColor, selectedNumberBackgroundColor)
        // K3 Ball colors (3-18) - assuming these are single colors
        selectedNumber in listOf(3, 7, 9, 11, 13, 15, 17) -> Pair(Color(0xFFE53935), Color(0xFFE53935)) // Red
        selectedNumber in listOf(4, 6, 8, 10, 12, 14, 16, 18) -> Pair(Color(0xFF4CAF50), Color(0xFF4CAF50)) // Green
        // Bet option colors
        selectedColor == "Big" -> Pair(Color(0xFFFFC107), Color(0xFFFFC107)) // Yellow for Big
        selectedColor == "Small" -> Pair(Color(0xFF2196F3), Color(0xFF2196F3)) // Blue for Small
        selectedColor == "Odd" -> Pair(Color(0xFFFFC107), Color(0xFFFFC107)) // Yellow
        selectedColor == "Even" -> Pair(Color(0xFF2196F3), Color(0xFF2196F3)) // Blue
        selectedColor == "Green" -> Pair(Color(0xFF4CAF50), Color(0xFF4CAF50))
        selectedColor == "Violet" -> Pair(Color(0xFF9C27B0), Color(0xFF9C27B0))
        selectedColor == "Red" -> Pair(Color(0xFFE53935), Color(0xFFE53935))
        // Fallback for other numbers
        selectedNumber != null -> {
            val singleColor = getNumberBackgroundColor(selectedNumber)
            Pair(singleColor, singleColor)
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
        // Keyboard-aware dialog placement and overlay
        val ime = WindowInsets.ime
        val imeVisible = ime.getBottom(LocalDensity.current) > 0
        val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
        val showAboveKeyboard = imeBottom > 0

        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(bottom = if (showAboveKeyboard) 40.dp else 100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White, RectangleShape)
            ) {
                // All existing dialog content preserved below
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    // Header Section with Split Colors - REDUCED HEIGHT
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
                                text = "Win Go 60s",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                                .background(bottomColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp), // Add horizontal padding to prevent edge overflow
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .fillMaxHeight(), // Ensure it doesn't exceed parent height
                            ) {
                                Text(
                                    text = displayText,
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis // Prevent overflow
                                )
                            }
                        }
                    }
                    // Balance Selection
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
                            modifier = Modifier
                                .width(IntrinsicSize.Min), // Ensures Row is only as wide as needed
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                    modifier = Modifier
                                        .padding(0.dp), // Remove extra padding
                                    contentPadding = PaddingValues(0.dp) // Remove Button's internal padding
                                ) {
                                    Text(
                                        text = balance.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                    // Bet Amount Input
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
                    // Multiplier Selection
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
                    // Agreement Checkbox
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
                    // Buttons
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
}



//@Preview(showBackground = true)
//@Composable
//fun Wingo60BettingPopupDialogPreview() {
//    MaterialTheme {
//        Wingo60BettingPopupDialog(
//            selectedNumber = 5,
//            selectedColor = null,
//            onDismiss = {},
//            onConfirmBet = { _, _, _, _ -> },
//            viewModel = null // Pass null for preview
//        )
//    }
//}