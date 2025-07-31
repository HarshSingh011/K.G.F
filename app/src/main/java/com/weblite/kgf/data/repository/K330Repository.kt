package com.weblite.kgf.data.repository

import com.weblite.kgf.Api2.ApiService
import com.weblite.kgf.data.GameBetK360
import com.weblite.kgf.data.K330GameHistoryResponse
import com.weblite.kgf.data.K330MyHistoryResponse
import com.weblite.kgf.data.K360PeriodIdResponse
import com.weblite.kgf.data.GameK360BettingResponse
import com.weblite.kgf.data.models.games.K3PopupHistoryResponse
import retrofit2.Response
import javax.inject.Inject

class K330GameRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getThirtySecondPeriodIDForK3(userId: String): Response<K360PeriodIdResponse> {
        return apiService.getThirtySecondPeriodIDForK3(userId)
    }

    suspend fun placeBet3Sec(request: GameBetK360): Response<GameK360BettingResponse> {
        return apiService.placeBet3Sec(request)
    }

    suspend fun getK330GameHistory(userId: String): Response<K330GameHistoryResponse> {
        return apiService.getK330GameHistory(userId)
    }

    suspend fun getK330MyHistory(userId: String): Response<K330MyHistoryResponse> {
        return apiService.getK330MyHistory(userId)
    }

    suspend fun getK330PopupHistory(userId: String): Result<K3PopupHistoryResponse> {
        return try {
            val response = apiService.getK330PopupHistory(userId)
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
