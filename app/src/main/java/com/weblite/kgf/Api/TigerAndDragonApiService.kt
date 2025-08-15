package com.weblite.kgf.Api

import com.weblite.kgf.data.models.games.DragonTigerBetRequest
import com.weblite.kgf.data.models.games.DragonTigerBetResponse
import com.weblite.kgf.data.models.games.DragonTigerWinResultResponse
import com.weblite.kgf.data.models.games.TigerPeriodIdResponse
import com.weblite.kgf.data.models.games.TigerGameHistoryResponse
import com.weblite.kgf.data.models.games.MyHistoryApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TigerAndDragonApiService {
    @GET("web/Api/dragontigerWinResult")
    suspend fun getDragonTigerWinResult(): DragonTigerWinResultResponse

    @GET("web/Api/dragonTigerPeriodID")
    suspend fun getTigerPeriodId(@Query("user_id") userId: String): TigerPeriodIdResponse

    // Tiger and Dragon Game History API (restore to previous endpoint and params)
    @GET("web/Api/dragonTigerGameHistory")
    suspend fun getTigerGameHistory(@Query("user_id") userId: String): TigerGameHistoryResponse

    // New: Tiger and Dragon Bet API
    @POST("web/Api/dragonTigerBet")
    suspend fun placeDragonTigerBet(@Body request: DragonTigerBetRequest): DragonTigerBetResponse

    // My History API
    @GET("web/Api/dragontigerMyHistory")
    suspend fun getMyHistory(@Query("user_id") userId: String): MyHistoryApiResponse
}