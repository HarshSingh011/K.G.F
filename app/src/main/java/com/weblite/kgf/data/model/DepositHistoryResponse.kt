package com.weblite.kgf.data.model

data class DepositHistoryResponse(
    val status: String,
    val status_code: Int,
    val msg: String,
    val result: List<DepositHistoryItem>
)

data class DepositHistoryItem(
    val recharge_sl: String?,
    val user_id: String?,
    val order_no: String?,
    val user_mobile: String?,
    val user_name: String?,
    val pay_amount: String?,
    val txn_id: String?,
    val step_2: String?,
    val payment_mode: String?,
    val pay_id: String?,
    val status: String?,
    val reason: String?,
    val rejected_at: String?,
    val update_date: String?,
    val post_date: String?,
    val file: String?,
    val bonus_amount: String?,
    val total_amount: String?,
    val approved: String?,
    val commission: String?
)
