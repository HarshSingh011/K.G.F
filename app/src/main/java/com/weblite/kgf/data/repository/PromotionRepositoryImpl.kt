package com.weblite.kgf.data.repository

import com.weblite.kgf.data.remote.PromotionApi
import com.weblite.kgf.domain.model.PromotionCommissionDateDetailsResponse
import com.weblite.kgf.domain.model.MyCommissionsResponse
import com.weblite.kgf.domain.model.DirectTeamDataResponse
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

    override suspend fun getMyCommissions(userId: String): Result<MyCommissionsResponse> {
        return try {
            val response = api.getMyCommissions(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDirectTeamData(userId: String): Result<DirectTeamDataResponse> {
        return try {
            val response = api.getDirectTeamData(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDirectTeamDataWithDate(userId: String, joiningDate: String): Result<DirectTeamDataResponse> {
        return try {
            val response = api.getDirectTeamDataWithDate(userId, joiningDate)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPromotionView(userId: String): Result<com.weblite.kgf.domain.model.PromotionViewResponse> {
        return try {
            val response = api.getPromotionView(userId)
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
