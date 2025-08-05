package com.weblite.kgf.domain.model

data class PromotionCommissionDateDetailsResponse(
    val status: String,
    val status_code: Int,
    val msg: String,
    val result: PromotionCommissionDateDetailsResult
)

data class PromotionCommissionDateDetailsResult(
    val date: String,
    val myCommissions: List<PromotionCommissionDetail>
)

data class PromotionCommissionDetail(
    val id: String,
    val commission_amount: String,
    val receiver_users_table_id: String,
    val giver_users_table_id: String,
    val is_added_to_wallet: String,
    val created_at: String,
    val giver_user_id: String
)
