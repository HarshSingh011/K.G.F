package com.weblite.kgf.domain.model

// Root response for /web/Api/myCommissionDateDetails
// { status, status_code, msg, result: { date, myCommissions: [...] } }
data class MyCommissionDateDetailsResponse(
    val status: String,
    val status_code: Int,
    val msg: String,
    val result: MyCommissionDateDetailsResult
)

data class MyCommissionDateDetailsResult(
    val date: String,
    val myCommissions: List<MyCommissionDetail>
)

data class MyCommissionDetail(
    val id: String,
    val commission_amount: String,
    val receiver_users_table_id: String,
    val giver_users_table_id: String,
    val is_added_to_wallet: String,
    val created_at: String,
    val giver_user_id: String
)
