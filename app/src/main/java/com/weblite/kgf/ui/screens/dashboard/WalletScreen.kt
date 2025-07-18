package com.weblite.kgf.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weblite.kgf.Api2.MainViewModel
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.ui.components.AttractiveText
import com.weblite.kgf.ui.components.ON_DEPOSIT
import com.weblite.kgf.ui.components.ON_DEPO_HIST
import com.weblite.kgf.ui.components.ON_WITHDRAW
import com.weblite.kgf.ui.components.ON_WITHDR_HIST
import com.weblite.kgf.ui.components.WALLET_MAIN
import com.weblite.kgf.ui.screens.game.K3Ui30
import com.weblite.kgf.ui.screens.game.K3Ui60
import com.weblite.kgf.ui.screens.payScreens.DepositHistory
import com.weblite.kgf.ui.screens.payScreens.DepositScreen
import com.weblite.kgf.ui.screens.payScreens.WithdrawHistory
import com.weblite.kgf.ui.screens.payScreens.WithdrawScreen
import com.weblite.kgf.utils.AssetIcon

@Composable
fun MainWalletScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
){


    val dashboardState = viewModel.dashboardState.value

    var userMobile by remember { mutableStateOf("") }
    var joiningDate by remember { mutableStateOf("") }
    var userLevel by remember { mutableStateOf("") }
    var totalBalance by remember { mutableStateOf(0) }

    val userid = SharedPrefManager.getString("user_id","0")
    LaunchedEffect(key1 = userid) {
        viewModel.fetchDashboard( userid)
    }

    if (dashboardState is Resource.Success) {
        dashboardState.data?.result?.let { result ->
            userMobile = result.user.mobile.orEmpty()
            joiningDate = result.user.joiningDate.orEmpty()
            userLevel = result.levelUser.orEmpty()
            totalBalance = result.totalBalance
        }

    }


    val localNavController = rememberNavController()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    NavHost(
        navController = localNavController,
        startDestination = WALLET_MAIN,
        modifier = modifier
    ) {
        composable(WALLET_MAIN) {
            LaunchedEffect(Unit) {
                onShowTopBar(true)
                onShowBottomBar(true)
            }

            WalletScreen(
                totalBalance = totalBalance.toString(),
                navController = localNavController,
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }

        composable(ON_DEPOSIT) {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Deposit clicked", Toast.LENGTH_SHORT).show()
            }
            DepositScreen(
                balance = "₹ $totalBalance",
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar,
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

        // And so on...
    }

//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .background(Color(0xFF002051))
//            .verticalScroll(scrollState) //
//            .padding(bottom = 16.dp)
//    ){
//        WalletScreen(
//            navController= localNavController,
//            onBackClick = { localNavController.popBackStack() },
//            onShowTopBar = { onShowTopBar(it) },
//            onShowBottomBar = { onShowBottomBar(it) }
//        )
//    }
}

/////

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    totalBalance: String = "₹18,941,395",
    mainWalletPercent: Int = 0,
    thirdPartyWalletPercent: Int = 0,
    mainWalletAmount: String = "₹0.00",
    thirdPartyWalletAmount: String = "₹0.00",
    onMainWalletTransferClick: () -> Unit = {},
    navController: NavController,
    onBackClick: () -> Unit = {},
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val horizontalPadding = when {
        screenWidth < 640 -> 16.dp
        screenWidth < 1024 -> 32.dp
        else -> 64.dp
    }
    val sectionSpacing = 32.dp

    LaunchedEffect(Unit) {
        onShowTopBar(true)
        onShowBottomBar(true)
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D0F3B), Color(0xFF002051))
                )
            ),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            AttractiveText(
                text = "Wallet",
                primaryColor = Color.White,
                shadowColor = Color(0xFF606060),
                fontSize = 26.sp,
                strokeWidth = 1.5f
            )
            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Wallet Icon",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
//            Text(
//                text = totalBalance,
//                fontSize = 32.sp,
//                fontWeight = FontWeight.ExtraBold,
//                color = Color.White
//            )
            AttractiveText(
                text = "₹$totalBalance",
                primaryColor = Color.White,
                shadowColor = Color(0xFF606060),
                fontSize = 30.sp,
                strokeWidth = 1.5f
            )
            Text(
                text = "Total balance",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 4.dp, bottom = sectionSpacing)
            )

            // Wallet Card
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0xff00EBEF),
                        spotColor = Color(0xff00EBEF)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xff000000), RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xff00ebef), Color(0xff00EBEF))
                        )
                    ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 38.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Wallet progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WalletProgressSection(
                            label = "Main wallet",
                            percent = mainWalletPercent,
                            amount = mainWalletAmount
                        )
                        WalletProgressSection(
                            label = "3rd party wallet",
                            percent = thirdPartyWalletPercent,
                            amount = thirdPartyWalletAmount
                        )
                    }
                    Spacer(Modifier.height(24.dp))

                    // Transfer button
                    Button(
                        onClick = onMainWalletTransferClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                    ) {
                        Text(
                            text = "Main Wallet Transfer",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(32.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WalletActionButton(
                            label = "Deposit",
                            icon = "icons/deposit.png",
                            iconTint = Color(0xFFF44336),
                            onClick = { navController.navigate(ON_DEPOSIT) },
                            contentDescription = "Deposit icon"
                        )
                        WalletActionButton(
                            label = "Withdraw",
                            icon = "icons/withdraw.webp",
                            iconTint = Color(0xFF2196F3),
                            onClick = { navController.navigate(ON_WITHDRAW) },
                            contentDescription = "Withdraw icon"
                        )
                        WalletActionButton(
                            label = "Deposit \nHistory",
                            icon = "icons/depo_history.png",
                            iconTint = Color(0xFF4CAF50),
                            onClick = { navController.navigate(ON_DEPO_HIST) },
                            contentDescription = "Deposit history icon"
                        )
                        WalletActionButton(
                            label = "Withdrawal \nHistory",
                            icon = "icons/withHistory.png",
                            iconTint = Color(0xFFF57C00),
                            onClick = { navController.navigate(ON_WITHDR_HIST) },
                            contentDescription = "Withdrawal history icon"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletProgressSection(
    label: String,
    percent: Int,
    amount: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(140.dp)
    ) {
        CircularProgressIndicator(
            progress = percent / 100f,
            strokeWidth = 8.dp,
            modifier = Modifier.size(80.dp),
            color = Color(0xFF0D47A1),
            trackColor = Color(0xFFE0E0E0)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "$percent%",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.Black
        )
        Text(
            text = amount,
            color = Color.Black.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun WalletActionButton(
    label: String,
    icon: String,
    iconTint: Color,
    onClick: () -> Unit,
    contentDescription: String
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(iconTint.copy(alpha = 0.2f), shape = RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            AssetIcon(
                assetPath = icon, // inside assets/icons/
                contentDescription = "Icon",
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

///


@Preview(showBackground = true)
@Composable
fun WalletPreview() {
    //AgencyCommissionUI()
    //WalletScreen()
}
