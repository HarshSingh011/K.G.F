package com.weblite.kgf.navigation

import androidx.navigation.NavController

// Route name constants
object AppRoutes {
    const val HOME = "home"
    const val WINGO_30 = "wingo_30"
    const val WINGO_60 = "wingo_60"
    const val K3_30 = "k3_30"
    const val K3_60 = "k3_60"
    const val DRAGON_TIGER = "dragon_tiger"
    const val AVIATOR = "aviator"
    const val PAYMENT_DEPOSIT = "payment_deposit"
    // Add more as needed
}

// Navigation helper functions
fun navigateToWingo30(navController: NavController) {
    navController.navigate(AppRoutes.WINGO_30)
}

fun navigateToWingo60(navController: NavController) {
    navController.navigate(AppRoutes.WINGO_60)
}

fun navigateToK3_30(navController: NavController) {
    navController.navigate(AppRoutes.K3_30)
}

fun navigateToK3_60(navController: NavController) {
    navController.navigate(AppRoutes.K3_60)
}

fun navigateToDragonTiger(navController: NavController) {
    navController.navigate(AppRoutes.DRAGON_TIGER)
}

fun navigateToAviator(navController: NavController) {
    navController.navigate(AppRoutes.AVIATOR)
}