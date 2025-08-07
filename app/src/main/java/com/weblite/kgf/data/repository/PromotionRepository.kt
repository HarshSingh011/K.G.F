package com.weblite.kgf.data.repository

import com.weblite.kgf.domain.model.PromotionCommissionDateDetailsResponse
import com.weblite.kgf.domain.model.MyCommissionsResponse
import com.weblite.kgf.domain.model.DirectTeamDataResponse

interface PromotionRepository {
    suspend fun getPromotionView(userId: String): Result<com.weblite.kgf.domain.model.PromotionViewResponse>
    suspend fun getDirectTeamDataWithDate(userId: String, joiningDate: String): Result<DirectTeamDataResponse>
    suspend fun getPromotionCommissionDateDetails(userId: String, date: String): Result<PromotionCommissionDateDetailsResponse>

    suspend fun getMyCommissions(userId: String): Result<MyCommissionsResponse>

    suspend fun getDirectTeamData(userId: String): Result<DirectTeamDataResponse>
}
