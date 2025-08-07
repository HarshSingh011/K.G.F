package com.weblite.kgf.data.remote

import com.weblite.kgf.domain.model.PromotionCommissionDateDetailsResponse
import com.weblite.kgf.domain.model.MyCommissionsResponse
import com.weblite.kgf.domain.model.DirectTeamDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.weblite.kgf.domain.model.PromotionViewResponse

interface PromotionApi {
    @GET("web/Api/promotionView")
    suspend fun getPromotionView(
        @Query("user_id") userId: String
    ): retrofit2.Response<PromotionViewResponse>
    @GET("web/Api/getDepositeUsersByDate")
    suspend fun getDirectTeamDataWithDate(
        @Query("user_id") userId: String,
        @Query("joining_date") joiningDate: String
    ): Response<DirectTeamDataResponse>
    @GET("web/Api/myCommissionDateDetails")
    suspend fun getPromotionCommissionDateDetails(
        @Query("user_id") userId: String,
        @Query("date") date: String
    ): Response<PromotionCommissionDateDetailsResponse>

    @GET("web/Api/myCommissions")
    suspend fun getMyCommissions(
        @Query("user_id") userId: String
    ): Response<MyCommissionsResponse>

    @GET("web/Api/getUserDataBySession")
    suspend fun getDirectTeamData(
        @Query("user_id") userId: String
    ): Response<DirectTeamDataResponse>
}
