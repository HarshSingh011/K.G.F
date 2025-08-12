package com.weblite.kgf.data.withdraw

import retrofit2.http.GET
import retrofit2.http.Query

interface WithdrawUpiApi {
    @GET("web/Api/getUserUPIDetails")
    suspend fun getUpiDetails(
        @Query("user_id") userId: String
    ): UpiDetailsApiResponse
}
