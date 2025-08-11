package com.weblite.kgf.ui.screens.payScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import android.util.Log
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.navigation.NavController
import com.weblite.kgf.ui.components.ON_DEPO_HIST

@Composable
fun PaymentDepositScreen(
    amount: String = "₹ 100.00",
    navController: NavController,
    onBackClick: () -> Unit = {},
    showNoUpi: Boolean = true,
    qrViewModel: QrCodeViewModel = hiltViewModel(),
    depositViewModel: DepositApiViewModel = hiltViewModel()
) {
    val qrImageUrl by qrViewModel.qrImageUrl.collectAsState()
    var utrNumber by remember { mutableStateOf("") }
    val utrValid = utrNumber.length == 12 && utrNumber.all { it.isDigit() }
    val loading by depositViewModel.loading.collectAsState()
    val error by depositViewModel.error.collectAsState()
    val depositResponse by depositViewModel.depositResponse.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    // Show success and navigate back if deposit is successful
    LaunchedEffect(depositResponse) {
        if (depositResponse?.status == "success") {
            android.widget.Toast.makeText(context, "Deposit successful!", android.widget.Toast.LENGTH_SHORT).show()
            navController.navigate(ON_DEPO_HIST) {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true
            }
        }
    }
    LaunchedEffect(Unit) {
        qrViewModel.fetchQrCode()
    }
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF007BFF))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onBackClick,
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
                Spacer(modifier = Modifier.width(36.dp))
            }
            // Amount Payable
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
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
            Spacer(modifier = Modifier.height(24.dp))
            // OR Divider
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                Text("  OR  ", color = Color.Gray, fontWeight = FontWeight.Bold)
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // QR Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!qrImageUrl.isNullOrBlank()) {
                    var imageError by remember { mutableStateOf<String?>(null) }
                    AsyncImage(
                        model = qrImageUrl,
                        contentDescription = "Deposit QR Code",
                        modifier = Modifier
                            .sizeIn(maxWidth = 240.dp, maxHeight = 240.dp)
                            .fillMaxWidth(0.7f),
                        onError = { error ->
                            imageError = error.result.throwable?.localizedMessage ?: "Unknown error"
                            Log.e("AsyncImage", "Image load error", error.result.throwable)
                        }
                    )
                    if (imageError != null) {
                        Text(
                            text = "Failed to load QR image: $imageError",
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                } else {
                    Text("QR code not available", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // No UPI id warning
            if (showNoUpi) {
                Text(
                    "No UPI id to show please contact support.",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            // UTR Number input
            OutlinedTextField(
                value = utrNumber,
                onValueChange = { if (it.length <= 12) utrNumber = it.filter { ch -> ch.isDigit() } },
                label = { Text("Enter 12-digit UTR number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f),
                isError = utrNumber.isNotEmpty() && !utrValid
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Submit button
            Button(
                onClick = {
                    val amt = amount.filter { it.isDigit() }.toIntOrNull() ?: 0
                    depositViewModel.submitDeposit(amt, utrNumber)
                },
                enabled = utrValid && !loading,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                if (loading) {
                    Text("Submitting...", fontWeight = FontWeight.Bold)
                } else {
                    Text("Submit", fontWeight = FontWeight.Bold)
                }
            }
            if (error != null) {
                Text(error ?: "", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }
            if (depositResponse != null && depositResponse?.status != "success") {
                Text("Deposit submitted! Status: ${depositResponse?.status}", color = Color.Green, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}



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
fun DepositQrImageRow(qrImageUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (qrImageUrl != null) {
            AsyncImage(
                model = qrImageUrl,
                contentDescription = "Deposit QR Code",
                modifier = Modifier
                    .size(180.dp)
            )
        } else {
            Text("QR code not available", color = Color.Gray)
        }
    }
}
