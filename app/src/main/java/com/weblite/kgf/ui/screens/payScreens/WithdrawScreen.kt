package com.weblite.kgf.ui.screens.payScreens

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.CheckboxDefaults.colors
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
import androidx.compose.material3.*
import com.weblite.kgf.ui.components.AttractiveText
import com.weblite.kgf.ui.screens.auth.CustomInputField2
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.TextField
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.Api2.MainViewModel
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.ui.screens.auth.ChangePasswordDialog
import kotlinx.coroutines.launch

data class UpiAccount(
    val upiUserName: String = "Sushanta behera",
    val accountNumber: String = "9178112233344",
    val upiId: String = "123@pytm"
)
@Composable
fun WithdrawScreen(
    balance: String = "₹ 17,511,164.75",
    onBackClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
){
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF001B40), Color(0xFF002D60))
    )
    val iconSize = 46.dp
    val backgroundCard = Brush.verticalGradient(
        colors = listOf(Color(0xff182548) , Color(0xff2293E3),Color(0xff182548))
    )
    var amount by remember { mutableStateOf("") }
    val withdrawableAmount = "00.00"
    val minWithdrawal = "200.00"
    val maxWithdrawal = "10,000,000.00"
    val remainingWithdrawals = 3
    val yellowColor = Color(0xFF02F9FE)
    val showError = amount.isNotEmpty() && (amount.toFloatOrNull() ?: 0f) < 200

    var showBankcard by remember { mutableStateOf(true) }
    var showUPIcard by remember { mutableStateOf(true) }
    val dashboardState = viewModel.dashboardState.value

    var totalBalance by remember { mutableStateOf(0) }
    if (dashboardState is Resource.Success) {
        dashboardState.data?.result?.let { result ->
            totalBalance = result.totalBalance
        }

    }
    /////info
    var isSelectedUPI by remember { mutableStateOf(false) }
    var isSelectedBank by remember { mutableStateOf(false) }
    val upiAccounts = remember { mutableStateListOf<UpiAccount>() }


    val accountNumber = SharedPrefManager.getString("ACCOUNT_NUMBER") ?: ""
    val userName = SharedPrefManager.getString("UPI_NAME") ?: ""
    val upiId = SharedPrefManager.getString("UPI_ID") ?: ""

    showBankcard = userName.isNotBlank() && accountNumber.isNotBlank()
    showUPIcard = userName.isNotBlank() && upiId.isNotBlank()







    LaunchedEffect(Unit) {
        onShowTopBar(false)
        onShowBottomBar(false)
    }
    var showAddBankDialog by remember { mutableStateOf(false) }
    if (showAddBankDialog) {
        BankAccountDialog(
            onMode = "Add",
            onDismiss = { showAddBankDialog = false },
            onSave = { bankName, fullName, accountNumber, phone, ifsc ->
                SharedPrefManager.setString("BANK_NAME", bankName)
                SharedPrefManager.setString("FULL_NAME", fullName)
                SharedPrefManager.setString("ACCOUNT_NUMBER", accountNumber)
                SharedPrefManager.setString("PHONE_NUMBER", phone)
                SharedPrefManager.setString("IFSC_CODE", ifsc)
                showAddBankDialog = false
            }
        )
    }


    var showUpiDialog by remember { mutableStateOf(false) }
    if (showUpiDialog) {
        UpiDetailsDialog(
            onMode = "Enter",
            onDismiss = { showUpiDialog = false },
            onUpdate = { name, app, upiId ->
                SharedPrefManager.setString("UPI_NAME", name)
                SharedPrefManager.setString("UPI_APP", app)
                SharedPrefManager.setString("UPI_ID", upiId)
                showUpiDialog = false
            }
        )
    }
    ///////
    var showAddBankEditDialog by remember { mutableStateOf(false) }
    if (showAddBankEditDialog) {
        BankAccountDialog(
            onMode = "Edit",
            onDismiss = { showAddBankEditDialog = false },
            onSave = { bankName, fullName, accountNumber, phone, ifsc ->
                SharedPrefManager.setString("BANK_NAME", bankName)
                SharedPrefManager.setString("FULL_NAME", fullName)
                SharedPrefManager.setString("ACCOUNT_NUMBER", accountNumber)
                SharedPrefManager.setString("PHONE_NUMBER", phone)
                SharedPrefManager.setString("IFSC_CODE", ifsc)
                showAddBankEditDialog = false
            }
        )
    }


    var showUpiEditDialog by remember { mutableStateOf(false) }
    if (showUpiEditDialog) {
        UpiDetailsDialog(
            onMode = "Edit",
            onDismiss = { showUpiEditDialog = false },
            onUpdate = { name, app, upiId ->
                SharedPrefManager.setString("UPI_NAME", name)
                SharedPrefManager.setString("UPI_APP", app)
                SharedPrefManager.setString("UPI_ID", upiId)
                showUpiEditDialog = false
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ){
        // Top Row: Back Button, Title, History Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(31.dp),
                    ambientColor = Color(0xff00EBEF),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(32.dp))
                .border(2.dp, Color(0xFF29659A), RoundedCornerShape(32.dp))
                .background(Color.White)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF007BFF))
            }

            Text(
                text = "Withdraw",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onHistoryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007BFF),
                    contentColor = Color(0xFFffffff)
                ),
                shape = RoundedCornerShape(30),
                modifier = Modifier.size(height = 34.dp, width = 138.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text("Withdraw History",
                    fontSize = 15.sp,
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(6.dp))

            Spacer(modifier = Modifier.height(24.dp))

        }
        Spacer(modifier = Modifier.height(24.dp))
        //available Balance card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(150.dp) // Increased height for better proportion
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = Color(0xff00EBEF),
                    spotColor = Color(0xff00EBEF)
                )
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, Color(0xFF29659A), RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xff182548), Color(0xff00A9AA), Color(0xff182548))
                    )
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(16.dp),
                //verticalArrangement = Arrangement.Start,
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.wallet_filled_money_tool),
                        contentDescription = "Refresh",
                        tint = Color.Yellow,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    AttractiveText(
                        text = "Available balance",
                        primaryColor = Color.White,
                        shadowColor = Color(0xFF606060),
                        fontSize = 17.sp,
                        strokeWidth = 1.5f
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                AttractiveText(
                    text = "₹ $totalBalance",
                    primaryColor = Color.White,
                    shadowColor = Color(0xFF606060),
                    fontSize = 30.sp,
                    strokeWidth = 1.5f
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.align(alignment = Alignment.End)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End
                ){
                    AttractiveText(
                        text = "****   **** ",
                        primaryColor = Color.White,
                        shadowColor = Color(0xFF606060),
                        fontSize = 20.sp,
                        strokeWidth = 1.5f
                    )
                }
            }
        }
        /////
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // BANK CARD Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(75.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(30.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(30.dp))
                    .border(2.dp, Color(0xFF29659A), RoundedCornerShape(30.dp))
                    .background(Color(0xFF03801e))
                    .clickable { showAddBankDialog = true },
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column (
                    modifier = Modifier.fillMaxSize().padding(2.dp)
                        .border(2.dp, Color(0xFF000000), RoundedCornerShape(30.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.ppay),
                        contentDescription = "PhonePe",
                        modifier = Modifier.size(iconSize).clickable { showAddBankDialog = true },
                    )
                    Text(
                        "BANK CARD",
                        color = Color.White,
                        lineHeight = 10.sp,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // UPI Details Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(75.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(30.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(30.dp))
                    .border(2.dp, Color(0xFF29659A), RoundedCornerShape(30.dp))
                    .background(
                        Color.White
                    )
                    .clickable { showUpiDialog = true },
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column (
                    modifier = Modifier.fillMaxSize().padding(2.dp)
                        .border(2.dp, Color(0xFF000000), RoundedCornerShape(30.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.ic_upi),
                        contentDescription = "PhonePe",
                        modifier = Modifier.size(width = 48.dp, height = 40.dp)
                    )
                    Text(
                        "UPI Details",
                        color = Color.Black,
                        fontSize = 12.sp,
                        lineHeight = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        ////////
        if (showBankcard) {
            UserBankAccountCard(
                accountNumber = accountNumber,
                isSelected = isSelectedBank,
                onCheckedChange = { checked ->
                    isSelectedBank = checked
                    if (checked) isSelectedUPI = false
                },
                onEditClick = {
                    showAddBankEditDialog = true
                }
            )
        }

        if (showUPIcard) {
            UserUpiCard(
                userName = userName,
                upiId = upiId,
                isSelected = isSelectedUPI,
                onCheckedChange = { checked ->
                    isSelectedUPI = checked
                    if (checked) isSelectedBank = false
                },
                onEditClick = {
                    showUpiEditDialog = true
                }
            )
        }


        /////////

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Need to add beneficiary information to be able to withdraw money",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = yellowColor,
                    spotColor = yellowColor
                )
                .border(1.dp, Color(0xFF29659A), RoundedCornerShape(12.dp))
                .background(Color(0xFFFFFFFF)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Using your CustomInputField2 for amount input
                BasicTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(30.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "Money Icon",
                                tint = Color.Black,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (amount.isEmpty()) {
                                    Text(
                                        text = "Please enter the amount",
                                        color = Color.DarkGray,
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                )
                // Show error text conditionally
                if (showError) {
                    Text(
                        text = "Amount must be at least ₹200",
                        color = Color.Red,
                        fontSize = 15.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 1.dp, start = 18.dp)
                    )
                }


                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Withdrawable amount received",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    Text(
                        "₹$withdrawableAmount",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Withdraw button (styled to match your design)
                val gradientBrush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF182548), Color(0xFF041cba), Color(0xFF182548))
                )

                Button(
                    onClick = { /* Handle withdrawal */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = yellowColor,
                            spotColor = yellowColor
                        )
                        .border(1.dp, Color(0xFF29659A), RoundedCornerShape(18.dp))
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = gradientBrush, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Withdraw",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Information section
                val bullet = "◆"
                val bulletSpacing = 16.sp

                val bulletItems = listOf(
                    "Need to bet ₹0.00 to be able to withdraw",
                    "Withdraw time 00:00–23:59",
                    "Today Remaining Withdrawal Time $remainingWithdrawals",
                    "Withdrawal amount range ₹$minWithdrawal–₹$maxWithdrawal",
                    "Please confirm your beneficial account information before withdrawing. If your information is incorrect, our company will not be liable for the amount of loss.",
                    "If your beneficial information is incorrect, please contact customer service."
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bulletItems.forEach { item ->
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = bulletSpacing))) {
                                        append("$bullet\t")

                                        val regex = Regex("""₹[\d.,\-]+|00:00–23:59|$remainingWithdrawals""")
                                        var lastIndex = 0
                                        regex.findAll(item).forEach { match ->
                                            val start = match.range.first
                                            val end = match.range.last + 1
                                            append(item.substring(lastIndex, start)) // normal text

                                            // styled dynamic part
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color(0xFF000000),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append(item.substring(start, end))
                                            }
                                            lastIndex = end
                                        }
                                        if (lastIndex < item.length) {
                                            append(item.substring(lastIndex))
                                        }
                                    }
                                },
                                fontSize = 15.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

            }
        }


    }

}
//////////////////Bank A/C card
@Composable
fun UserBankAccountCard(
    accountNumber: String,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF003366)),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Title
            Text(
                text = "User Bank Account Details",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onCheckedChange
                )
                Text(text = "Select this bank account", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Account number
            Text(
                text = "Bank Account Number: $accountNumber",
                color = Color.Black,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Edit Button
            Button(
                onClick = onEditClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007BFF), // Blue color
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .width(80.dp)
                    .height(36.dp)
            ) {
                Text("Edit")
            }
        }
    }
}

