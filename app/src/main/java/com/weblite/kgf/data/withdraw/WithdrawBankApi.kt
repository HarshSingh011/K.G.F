package com.weblite.kgf.data.withdraw

import retrofit2.http.GET
import retrofit2.http.Query

interface WithdrawBankApi {
    @GET("web/Api/getUserBankDetails")
    suspend fun getBankDetails(
        @Query("user_id") userId: String
    ): BankDetailsApiResponse
}
