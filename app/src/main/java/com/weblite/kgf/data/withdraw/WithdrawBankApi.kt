package com.weblite.kgf.data.withdraw

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface WithdrawApi {
    @GET("web/Api/getUserBankDetails")
    suspend fun getBankDetails(
        @Query("user_id") userId: String
    ): BankDetailsApiResponse

    @GET("web/Api/getUserUPIDetails")
    suspend fun getUpiDetails(
        @Query("user_id") userId: String
    ): UpiDetailsApiResponse

    @POST("web/Api/userAddBank")
    suspend fun addBankAccount(
        @Body request: AddBankAccountRequest
    ): AddBankAccountResponse
}
