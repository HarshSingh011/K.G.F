package com.weblite.kgf.data

import com.weblite.kgf.Api2.ApiService
import com.weblite.kgf.data.K330GameHistoryResponse
import com.weblite.kgf.data.K330MyHistoryResponse
import com.weblite.kgf.data.K360PeriodIdResponse
import com.weblite.kgf.data.GameK360BettingResponse
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
}
