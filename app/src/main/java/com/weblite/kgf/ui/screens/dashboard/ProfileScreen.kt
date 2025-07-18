package com.weblite.kgf.ui.screens.dashboard

import GiftHistoryItem
import GiftScreen
import android.R.attr.alpha
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weblite.kgf.utils.AssetImage
import com.weblite.kgf.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.weblite.kgf.utils.AssetIcon
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weblite.kgf.data.CustomFont.Companion.AptosFontNormal
import com.weblite.kgf.data.CustomFont.Companion.aptosFontBold
import com.weblite.kgf.ui.screens.auth.ChangePasswordDialog
import com.weblite.kgf.ui.screens.payScreens.DepositScreen
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.core.view.WindowCompat
import com.weblite.kgf.ui.components.ON_DEPOSIT
import com.weblite.kgf.ui.components.ON_DEPO_HIST
import com.weblite.kgf.ui.components.ON_GIFTS
import com.weblite.kgf.ui.components.ON_TRANS_HISTORY
import com.weblite.kgf.ui.components.ON_VIP
import com.weblite.kgf.ui.components.ON_WITHDRAW
import com.weblite.kgf.ui.components.ON_WITHDR_HIST
import com.weblite.kgf.ui.components.PROFILE_MAIN_ROUTE
import com.weblite.kgf.ui.components.WALLET_ROUTE
import com.weblite.kgf.ui.screens.internalScreens.VipScreen
import com.weblite.kgf.ui.screens.payScreens.DepositHistory
import com.weblite.kgf.ui.screens.payScreens.TransactionHistory
import com.weblite.kgf.ui.screens.payScreens.WithdrawHistory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.weblite.kgf.Api2.MainViewModel
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.ui.screens.payScreens.WithdrawScreen

data class HistoryItem(
    val title: String,
    val iconRes: String // Drawable resource ID
)