////////////
@Composable
fun UserUpiCard(
    userName: String,
    upiId: String,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF003366)),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Title
            Text(
                text = "User UPI Details",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onCheckedChange
                )
                Text(text = "Select this UPI account", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User Name
            Text(
                buildAnnotatedString {
                    append("User Name: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(userName)
                    }
                },
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            // UPI ID
            Text(
                text = "UPI ID: $upiId",
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Edit Button
            Button(
                onClick = onEditClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007BFF), // Blue color
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .width(80.dp)
                    .height(36.dp)
            ) {
                Text("Edit")
            }
        }
    }
}



@Composable
fun BankAccountDialog(
    onMode: String ="",
    onDismiss: () -> Unit = {},
    onSave: (bankName: String, fullName: String, accountNumber: String, phoneNumber: String, ifscCode: String) -> Unit

) {
    var bankName by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var ifscCode by remember { mutableStateOf("") }

    val isBankValid = bankName.isNotBlank()
    val isFullNameValid = fullName.isNotBlank()
    val isAccountValid = accountNumber.matches(Regex("^\\d{9,18}$"))
    val isPhoneValid = phoneNumber.matches(Regex("^\\d{10}$"))
    val isIfscValid = ifscCode.matches(Regex("^[A-Z]{4}0[A-Z0-9]{6}$"))

    val allValid = isBankValid && isFullNameValid && isAccountValid && isPhoneValid && isIfscValid

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),

        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Close",
                            tint = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$onMode a bank account number",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Divider(color = Color.Black, modifier = Modifier.fillMaxWidth().height(1.dp))
                Spacer(modifier = Modifier.height(7.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFE6E6), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "To ensure the safety of your funds, please bind your bank account",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                InputFieldWithValidation(
                    label = "Choose a bank",
                    placeholder = "Please Select a bank",
                    icon = Icons.Default.AccountBalance,
                    value = bankName,
                    onValueChange = { bankName = it },
                    isValid = isBankValid,
                    errorMessage = "Please select a bank"
                )

                InputFieldWithValidation(
                    label = "Full recipient's name",
                    placeholder = "Enter Full recipient's name",
                    icon = Icons.Default.Person,
                    value = fullName,
                    onValueChange = { fullName = it },
                    isValid = isFullNameValid,
                    errorMessage = "Name cannot be empty"
                )

                InputFieldWithValidation(
                    label = "Bank account number",
                    placeholder = "Enter Bank account number",
                    icon = Icons.Default.CreditCard,
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    isValid = isAccountValid,
                    errorMessage = "Invalid account number"
                )

                InputFieldWithValidation(
                    label = "Phone number",
                    placeholder = "Enter Phone number",
                    icon = Icons.Default.Phone,
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    isValid = isPhoneValid,
                    errorMessage = "Enter 10-digit phone number"
                )

                InputFieldWithValidation(
                    label = "IFSC code",
                    placeholder = "Enter IFSC code number",
                    icon = Icons.Default.VpnKey,
                    value = ifscCode,
                    onValueChange = { ifscCode = it.uppercase() },
                    isValid = isIfscValid,
                    errorMessage = "Invalid IFSC format"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onSave(bankName, fullName, accountNumber, phoneNumber, ifscCode)
                    },
                    enabled = allValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6600),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFFF6600).copy(alpha = 0.4f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Save")
                }

            }
        }
    }
}

