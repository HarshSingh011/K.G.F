package com.weblite.kgf.data.withdraw

import retrofit2.http.GET
import retrofit2.http.Query

// Withdraw History API response models

data class WithdrawHistoryResponse(
    val status: String?,
    val status_code: Int?,
    val msg: String?,
    val result: Map<String, WithdrawHistoryItem>?
)

data class WithdrawHistoryItem(
    val id: String?,
    val user_id: String?,
    val amount: String?,
    val payment_method: String?,
    val username: String?,
    val account_no: String?,
    val ifsc_code: String?,
    val upi_id: String?,
    val upi_provider: String?,
    val transaction_id: String?,
    val step_2: String?,
    val created_at: String?,
    val status: String?,
    val rejection_reason: String?,
    val rejected_at: String?
)

interface WithdrawHistoryApi {
    @GET("web/Api/userWithdrawHistory")
    suspend fun getWithdrawHistory(@Query("user_id") userId: String): WithdrawHistoryResponse
}
