package com.weblite.kgf.ui.screens.payScreens
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.weblite.kgf.R
@Composable
fun DepositScreen(
    balance: String = "₹ 17,511,164.75",
    onBackClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit

) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF001B40), Color(0xFF002D60))
    )
    val iconSize = 64.dp
    val backgroundCard = Brush.verticalGradient(
        colors = listOf(Color(0xff182548) , Color(0xff2293E3),Color(0xff182548))
    )
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
                text = "Deposit",
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
                modifier = Modifier.size(height = 34.dp, width = 125.dp),
                contentPadding = PaddingValues(1.dp)
            ) {
                Text("Deposit History",
                    fontSize = 15.sp,
                    lineHeight = 16.sp)
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
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
                        colors = listOf(Color(0xff182548) , Color(0xff02F9FE),Color(0xff182548))
                    )
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF003366))
        ) {
            Column(//Color(0xff2293E3) Color(0xff2293E3)
                modifier = Modifier.fillMaxSize()
                    .background(backgroundCard),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Balance",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(balance,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // E-wallet Column
            Card(
                modifier = Modifier.weight(1f)
                    .background(backgroundCard)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, Color(0xFF29659A), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF002B63))
            ) {
                Column(
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxWidth()
                        .background(backgroundCard),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ){
                        Text("E-wallet", color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Image(painter = painterResource(R.drawable.gpay), contentDescription = "GPay", modifier = Modifier.size(iconSize))
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(painter = painterResource(R.drawable.paytm), contentDescription = "Paytm", modifier = Modifier.size(iconSize))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Image(painter = painterResource(R.drawable.ppay), contentDescription = "PhonePe", modifier = Modifier.size(iconSize))
                    }
                }
            }

            // Bonus Column
            Card(
                modifier = Modifier.weight(1f)
                    .background(backgroundCard)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, Color(0xFF29659A), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF002B63))
            ) {
                Column(
                    modifier = Modifier
                        .background(backgroundCard)
                        .padding(horizontal = 14.dp, vertical = 16.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Text("Bonus",
                        color = Color.Yellow,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("30,000 - 60,000  →  1%", color = Color.White, fontSize = 12.sp)
                    Text("5,000 - 25,000  →  2%", color = Color.White, fontSize = 12.sp)
                    Text("100 - 1,000     →  5%", color = Color.White, fontSize = 12.sp)
                }
            }
        }
        DepositAmountSection(
            onDepositClick = { amount ->
                // Handle deposit logic here
                Toast.makeText(context, "Depositing ₹$amount", Toast.LENGTH_SHORT).show()
            }
        )

        RechargeInstructionsSection()

    }
}

fun Int.toShortAmount(): String {
    return when {
        this >= 100000 -> "${this / 1000}K"
        this >= 1000 -> "${this / 1000}K"
        else -> this.toString()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DepositAmountSection(
    onDepositClick: (Int) -> Unit = {}
) {
    val predefinedAmounts = listOf(100, 500, 1000, 5000, 10000, 20000, 50000, 100000)
    var customAmount by remember { mutableStateOf("") }
    val isValidAmount = customAmount.toIntOrNull()?.let { it >= 100 } ?: false
    val errorText = if (customAmount.isNotEmpty() && !isValidAmount) {
        "Amount is required and must be 100 or more!"
    } else null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Deposit Amount",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Predefined Amounts
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            predefinedAmounts.forEach { amount ->
                OutlinedButton(
                    onClick = {
                        customAmount = amount.toString()
                    },
                    shape = RoundedCornerShape(22),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF007BFF)
                    ),
                    border = BorderStroke(1.dp, Color(0xdd007BFF)),
                    modifier = Modifier.size(width = 85.dp, height = 34.dp)
                ) {
                    Text("₹${amount.toShortAmount()}",
                        lineHeight = 18.sp,
                        fontSize = 17.sp,
                        )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Custom Amount Input
        OutlinedTextField(
            value = customAmount,
            onValueChange = { input ->
                if (input.length <= 7) customAmount = input.filter { it.isDigit() }
            },
            placeholder = { Text("Please enter the amount") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50)),
            singleLine = true,
            shape = RoundedCornerShape(50),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF007BFF),
                unfocusedBorderColor = Color(0xFFBBBBBB),
                cursorColor = Color(0xFF007BFF)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error Message
        if (errorText != null) {
            Text(
                text = errorText,
                color = Color(0xFF007BFF),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Deposit Button
        Button(
            onClick = { onDepositClick(customAmount.toInt()) },
            enabled = isValidAmount,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isValidAmount) Color(0xFF007BFF) else Color.Gray,
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Deposit", fontWeight = FontWeight.Bold)
        }
    }
}


////////////////////

@Composable
fun RechargeInstructionsSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Recharge Instructions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            val instructions = listOf(
                "If the transfer time is up, please fill out the deposit form again.",
                "The transfer amount must match the order you created, otherwise the money cannot be credited successfully.",
                "If you transfer the wrong amount, our company will not be responsible for the lost amount!",
                "Note: Do not cancel the deposit order after the money has been transferred."
            )

            instructions.forEach { instruction ->
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("• ", color = Color.Gray)
                    Text(
                        text = instruction,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun deppre(){
    val navController = rememberNavController()
//    DepositScreen(
//        onBackClick = { navController.popBackStack() },
//        onHistoryClick = { navController.navigate("depositHistory") }
//    )

}