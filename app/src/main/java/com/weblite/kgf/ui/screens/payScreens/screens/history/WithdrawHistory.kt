package com.weblite.kgf.ui.screens.payScreens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "approved" -> Color(0xFF28A745)
        "rejected", "failed" -> Color(0xFFFF0000)
        else -> Color(0xFFFFC107)
    }
}

data class WithdrawalData(
    val amount: String,
    val date: String,
    val txnId: String,
    val method: String,
    val status: String
)

@Composable
fun WithdrawHistory(
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF001B40), Color(0xFF002D60))
    )

    var triggerFetch by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("All") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedDate by remember { mutableStateOf("dd-mm-yyyy") }
    var expanded by remember { mutableStateOf(false) }
    var showData by remember { mutableStateOf(true) }

    val dummyData = listOf(
        WithdrawalData("₹250.00", "2025-06-19 01:46:05", "TXN_68531e85", "UPI", "Pending"),
        WithdrawalData("₹500.00", "2025-05-19 13:40:07", "TXN_682ae75f", "Bank Card", "Pending"),
        WithdrawalData("₹600.00", "2025-06-15 17:50:00", "TXN_123456", "UPI", "Approved"),
        WithdrawalData("₹700.00", "2025-06-10 10:10:10", "TXN_234567", "Bank Card", "Rejected")
    )

    val datePickerDialog = remember {
        DatePickerDialog(context, { _, y, m, d ->
            selectedDate = String.format("%02d-%02d-%04d", d, m + 1, y)
        }, 2025, 5, 18)
    }

    val filterOptions = listOf("All", "Bank Card", "UPI")

    LaunchedEffect(triggerFetch) {
        if (triggerFetch) {
            showData = false
            delay(1500)
            showData = true
            triggerFetch = false
        }
    }

    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth().height(50.dp)
                .clip(RoundedCornerShape(32.dp))
                .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF007BFF))
            }
            Text("Withdraw History",
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            filterOptions.forEach { option ->
                val isSelected = option == selectedOption
                Button(
                    onClick = { selectedOption = option },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFFF8C00) else Color.White,
                        contentColor = if (isSelected) Color.White else Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(option, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicTextField(
                        value = selectedFilter,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
                }

                DropdownMenu(
                    modifier = Modifier.width(200.dp),
                    containerColor = Color(0xFFEEEEFF),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("All", "Approved", "Rejected", "Pending").forEach { item ->
                        DropdownMenuItem(text = { Text(item, color = Color.Black) }, onClick = {
                            selectedFilter = item
                            expanded = false
                        })
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .clickable { datePickerDialog.show() }
                    .padding(horizontal = 12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicTextField(
                        value = selectedDate,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Black)
                }
            }

            Button(
                onClick = { triggerFetch = true },
                modifier = Modifier.align(Alignment.CenterVertically).height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text("Fetch", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!showData) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            val filtered = dummyData.filter {
                (selectedOption == "All" || it.method.equals(selectedOption, true)) &&
                        (selectedFilter == "All" || it.status.equals(selectedFilter, true))
            }

            if (filtered.isEmpty()) {
                Text("No records found.", color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    filtered.forEach {
                        WithdrawCard(it)
                    }
                }
            }
        }
    }
}

@Composable
fun WithdrawCard(data: WithdrawalData) {
    val statusColor = getStatusColor(data.status)
    val statusTextColor = when (data.status.lowercase()) {
        "approved" -> Color(0xFFFFFFFF)
        "rejected" -> Color(0xFFFFFFFF)
        else -> Color(0xFF000000)
    }

    Card(
        modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(35.dp)
                ) {
                    Text("Withdraw", fontSize = 15.sp, color = Color.White)
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(35.dp)
                ) {
                    Text(data.status, fontSize = 15.sp, color = statusTextColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LabelValueText("Transaction Id: ", data.txnId)
            LabelValueText("Payment Method: ", data.method)
            LabelValueText("Amount: ", data.amount)
            LabelValueText("Date: ", data.date)
        }
    }
}

@Composable
fun LabelValueText(label: String, value: String) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)) {
                append(label)
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                append(value)
            }
        },
        color = Color.Black,
        fontSize = 14.sp
    )
}
