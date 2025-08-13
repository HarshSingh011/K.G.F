
package com.weblite.kgf.data.withdraw


import com.weblite.kgf.data.models.auth.UpiDetailsRequest
import com.weblite.kgf.presentation.withdraw.UpiDetailsResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface WithdrawApi {
    // Bank
    @GET("web/Api/getUserBankDetails")
    suspend fun getBankDetails(
        @Query("user_id") userId: String
    ): BankDetailsApiResponse

    @POST("web/Api/userAddBank")
    suspend fun addBankAccount(
        @Body request: AddBankAccountRequest
    ): AddBankAccountResponse

    @POST("web/Api/updateAccountDetails")
    suspend fun updateAccountDetails(
        @Body request: UpdateAccountDetailsRequest
    ): UpdateAccountDetailsResponse

    // UPI
    @GET("web/Api/getUserUPIDetails")
    suspend fun getUpiDetails(
        @Query("user_id") userId: String
    ): UpiDetailsApiResponse

    @POST("web/Api/userUpiDetails")
    suspend fun userUpiDetails(
        @Body request: UpiDetailsRequest
    ): UpiDetailsResponse

    @POST("web/Api/updateUPIDetails")
    suspend fun updateUpiDetails(
        @Body request: UpdateUpiDetailsRequest
    ): UpdateUpiDetailsResponse

    // Withdraw Request (Bank)
    @POST("web/Api/userWithdrawRequest")
    suspend fun userWithdrawRequestBank(
        @Body request: com.weblite.kgf.data.models.auth.WithdrawRequestBank
    ): com.weblite.kgf.data.models.auth.WithdrawResponse

    // Withdraw Request (UPI)
    @POST("web/Api/userWithdrawRequest")
    suspend fun userWithdrawRequestUpi(
        @Body request: com.weblite.kgf.data.models.auth.WithdrawRequestUpi
    ): com.weblite.kgf.data.models.auth.WithdrawResponse
}