@Composable
fun InputFieldWithValidation(
    label: String,
    placeholder: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    errorMessage: String
) {
    var isTouched by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFF6600),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 14.sp, color = Color(0xFFFF6600))
        }

        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = value,
            onValueChange = {
                if (!isTouched) isTouched = true
                onValueChange(it)
            },
            placeholder = { Text(placeholder, color = Color.DarkGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(6.dp),
            singleLine = true
        )

        if (isTouched && !isValid) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 10.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}


@Preview
@Composable
fun WithPre(){
    //WithdrawalScreen()
//    AddBankAccountDialog(
//        onSave = { _, _ ->
//             false
//        }
//    )
    var isSelected by remember { mutableStateOf(false) }

    UserBankAccountCard(
        accountNumber = "9178112233344",
        isSelected = isSelected,
        onCheckedChange = { isSelected = it },
        onEditClick = {
            // Do edit action here
        }
    )

//    UpiDetailsDialog(
//        onUpdate = { _, _, _ ->
//            false
//        }
//    )
}

@Composable
fun UpiDetailsDialog(
    onMode: String ="",
    onDismiss: () -> Unit = {},
    onUpdate: (name: String, app: String, upiId: String) -> Unit

) {
    var name by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    var selectedApp by remember { mutableStateOf("") }

    var isNameTouched by remember { mutableStateOf(false) }
    var isUpiTouched by remember { mutableStateOf(false) }

    val isNameValid = name.isNotBlank()
    val isUpiValid = upiId.matches(Regex("^[\\w.-]+@[\\w]+$"))
    val isAppSelected = selectedApp.isNotBlank()

    val allValid = isNameValid && isUpiValid && isAppSelected

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Close Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Red)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "$onMode UPI Details",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6600),
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }

                // Name Field
                TextField(
                    value = name,
                    onValueChange = {
                        if (!isNameTouched) isNameTouched = true
                        name = it
                    },
                    placeholder = { Text("Name", color = Color.DarkGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Color(0xFFFF6600), shape =  RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color(0x00FF6600),
                        unfocusedIndicatorColor = Color(0x00FF6600)
                    ),
                    singleLine = true
                )

                if (isNameTouched && !isNameValid) {
                    Text("Name cannot be empty", color = Color.Red, fontSize = 10.sp)
                }

                // Radio Buttons
                Column {
                    Text("Select App:", color = Color(0xFFFF6600), fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedApp == "GPay",
                            onClick = { selectedApp = "GPay" }
                        )
                        Text("Google Pay (GPay)", color = Color(0xFFEE5500))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedApp == "PhonePe",
                            onClick = { selectedApp = "PhonePe" }
                        )
                        Text("PhonePe", color = Color(0xFFEE5500))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedApp == "Paytm",
                            onClick = { selectedApp = "Paytm" }
                        )
                        Text("Paytm", color = Color(0xFFEE5500))
                    }

                    if (!isAppSelected) {
                        Text("Please select a UPI App", color = Color.Red, fontSize = 12.sp)
                    }
                }

                // UPI ID Field
                TextField(
                    value = upiId,
                    onValueChange = {
                        if (!isUpiTouched) isUpiTouched = true
                        upiId = it
                    },
                    placeholder = { Text("Enter UPI ID", color = Color.DarkGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Color(0xFFFF6600), shape =  RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = TextStyle(
                        color = Color.Black
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color(0x00FF6600),
                        unfocusedIndicatorColor = Color(0x00FF6600)
                    ),
                    singleLine = true
                )

                if (isUpiTouched && !isUpiValid) {
                    Text("Invalid UPI ID (e.g. name@bank)", color = Color.Red, fontSize = 12.sp)
                }

                // Update Button
                Button(
                    onClick = { onUpdate(name, selectedApp, upiId) },
                    enabled = allValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6600),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFFF6600).copy(alpha = 0.4f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Update")
                }
            }
        }
    }
}
