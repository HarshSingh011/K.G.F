package com.weblite.kgf.data.repository

import com.weblite.kgf.data.remote.PromotionApi
import com.weblite.kgf.domain.model.PromotionCommissionDateDetailsResponse
import javax.inject.Inject

class PromotionRepositoryImpl @Inject constructor(
    private val api: PromotionApi
) : PromotionRepository {
    override suspend fun getPromotionCommissionDateDetails(userId: String, date: String): Result<PromotionCommissionDateDetailsResponse> {
        return try {
            val response = api.getPromotionCommissionDateDetails(userId, date)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
