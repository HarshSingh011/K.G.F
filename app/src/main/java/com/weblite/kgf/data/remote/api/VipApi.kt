package com.weblite.kgf.data.remote.api

import com.weblite.kgf.data.remote.vip.VipApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VipApi {
    @GET("web/Api/vip")
    suspend fun getVipInfo(
        @Query("user_id") userId: String
    ): VipApiResponse

    @GET("web/Api/reward_history")
    suspend fun getRewardHistory(
        @Query("user_id") userId: String
    ): com.weblite.kgf.data.remote.vip.VipRewardHistoryResponse
}