@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onNavigateToWallet: () -> Unit
) {

    val localNavController = rememberNavController()
    val giftHistory = listOf(
        GiftHistoryItem("COUPON123", "₹50", "2025-06-30", "Success"),
        GiftHistoryItem("GIFT2025", "₹100", "2025-06-29", "Success"),
        GiftHistoryItem("COUPON123", "₹50", "2025-06-30", "Success"),
        GiftHistoryItem("GIFT2025", "₹100", "2025-06-29", "Success"),
        GiftHistoryItem("COUPON123", "₹50", "2025-06-30", "Success"),
        GiftHistoryItem("GIFT2025", "₹100", "2025-06-29", "Success"),
        GiftHistoryItem("COUPON123", "₹50", "2025-06-30", "Success"),
        GiftHistoryItem("GIFT2025", "₹100", "2025-06-29", "Success")

    )
    val context = LocalContext.current
    val window = (context as Activity).window



    NavHost(navController = localNavController, startDestination = PROFILE_MAIN_ROUTE) {
        composable(PROFILE_MAIN_ROUTE) {
            //  Restore bars when returning to profile
            LaunchedEffect(Unit) {
                onShowTopBar(true)
                onShowBottomBar(true)
            }

            ProfileMainContent(
                navController = localNavController
            )
        }
        composable(WALLET_ROUTE) {
            onNavigateToWallet()
        }
        composable(ON_DEPOSIT) {
            DepositScreen(
                balance = "₹ 17,511,164.75",
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) },
                onHistoryClick = {
                    localNavController.navigate(ON_DEPO_HIST)
                }
            )
        }
        composable(ON_WITHDRAW) {
            WithdrawScreen(
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar,
                onHistoryClick = {
                    localNavController.navigate(ON_WITHDR_HIST)
                }
            )
        }
        composable(ON_DEPO_HIST) {
            DepositHistory(
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(ON_WITHDR_HIST) {
            WithdrawHistory(
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(ON_TRANS_HISTORY) {
            TransactionHistory(
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(ON_GIFTS) {
            GiftScreen(
                onBackClick = { localNavController.popBackStack() },
                historyList = giftHistory,
                onApplyCoupon = { code -> println("Applied $code") },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(ON_VIP) {
            VipScreen(
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
            WindowCompat.setDecorFitsSystemWindows(window, false)

        }
    }
}

@Composable
fun ProfileMainContent(navController: NavController,
                       modifier: Modifier = Modifier,
                       viewModel: MainViewModel = hiltViewModel()
) {

    ////////
    val profileState by viewModel.profileState.collectAsState()
    val userId = SharedPrefManager.getString("user_id", "0")

    LaunchedEffect(key1 = userId) {
        if (userId != "0") {
            viewModel.fetchUserProfile(userId.toString())
        }
    }

    // Local variables for ProfileResponse
    var levelUser by remember { mutableStateOf("") }
    var totalBalance by remember { mutableStateOf("") }
    var teamDepositsTotalDate by remember { mutableStateOf("") }
    var teamBettingTotalDate by remember { mutableStateOf("") }
    var allTimeTeamDepositsTotal by remember { mutableStateOf("") }
    var allTimeTeamBettingTotal by remember { mutableStateOf("") }
    var allTimeTeamWithdrawalTotal by remember { mutableStateOf("") }
    var allTimeTeamBalanceTotal by remember { mutableStateOf("") }
    var allTimeTeamProfit by remember { mutableStateOf("") }
    var dayTeamDepositsTotalFmt by remember { mutableStateOf("") }
    var dayTeamBettingTotalFmt by remember { mutableStateOf("") }

    // Local variables for User
    var id by remember { mutableStateOf("") }
    var userIdField by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var joiningDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf("") }
    var wallet by remember { mutableStateOf("") }
    var bonusAmount by remember { mutableStateOf("") }
    var firstDeposite by remember { mutableStateOf("") }
    var withdrawalAmount by remember { mutableStateOf("") }
    var referral by remember { mutableStateOf("") }
    var referralId by remember { mutableStateOf("") }
    var franchise by remember { mutableStateOf("") }
    var rechargeOption by remember { mutableStateOf("") }
    var bonusApplied by remember { mutableStateOf("") }
    var totalIncome by remember { mutableStateOf("") }
    var dStatus by remember { mutableStateOf("") }
    var isSuperUser by remember { mutableStateOf("") }
    var sessionId by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf("") }
    var updatedAt by remember { mutableStateOf("") }
    var lastLogin by remember { mutableStateOf("") }
    var authToken by remember { mutableStateOf("") }

    when {
        profileState?.isSuccess == true -> {
            profileState?.getOrNull()?.let { profile ->
                val user = profile.user

                // ProfileResponse fields
                levelUser = profile.levelUser
                totalBalance = profile.totalBalance
                teamDepositsTotalDate = profile.teamDepositsTotalDate.orEmpty()
                teamBettingTotalDate = profile.teamBettingTotalDate.orEmpty()
                allTimeTeamDepositsTotal = profile.allTimeTeamDepositsTotal.orEmpty()
                allTimeTeamBettingTotal = profile.allTimeTeamBettingTotal.orEmpty()
                allTimeTeamWithdrawalTotal = profile.allTimeTeamWithdrawalTotal.orEmpty()
                allTimeTeamBalanceTotal = profile.allTimeTeamBalanceTotal.orEmpty()
                allTimeTeamProfit = profile.allTimeTeamProfit?.toString().orEmpty()
                dayTeamDepositsTotalFmt = profile.dayTeamDepositsTotalFmt.orEmpty()
                dayTeamBettingTotalFmt = profile.dayTeamBettingTotalFmt.orEmpty()

                // User fields
                id = user.id.orEmpty()
                userIdField = user.userId.orEmpty()
                name = user.name.orEmpty()
                email = user.email.orEmpty()
                mobile = user.mobile.orEmpty()
                otp = user.otp.orEmpty()
                city = user.city.orEmpty()
                state = user.state.orEmpty()
                country = user.country.orEmpty()
                pass = user.pass.orEmpty()
                status = user.status.orEmpty()
                joiningDate = user.joiningDate.orEmpty()
                gender = user.gender.orEmpty()
                pin = user.pin.orEmpty()
                address = user.address.orEmpty()
                photo = user.photo.orEmpty()
                wallet = user.wallet.orEmpty()
                bonusAmount = user.bonusAmount.orEmpty()
                firstDeposite = user.firstDeposite.orEmpty()
                withdrawalAmount = user.withdrawalAmount.orEmpty()
                referral = user.referral.orEmpty()
                referralId = user.referralId.orEmpty()
                franchise = user.franchise.orEmpty()
                rechargeOption = user.rechargeOption.orEmpty()
                bonusApplied = user.bonusApplied.orEmpty()
                totalIncome = user.totalIncome.orEmpty()
                dStatus = user.dStatus.orEmpty()
                isSuperUser = user.isSuperUser.orEmpty()
                sessionId = user.sessionId.orEmpty()
                createdAt = user.createdAt.orEmpty()
                updatedAt = user.updatedAt.orEmpty()
                lastLogin = user.lastLogin.orEmpty()
                authToken = user.authToken.orEmpty()
            }
        }

        profileState?.isFailure == true -> {
            val errorMessage = profileState?.exceptionOrNull()?.message.orEmpty()
            Text("Failed to load profile: $errorMessage", color = Color.Red)
        }

        else -> {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
    }


    val lastFourDigits = mobile.takeLast(4)

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val historyItems = listOf(
        HistoryItem("Deposit History", "icons/depo_history.png"),
        HistoryItem("Withdraw History", "icons/withHistory.png"),
        HistoryItem("Transaction History", "icons/transaction-history.png"),
        HistoryItem("Gifts", "icons/GiftsIC.png")
    )

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onSubmit = { _, _ ->
                showChangePasswordDialog = false
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Password changed!")
                }
            }
        )
    }

    Scaffold(scaffoldState = scaffoldState) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF002051))
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp)
                .padding(it)
        ) {
            Box {
                HalfBoxWithBottomRoundedCorners()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 12.dp, start = 36.dp, end = 16.dp)
                ) {
                    UserProfileCard(
                        uid = userIdField,
                        mobile = "XXXXXX$lastFourDigits",
                        level = levelUser,
                        lastLogin = lastLogin,
                        profileImage = painterResource(id = R.drawable.userpro)
                    )
                }
            }


            BalanceCard(
                balance = "₹ $totalBalance",
                onRefresh = {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("Balance refreshed")
                    }
                },
                onWalletClick = {
                    navController.navigate(WALLET_ROUTE)
                },
                onDepositClick = {
                    navController.navigate(ON_DEPOSIT)
                },
                onWithdrawClick = {
                    navController.navigate(ON_WITHDRAW)
                },
                onVipClick = {
                    navController.navigate(ON_VIP)
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("VIP")
                    }
                }
            )

            SafeIncomeCard()
            HistoryGrid(
                navController = navController,
                items = historyItems,
                onItemClick = { item: HistoryItem ->
                    when (item.title) {
                        "Deposit History" -> navController.navigate(ON_DEPO_HIST)
                        "Withdraw History" -> navController.navigate(ON_WITHDR_HIST)
                        "Transaction History" -> navController.navigate(ON_TRANS_HISTORY)
                        "Gifts" -> navController.navigate(ON_GIFTS)
                        else -> {
                            Toast.makeText(context, "Something went Wrong!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )

            UserDetailsScreen(
                onSupportClick = { println("Support clicked") },
                onNotificationClick = { println("Notifications clicked") },
                onChangePasswordClick = { showChangePasswordDialog = true }
            )
            LogoutButton { /* act */ }
        }
    }
}



///////////
// Custom shape that creates a half-circle cut from the top
val BottomRoundedShape = GenericShape { size, _ ->
    val cornerRadius = size.height * 0.7f // Adjust for roundness

    moveTo(0f, 0f) // Top-left (sharp)
    lineTo(0f, size.height - cornerRadius) // Left edge

    // Bottom-left rounded corner (quarter-circle)
    quadraticBezierTo(
        0f, size.height,
        cornerRadius, size.height
    )

    lineTo(size.width - cornerRadius, size.height) // Bottom edge

    // Bottom-right rounded corner (quarter-circle)
    quadraticBezierTo(
        size.width, size.height,
        size.width, size.height - cornerRadius
    )

    lineTo(size.width, 0f) // Top-right (sharp)
    close()
}

@Composable
fun HalfBoxWithBottomRoundedCorners() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(brush = Brush.horizontalGradient(
                colors = listOf(Color(0xff182548), Color(0xff2293E3), Color(0xff02F9FE))
            ),
                shape = BottomRoundedShape)
            .border(2.dp, Color(0xFF0099FF), shape = BottomRoundedShape)
            .shadow(
                elevation = 4.dp,
                shape = BottomRoundedShape,
                ambientColor = Color(0xff00EBEF),
                spotColor = Color(0xff00EBEF)
            )
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp).padding(2.dp)
                .background(brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xff182548), Color(0xff2293E3), Color(0xff02F9FE))
                ),
                    shape = BottomRoundedShape)
                .border(2.dp, Color(0xFF000000), shape = BottomRoundedShape)
                .shadow(
                    elevation = 2.dp,
                    shape = BottomRoundedShape,
                    ambientColor = Color(0xff00EBEF),
                    spotColor = Color(0xff00EBEF)
                )
        )
    }
}

