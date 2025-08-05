package com.weblite.kgf.data.remote.vip

data class VipRewardHistoryResponse(
    val success: Boolean,
    val message: String,
    val data: List<VipRewardHistoryItem>
)

data class VipRewardHistoryItem(
    val id: String,
    val user_id: String,
    val level: String,
    val bonusAmount: String,
    val type: String,
    val isClaimed: String,
    val maintainLeveBonusClaimed: String,
    val claimed_at: String
)
