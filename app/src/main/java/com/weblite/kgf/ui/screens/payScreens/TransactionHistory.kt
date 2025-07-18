package com.weblite.kgf.ui.screens.payScreens

import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import java.util.*

data class TransactionData(
    val type: String, // "Deposit" or "Withdraw"
    val amount: String,
    val date: String,
    val txnId: String,
    val status: String
)

@Composable
fun TransactionHistory(
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val backgroundColor = Brush.verticalGradient(
        listOf(Color(0xFF001B40), Color(0xFF002D60))
    )

    var triggerFetch by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("All") }
    var selectedStatus by remember { mutableStateOf("All") }
    var selectedDate by remember { mutableStateOf("dd-mm-yyyy") }
    var expanded by remember { mutableStateOf(false) }
    var showData by remember { mutableStateOf(true) }

    val dummyTransactions = listOf(
        TransactionData("Deposit", "100", "2025-05-19 13:18:58", "12", "Pending"),
        TransactionData("Withdraw", "100", "2025-05-18 16:55:12", "1", "Pending"),
        TransactionData("Deposit", "100", "2025-05-14 16:09:53", "12", "Approved"),
        TransactionData("Withdraw", "100", "2025-04-10 10:10:10", "10", "Rejected"),
        TransactionData("Deposit", "100", "2025-05-19 13:18:58", "12", "Gift Reward"),
        TransactionData("Withdraw", "100", "2025-05-18 16:55:12", "1", "Invitation bonus"),

        )

    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedDate = String.format("%02d-%02d-%04d", day, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    LaunchedEffect(triggerFetch) {
        if (triggerFetch) {
            showData = false
            delay(1000)
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
        // Top Title Bar
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
            Text(
                text = "Transaction History",
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("All", "Withdraw", "Deposit").forEach { type ->
                val isSelected = selectedTab == type
                Button(
                    onClick = { selectedTab = type },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF00BFFF) else Color.White,
                        contentColor = if (isSelected) Color.White else Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(type, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filters
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            // Status Dropdown
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = selectedStatus,
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
                    listOf("All", "Deposit", "Withdraw", "Invitation bonus", "Betting Reward", "Betting Transaction",
                        "Referral Commission","Betting Commission", "Gift Reward", "Vip One Time Reward").forEach {
                        DropdownMenuItem(
                            text = {
                                Text(it, color = Color.Black, lineHeight = 15.sp)
                                   },
                            onClick = {
                                selectedStatus = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Date Picker
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .clickable { datePickerDialog.show() }
                    .padding(horizontal = 12.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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

            // Fetch
            Button(
                onClick = { triggerFetch = true },
                modifier = Modifier.align(Alignment.CenterVertically).height(42.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text("Fetch Data", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!showData) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            val filtered = dummyTransactions.filter {
                (selectedTab == "All" || it.type.equals(selectedTab, true)) &&
                        (selectedStatus == "All" || it.status.equals(selectedStatus, true))
            }

            if (filtered.isEmpty()) {
                Text("No records found.", color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    filtered.forEach {
                        TransactionCard(data = it)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(data: TransactionData) {
    val statusColor = when (data.status.lowercase()) {
        "approved" -> Color(0xFF28A745)
        "rejected" -> Color(0xFFFF0000)
        else -> Color(0xFFFFC107)
    }
    val statusTextColor = when (data.status.lowercase()) {
        "approved" -> Color(0xFFFFFFFF)
        "rejected" -> Color(0xFFFFFFFF)
        else -> Color(0xFF000000)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusButton(data.type, Color(0xFF28A745), Color.White)
                StatusButton(data.status, statusColor, statusTextColor)
            }

            Spacer(modifier = Modifier.height(8.dp))

            InfoText("UserID: ", "6763043294")
            InfoText("Transaction Id: ", data.txnId)
            InfoText("Amount: ", data.amount)
            InfoText("Date: ", data.date)
        }
    }
}

@Composable
fun StatusButton(label: String, background: Color, textColor: Color) {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = background),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.height(35.dp)
    ) {
        Text(label, fontSize = 15.sp, color = textColor)
    }
}

@Composable
fun InfoText(label: String, value: String) {
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)) { append(label) }
            withStyle(SpanStyle(fontWeight = FontWeight.Normal)) { append(value) }
        },
        color = Color.Black,
        fontSize = 14.sp
    )
}
