package com.weblite.kgf.data

import com.weblite.kgf.Api2.ApiService
import com.weblite.kgf.data.K360DataClassses.GameBetK3
import com.weblite.kgf.data.K360DataClassses.GameK3BettingResponse
import com.weblite.kgf.data.K360DataClassses.K3PeriodIdResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class K3GameRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getK3OneMinPeriodID(userId: String?): Result<K3PeriodIdResponse> {
        return try {
            val response = apiService.getK3OneMinPeriodID(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun placeK3Bet(
        userid: String,
        bidNum: String,
        bidType: String,
        period: String,
        quantity: String,
        price: String,
        agree: String = "on",
        boost: String = "txt"
    ): Response<GameK3BettingResponse> {
        val request = GameBetK3(
            userid = userid,
            bidNum = bidNum,
            bidType = bidType,
            period = period,
            quantity = quantity,
            price = price,
            agree = agree,
            boost = boost
        )
        return apiService.placeK3Bet(request)
    }

    // Add suspend functions for K3 game history and my history here if needed
    /*
    suspend fun getK3GameHistory(): Response<K3GameHistoryResponse> {
        // return apiService.getK3GameHistory()
        TODO("Implement K3 game history API call")
    }

    suspend fun getK3MyHistory(userId: String): Response<K3MyHistoryResponse> {
        // return apiService.getK3MyHistory(userId)
        TODO("Implement K3 my history API call")
    }
    */
}
