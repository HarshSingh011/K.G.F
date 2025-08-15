package com.weblite.kgf.data.repository.GameRespsitories

import com.weblite.kgf.Api.TigerAndDragonApiService
import com.weblite.kgf.data.models.games.TigerPeriodIdResponse
import com.weblite.kgf.data.models.games.TigerGameHistoryResponse
import com.weblite.kgf.data.models.games.DragonTigerBetRequest
import com.weblite.kgf.data.models.games.DragonTigerBetResponse
import com.weblite.kgf.data.models.games.DragonTigerWinResultResponse
import com.weblite.kgf.data.models.games.MyHistoryApiResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TigerAndDragonRepository @Inject constructor(
    private val apiService: TigerAndDragonApiService
) {

    suspend fun fetchDragonTigerWinResult(): Result<DragonTigerWinResultResponse> {
        return try {
            val response = apiService.getDragonTigerWinResult()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTigerPeriodId(userId: String?): Result<TigerPeriodIdResponse> {
        return try {
            val response = apiService.getTigerPeriodId(userId ?: "")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTigerGameHistory(userId: String): TigerGameHistoryResponse {
        return apiService.getTigerGameHistory(userId)
    }

    // New: Place Dragon Tiger Bet
    suspend fun placeDragonTigerBet(request: DragonTigerBetRequest): Result<DragonTigerBetResponse> {
        return try {
            val response = apiService.placeDragonTigerBet(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // My History API
    suspend fun fetchMyHistory(userId: String): Result<MyHistoryApiResponse> {
        return try {
            val response = apiService.getMyHistory(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
