package com.weblite.kgf.domain.model

data class RewardHistory(
    val userId: String,
    val level: Int,
    val bonusAmount: Double,
    val type: String,
    val date: String
)
