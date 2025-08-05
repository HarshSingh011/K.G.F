package com.weblite.kgf.data.remote

import com.weblite.kgf.domain.model.PromotionCommissionDateDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PromotionApi {
    @GET("web/Api/myCommissionDateDetails")
    suspend fun getPromotionCommissionDateDetails(
        @Query("user_id") userId: String,
        @Query("date") date: String
    ): Response<PromotionCommissionDateDetailsResponse>
}
