package com.weblite.kgf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weblite.kgf.ui.screens.game.screens.K3Ui30
import com.weblite.kgf.ui.screens.game.screens.K3Ui60
import com.weblite.kgf.ui.screens.game.screens.TigerAndDragonGameScreen
import com.weblite.kgf.ui.screens.game.screens.Wingo30Screen
import com.weblite.kgf.ui.screens.game.screens.Wingo60Screen
import com.weblite.kgf.ui.screens.dashboard.HomeMainConten
import com.weblite.kgf.Api2.MainViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME
    ) {
        composable(AppRoutes.HOME) {
            LaunchedEffect(Unit) {
                onShowTopBar(true)
                onShowBottomBar(true)
            }
            HomeMainConten(
                navController = navController,
                onNavigateToWallet = onNavigateToWallet,
                onNavigateToProfile = onNavigateToProfile,
            )
        }
        composable(AppRoutes.WINGO_30) {
            Wingo30Screen(
                onBackClick = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }
        composable(AppRoutes.WINGO_60) {
            Wingo60Screen(
                onBackClick = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }
        composable(AppRoutes.K3_30) {
            K3Ui30(
                variant = "30",
                onBack = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }
        composable(AppRoutes.K3_60) {
            K3Ui60(
                variant = "60",
                onBack = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }
        composable(AppRoutes.DRAGON_TIGER) {
            TigerAndDragonGameScreen(
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.AVIATOR) {
            // Add your Aviator screen here if needed
        }
        composable("payment_deposit/{amount}") { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount") ?: "0"
            com.weblite.kgf.ui.screens.payScreens.PaymentDepositScreen(
                amount = "₹ $amount",
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
