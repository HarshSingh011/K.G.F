package com.weblite.kgf.data.repository

import com.weblite.kgf.domain.model.PromotionCommissionDateDetailsResponse

interface PromotionRepository {
    suspend fun getPromotionCommissionDateDetails(userId: String, date: String): Result<PromotionCommissionDateDetailsResponse>
}
