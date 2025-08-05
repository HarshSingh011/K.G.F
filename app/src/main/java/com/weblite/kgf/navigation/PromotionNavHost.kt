package com.weblite.kgf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weblite.kgf.ui.screens.internalScreens.CommissionDetails
import com.weblite.kgf.ui.screens.internalScreens.DirectTeamData
import com.weblite.kgf.ui.screens.internalScreens.InvitationRules
import com.weblite.kgf.ui.screens.internalScreens.RebateRules
import com.weblite.kgf.ui.screens.dashboard.PromotionMainScreen
import com.weblite.kgf.ui.screens.dashboard.DateDetailsScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

const val PROMOTION_MAIN = "MAIN"
const val DIRECT_TEAM = "direct team data"
const val COMMISS_DETAILS = "commission details"
const val INVITAION_RULES = "invitation rules"
const val REBATE_RATIO = "rebate ratio"

@Composable
fun PromotionNavHost(
    navController: NavHostController,
    onShowTopBar: (Boolean) -> Unit,
    onShowBottomBar: (Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = PROMOTION_MAIN) {
        composable(PROMOTION_MAIN) {
            LaunchedEffect(Unit) {
                onShowTopBar(true)
                onShowBottomBar(true)
            }
            PromotionMainScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onShowTopBar = onShowTopBar,
                onShowBottomBar = onShowBottomBar
            )
        }
        composable(DIRECT_TEAM) {
            DirectTeamData(
                onBackClick = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }
        composable(COMMISS_DETAILS) {
            CommissionDetails(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }
        composable(INVITAION_RULES) {
            InvitationRules(
                onBackClick = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }
        composable(REBATE_RATIO) {
            RebateRules(
                onBackClick = { navController.popBackStack() },
                onShowTopBar = { onShowTopBar(it) },
                onShowBottomBar = { onShowBottomBar(it) }
            )
        }
        composable(
            route = "commission_details/{date}",
            arguments = listOf(
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            DateDetailsScreen(date = date)
        }
    }
}
