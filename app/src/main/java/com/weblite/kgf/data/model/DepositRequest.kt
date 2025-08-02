package com.weblite.kgf.data.model

data class DepositRequest(
    val amount: Int,
    val utr_no: String,
    val mobile: String
)

data class DepositResponse(
    val status: String,
    val status_code: Int,
    val msg: String,
    val result: DepositResult?
)

data class DepositResult(
    val user_id: String?,
    val order_no: String?,
    val user_mobile: String?,
    val pay_amount: Int?,
    val bonus_amount: Int?,
    val txn_id: String?,
    val step_2: String?,
    val post_date: String?,
    val id: Int?
)
