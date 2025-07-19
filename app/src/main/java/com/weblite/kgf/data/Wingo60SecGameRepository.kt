package com.weblite.kgf.data

import com.weblite.kgf.Api2.ApiService
import com.weblite.kgf.Api2.BetRequest
import com.weblite.kgf.Api2.BetResponse
import com.weblite.kgf.data.Wingo60SecDataClasses.Game60SecBettingResponse
import com.weblite.kgf.data.Wingo60SecDataClasses.GameBet60Sec
import com.weblite.kgf.data.Wingo60SecDataClasses.GameHistory60SecResponse
import com.weblite.kgf.data.Wingo60SecDataClasses.Wingo60PeriodIdResponse
import com.weblite.kgf.data.Wingo60SecDataClasses.Wingo60SecMyHistoryResponse
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
}
