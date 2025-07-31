package com.weblite.kgf.ui.screens.payScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PaymentDepositDialog(
    amount: String = "₹ 100.00",
    onDismiss: () -> Unit = {},
    showNoUpi: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        containerColor = Color.Transparent,
        shape = RectangleShape,
        title = null,
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card with shadow and square corners
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp, vertical = 0.dp)
                            .shadow(16.dp, shape = RectangleShape)
                            .background(Color.White, shape = RectangleShape)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            // Top Bar with square corners
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF007BFF))
                                    .padding(horizontal = 8.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = onDismiss,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Text("←", fontSize = 22.sp, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Payment",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(36.dp)) // For symmetry
                                }
                            }

                            // Amount Payable Card with square corners and shadow, overlapping top bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .offset(y = (-15).dp)
                                    .shadow(8.dp, shape = RectangleShape)
                                    .background(Color.White, shape = RectangleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        text = "Amount Payable  ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = amount,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color(0xFF007BFF)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Payment Methods
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                PaymentMethodCard(R.drawable.paytm, "Paytm")
                                Spacer(modifier = Modifier.height(14.dp))
                                PaymentMethodCard(R.drawable.phonepe, "PhonePe")
                                Spacer(modifier = Modifier.height(14.dp))
                                PaymentMethodCard(R.drawable.gpay, "G Pay")
                                Spacer(modifier = Modifier.height(14.dp))
                                PaymentMethodCard(R.drawable.upi, "UPI")
                            }

                            if (showNoUpi) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    "No UPI id to show please contact support.",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    )
}


data class DepositHistoryItem(
    val type: String,
    val status: String,
    val balance: String,
    val time: String,
    val orderNumber: String
)



@Composable
fun PaymentMethodCard(iconRes: Int, label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(4.dp, shape = RectangleShape)
            .background(Color.White, shape = RectangleShape),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 18.dp)
        ) {
            Image(painter = painterResource(id = iconRes), contentDescription = label, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(18.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color.Black)
        }
    }
}

@Composable
fun DepositHistoryRow(item: DepositHistoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.type, color = Color(0xFF00A651), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(item.status, color = Color(0xFF00A651), fontWeight = FontWeight.Bold)
            }
            Text("Balance: ${item.balance}", color = Color.Black, fontSize = 14.sp)
            Text("Time: ${item.time}", color = Color.Gray, fontSize = 13.sp)
            Text("Order number: ${item.orderNumber}", color = Color(0xFF007BFF), fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentDepositDialogPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)) // semi-transparent black for dialog overlay
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        PaymentDepositDialog(
            amount = "₹ 100.00",
            onDismiss = {},
            showNoUpi = true
        )
    }
}