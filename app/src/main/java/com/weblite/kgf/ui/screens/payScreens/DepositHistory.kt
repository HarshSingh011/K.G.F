package com.weblite.kgf.ui.screens.payScreens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.weblite.kgf.R

@Composable
fun DepositHistory(
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF001B40), Color(0xFF002D60))
    )

    var selectedOption by remember { mutableStateOf("All") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedDate by remember { mutableStateOf("dd-mm-yyyy") }
    var expanded by remember { mutableStateOf(false) }
    var showData by remember { mutableStateOf(true) }
    var triggerFetch by remember { mutableStateOf(false) }

    data class DepositData(val amount: String, val time: String, val orderNo: String, val method: String, val status: String, val date: String)

    val dummyData = listOf(
        DepositData("₹500.00", "25 June 2025, 01:18 PM", "RC2025051913185843584303g", "UPI x QR", "to be paid", "2025-06-25"),
        DepositData("₹200.00", "22 June 2025, 04:55 PM", "RC2025051816551213792306g", "Paytm", "failed", "2025-06-22"),
        DepositData("₹700.00", "20 June 2025, 03:15 PM", "RC2025051815550099999999a", "UPI x QR", "approved", "2025-06-20")
    )

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                selectedDate = formattedDate
            },
            2025, 5, 18
        )
    }

    val filterOptions = listOf("All", "UPI x QR", "Paytm")

    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
    }

    LaunchedEffect(triggerFetch) {
        if (triggerFetch) {
            showData = false
            kotlinx.coroutines.delay(1500)
            showData = true
            triggerFetch = false
        }
    }

    val filteredList = dummyData.filter {
        (selectedOption == "All" || it.method.equals(selectedOption, ignoreCase = true)) &&
                (selectedFilter == "All" || it.status.equals(selectedFilter, ignoreCase = true)) &&
                (selectedDate == "dd-mm-yyyy" || it.date == selectedDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(16.dp)
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
            Text(
                text = "Deposit History",
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
                    Text(option, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
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
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = Color(0xFFEEEEFF)
                ) {
                    listOf("All", "To Be Paid", "Failed", "Approved").forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item, color = Color.Black) },
                            onClick = {
                                selectedFilter = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .clickable { datePickerDialog.show() }
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    BasicTextField(
                        value = selectedDate,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, lineHeight = 14.sp),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Black)
                }
            }

            Button(
                onClick = {
                    triggerFetch = true
                    Toast.makeText(context, "Fetching data...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.align(Alignment.CenterVertically).height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text("Fetch", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!showData) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            if (filteredList.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    filteredList.forEach {
                        DepositCard(it.amount, it.time, it.orderNo, it.status)
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Data not found", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun DepositCard(amount: String, time: String, orderNumber: String, status: String) {
    val statusColor = when (status.lowercase()) {
        "approved" -> Color(0xFF28A745)
        "failed" -> Color(0xFFFF0000)
        else -> Color(0xFFFFC107)
    }
    val statusTextColor = when (status.lowercase()) {
        "approved" -> Color(0xFFFFFFFF)
        "failed" -> Color(0xFFFFFFFF)
        else -> Color(0xFF000000)
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Cyan),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Deposit", color = Color.White, fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFF28A745), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp))
                Text(status.replaceFirstChar { it.uppercase() }, color = statusTextColor, fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)) {
                        append("Balance: ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(amount)
                    }
                },
                color = Color.Black,
                fontSize = 14.sp
            )

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)) {
                        append("Time: ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(time)
                    }
                },
                color = Color.Black,
                fontSize = 14.sp
            )

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)) {
                        append("Order number: ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(orderNumber)
                    }
                },
                color = Color.Black,
                fontSize = 14.sp
            )

        }
    }
}
