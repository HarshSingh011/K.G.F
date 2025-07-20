package com.weblite.kgf.data

import com.weblite.kgf.Api2.ApiService
import com.weblite.kgf.Api2.Resource
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class K360GameRepository @Inject constructor( // Renamed class
    private val apiService: ApiService
) {
    suspend fun getK360OneMinPeriodID(userId: String?): Response<K360PeriodIdResponse> {
        return apiService.getK3O60neMinPeriodID(userId)
    }

    suspend fun placeK360Bet(
        userid: String,
        bidNum: String,
        bidType: String,
        period: String,
        quantity: String,
        price: String
    ): Response<GameK360BettingResponse> {
        val request = GameBetK360(
            userid = userid,
            bidNum = bidNum,
            bidType = bidType,
            period = period,
            quantity = quantity,
            price = price
        )
        return apiService.placeK360Bet(request)
    }

    suspend fun getK360GameHistory(): Response<K360GameHistoryResponse> {
        return apiService.getK360GameHistory()
    }

    suspend fun getK360MyHistory(userId: String): Response<K360MyHistoryResponse> {
        return apiService.getK360MyHistory(userId)
    }
}
