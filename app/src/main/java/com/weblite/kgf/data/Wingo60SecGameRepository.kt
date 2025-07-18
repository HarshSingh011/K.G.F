package com.weblite.kgf.data

import com.weblite.kgf.Api2.ApiService
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
    // Add other 60-sec specific API calls here if needed in the future

    suspend fun getWingo60SecGameHistory(): Response<GameHistory60SecResponse> {
        return apiService.getWingo60SecGameHistory()
    }

    suspend fun getWingo60SecMyHistory(userId: String): Response<Wingo60SecMyHistoryResponse> {
        return apiService.getWingo60SecMyHistory(userId)
    }
}
