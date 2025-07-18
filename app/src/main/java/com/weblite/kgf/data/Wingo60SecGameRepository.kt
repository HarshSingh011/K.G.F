package com.weblite.kgf.data

import com.weblite.kgf.Api2.ApiService
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
}
