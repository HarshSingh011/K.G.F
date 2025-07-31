package com.weblite.kgf.data.repository

import com.weblite.kgf.Api2.ApiService
import com.weblite.kgf.data.models.games.Game60SecBettingResponse
import com.weblite.kgf.data.models.games.GameBet60Sec
import com.weblite.kgf.data.models.games.GameHistory60SecResponse
import com.weblite.kgf.data.models.games.Wingo60PeriodIdResponse
import com.weblite.kgf.data.models.games.Wingo60SecMyHistoryResponse
import com.weblite.kgf.data.models.games.BettingGameResultResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Wingo60GameRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getSixtySecondPeriodID(userId: String?): Result<Wingo60PeriodIdResponse> {
        return try {
            val response = apiService.getSixtySecondPeriodID(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWingo60SecGameHistory(): Response<GameHistory60SecResponse> {
        return apiService.getWingo60SecGameHistory()
    }

    suspend fun getWingo60SecMyHistory(userId: String): Response<Wingo60SecMyHistoryResponse> {
        return apiService.getWingo60SecMyHistory(userId)
    }

    suspend fun place60SecBet(request: GameBet60Sec): Response<Game60SecBettingResponse> {
        return apiService.place60SecBet(request)
    }

    suspend fun place60SecBet(
        userid: String,
        bidNum: String,
        bidType: String,
        period: String,
        quantity: String,
        price: String,
        agree: String = "on",
        boost: String = "txt"
    ): Response<Game60SecBettingResponse> {
        val request = GameBet60Sec(
            userid = userid,
            bidNum = bidNum,
            bidType = bidType,
            period = period,
            quantity = quantity,
            price = price,
            agree = agree,
            boost = boost
        )
        return apiService.place60SecBet(request)
    }

    // --- Wingo 60 Popup History Function (using same data class as Wingo30) ---
    suspend fun getWingo60SecPopupHistory(userId: String): Result<BettingGameResultResponse> {
        return try {
            val response = apiService.getWingo60SecPopupHistory(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