@Composable
fun UserProfileCard(
    uid: String = "6763043294",
    mobile: String = "XXXXXX9215",
    level: String = "L0",
    lastLogin: String = "2025-06-19 11:36:44",
    profileImage: Painter
) {
    Box(
        modifier = Modifier
            .background(Color(0x00000000),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = profileImage,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                InfoBadge(label = "UID", value = uid)
                InfoBadge(label = "MOB", value = mobile)
                InfoBadge(label = "Level", value = level)
                Text(
                    text = "Last login: $lastLogin",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun InfoBadge(label: String, value: String) {
    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Text(
            text = "$label | $value",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
///////////

@Composable
fun BalanceCard(
    balance: String = "₹ 17,622,175",
    onRefresh: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onDepositClick: () -> Unit = {},
    onWithdrawClick: () -> Unit = {},
    onVipClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 18.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xFF00EBEF),
                spotColor = Color(0xFF00EBEF)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(2.dp, Color(0xFF0099FF), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Total Balance",
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            fontFamily = AptosFontNormal,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(1.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            Text(
                text = balance,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = AptosFontNormal,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onRefresh) {
                Icon(
                    painter = painterResource(R.drawable.repeat),
                    contentDescription = "Refresh",
                    tint = Color.Gray,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(1.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            BalanceActionItem(
                icon = "icons/wallet-48.png",
                label = "Wallet",
                onClick = onWalletClick
            )
            BalanceActionItem(
                icon = "icons/deposit.png",
                label = "Deposit",
                onClick = onDepositClick
            )
            BalanceActionItem(
                icon = "icons/withdraw.webp",
                label = "Withdraw",
                onClick = onWithdrawClick
            )
            BalanceActionItem(
                icon = "icons/vip.png",
                label = "VIP",
                onClick = onVipClick
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
    }
}


@Composable
fun BalanceActionItem(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable { onClick() } // <-- Click handler here
    ) {
        AssetIcon(
            assetPath = icon,
            contentDescription = "Icon",
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontFamily = AptosFontNormal,
            fontSize = 15.sp,
            color = Color.Black
        )
    }
}

// safe
@Composable
fun SafeIncomeCard(
    title: String = "Safe",
    description: String = "Daily interest rate 0.1% + VIP extra income safe, calculated every 1 minute",
    amount: String = "₹0.00"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFE6CC)) // Light peach background
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = description,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.9f)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
            ){
                Text(
                    text = amount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier
                )
            }

        }
    }
}
// History grid

@Composable
fun HistoryGrid(
    navController: NavController,
    modifier: Modifier = Modifier,
    items: List<HistoryItem>,
    onItemClick: (HistoryItem) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0x003366))
            .padding(vertical = 16.dp)
    ) {
        val chunkedItems = items.chunked(2)
        val itemSpacing = 16.dp
        val horizontalPadding = 16.dp

        chunkedItems.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HistoryGridItem(
                    item = rowItems[0],
                    modifier = Modifier.weight(1f),
                    onClick = onItemClick
                )

                if (rowItems.size > 1) {
                    Spacer(modifier = Modifier.width(itemSpacing))
                    HistoryGridItem(
                        item = rowItems[1],
                        modifier = Modifier.weight(1f),
                        onClick = onItemClick
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HistoryGridItem(
    item: HistoryItem,
    modifier: Modifier = Modifier,
    onClick: (HistoryItem) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(item) }
            .padding(8.dp)
    ) {
        AssetIcon(
            assetPath = item.iconRes,
            contentDescription = "Icon",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
///

@Composable
fun UserDetailsScreen(
    onSupportClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {}
) {
    val context = LocalContext.current

    var teamDepositDate by remember { mutableStateOf("19/06/2025") }
    var teamBettingDate by remember { mutableStateOf("19/06/2025") }

    // Main Card
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF00EBEF),
                spotColor = Color(0xFF00EBEF)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User Details",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("₹0.00", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("Team Deposits on", fontSize = 14.sp, color = Color.Gray)

        DateSelector(
            date = teamDepositDate,
            onDateSelected = { teamDepositDate = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("₹0.00", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("Team Betting on", fontSize = 14.sp, color = Color.Gray)

        DateSelector(
            date = teamBettingDate,
            onDateSelected = { teamBettingDate = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoRow("₹2,312,000.00", "All-Time Team Deposits")
        InfoRow("₹1,529,910.00", "All-Time Team Betting")
        InfoRow("₹314,500.00", "All-Time Team Withdrawal")
        InfoRow("₹1,709,710.17", "All-Time Team Balance")
        InfoRow("₹287,789.83", "All-Time Team Profit/Loss", color = Color(0xFF28A745))

        Spacer(modifier = Modifier.height(20.dp))

        ActionIconText(
            iconRes = R.drawable.support,
            label = "24/7 Customer Support",
            onClick = onSupportClick
        )
        ActionIconText(
            iconRes = R.drawable.ic_notification,
            label = "Notification",
            onClick = onNotificationClick
        )
        ActionIconText(
            iconRes = R.drawable.ic_info,
            label = "Change Password",
            onClick = onChangePasswordClick
        )
    }
}

@Composable
fun DateSelector(
    date: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = {
            // Extract year, month, day from the string
            val parts = date.split("/")
            val day = parts.getOrNull(0)?.toIntOrNull() ?: 1
            val month = parts.getOrNull(1)?.toIntOrNull()?.minus(1) ?: 0 // Calendar months are 0-based
            val year = parts.getOrNull(2)?.toIntOrNull() ?: 2025

            val datePickerDialog = DatePickerDialog(
                context,
                { _: DatePicker, y: Int, m: Int, d: Int ->
                    val newDate = "%02d/%02d/%04d".format(d, m + 1, y)
                    onDateSelected(newDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        },
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .padding(vertical = 4.dp)
    ) {
        Text(text = date)
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Date")
    }
}

@Composable
fun InfoRow(amount: String, label: String, color: Color = Color.Black) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(amount, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = color)
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ActionIconText(iconRes: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.Black)
    }
}
///
@Composable
fun LogoutButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Button(
        onClick = {
            SharedPrefManager.clear()
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            onClick()
            val intent = (context as Activity).intent
            context.finish()
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF2b2a),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .height(54.dp)
            .border(
                width = 2.dp,
                color = Color(0xff043463),
                shape = RoundedCornerShape(12.dp)
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xFF00EBEF),
                spotColor = Color(0xFF00EBEF)
            ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp
        )
    ) {
        Text(
            text = "Logout",
            fontWeight = FontWeight.Medium,
            fontFamily = aptosFontBold,
            fontSize = 22.sp,
            color = Color.White
        )
    }
}


@Preview
@Composable
fun ProPreview(){
//    val painter = painterResource(id = R.drawable.userpro)
//    UserProfileCard(profileImage = painter)
    //ProfileScreen()
    //UserDetailsScreen()
    LogoutButton{

    }

}
