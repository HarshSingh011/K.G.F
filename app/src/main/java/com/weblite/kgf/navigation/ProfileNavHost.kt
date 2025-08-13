package com.weblite.kgf.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.weblite.kgf.ui.screens.payScreens.DepositScreen
import com.weblite.kgf.ui.screens.payScreens.WithdrawScreen
import com.weblite.kgf.ui.screens.payScreens.DepositHistory
import com.weblite.kgf.ui.screens.payScreens.TransactionHistory
import com.weblite.kgf.ui.screens.internalScreens.VipScreen
import com.weblite.kgf.ui.screens.dashboard.ProfileMainContent
import com.weblite.kgf.ui.components.*
import android.app.Activity
import com.weblite.kgf.ui.screens.internalScreens.Service24O7Screen
import com.weblite.kgf.ui.screens.payScreens.GiftHistoryItem
import com.weblite.kgf.ui.screens.payScreens.GiftScreen
import com.weblite.kgf.ui.screens.payScreens.screens.history.WithdrawHistory

const val SERVICE_24_7_ROUTE = "service_24_7"

@Composable
fun ProfileNavHost(
    navController: NavHostController,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onNavigateToWallet: () -> Unit
) {
    val context = LocalContext.current
    val window = (context as Activity).window
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
    NavHost(navController = navController, startDestination = PROFILE_MAIN_ROUTE) {
        composable(PROFILE_MAIN_ROUTE) {
            LaunchedEffect(Unit) {
                onShowTopBar(true)
                onShowBottomBar(true)
            }
            ProfileMainContent(
                navController = navController
            )
        }
        composable(SERVICE_24_7_ROUTE) {
            Service24O7Screen()
        }
        composable(WALLET_ROUTE) {
            onNavigateToWallet()
        }
        composable(ON_DEPOSIT) {
            DepositScreen(
                balance = "₹ 17,511,164.75",
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) },
                onHistoryClick = {
                    navController.navigate(ON_DEPO_HIST)
                }
            )
        }
        composable(ON_WITHDRAW) {
            WithdrawScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(ON_DEPO_HIST) {
            DepositHistory(
                onBackClick = { navController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(ON_WITHDR_HIST) {
            WithdrawHistory(
                onBackClick = { navController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(ON_TRANS_HISTORY) {
            TransactionHistory(
                onBackClick = { navController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(ON_GIFTS) {
            GiftScreen(
                onBackClick = { navController.popBackStack() },
                historyList = giftHistory,
                onApplyCoupon = { code -> println("Applied $code") },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(ON_VIP) {
            VipScreen(
                onBackClick = { navController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
            //WindowCompat.setDecorFitsSystemWindows(window, false)
        }
        // Add PaymentDepositScreen route for navigation from DepositScreen
        composable("payment_deposit/{amount}") { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount") ?: "0"
            com.weblite.kgf.ui.screens.payScreens.PaymentDepositScreen(
                amount = "₹ $amount",
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
