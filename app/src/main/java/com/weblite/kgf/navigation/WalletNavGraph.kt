package com.weblite.kgf.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weblite.kgf.navigation.AppRoutes
import com.weblite.kgf.ui.components.ON_DEPOSIT
import com.weblite.kgf.ui.components.ON_DEPO_HIST
import com.weblite.kgf.ui.components.ON_WITHDRAW
import com.weblite.kgf.ui.components.ON_WITHDR_HIST
import com.weblite.kgf.ui.components.WALLET_MAIN
import com.weblite.kgf.ui.screens.payScreens.DepositHistory
import com.weblite.kgf.ui.screens.payScreens.DepositScreen
import com.weblite.kgf.ui.screens.payScreens.PaymentDepositScreen
import com.weblite.kgf.ui.screens.payScreens.WithdrawScreen
import com.weblite.kgf.ui.screens.payScreens.screens.history.WithdrawHistory

@Composable
fun WalletNavGraph(
    totalBalance: String,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
) {
    val localNavController = rememberNavController()
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
                totalBalance = totalBalance,
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
                navController = localNavController,
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
                navController = localNavController,
                onBackClick = { localNavController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
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
        composable("payment_deposit/{amount}") { backStackEntry ->
            val encodedAmount = backStackEntry.arguments?.getString("amount") ?: "0"
            val amount = try {
                java.net.URLDecoder.decode(encodedAmount, "UTF-8")
            } catch (e: Exception) {
                encodedAmount // fallback to original if decoding fails
            }
            PaymentDepositScreen(
                amount = "₹ $amount",
                navController = localNavController,
                onBackClick = { localNavController.popBackStack() }
            )
        }
    }
}
