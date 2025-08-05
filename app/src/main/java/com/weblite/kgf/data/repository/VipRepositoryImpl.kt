package com.weblite.kgf.data.repository

import com.weblite.kgf.data.remote.api.VipApi
import com.weblite.kgf.domain.model.VipInfo
import com.weblite.kgf.domain.model.RewardHistory
import com.weblite.kgf.domain.repository.VipRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VipRepositoryImpl(private val api: VipApi) : VipRepository {
    override suspend fun getVipInfo(userId: String): Result<VipInfo> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getVipInfo(userId)
            val result = response.result
            Result.success(
                VipInfo(
                    daysLeft = result.daysLeft,
                    userId = result.userId,
                    totalExp = result.totalExp,
                    levelUser = result.levelUser
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRewardHistory(userId: String): Result<List<RewardHistory>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getRewardHistory(userId)
            val mapped = response.data.map {
                RewardHistory(
                    userId = it.user_id,
                    level = it.level.toIntOrNull() ?: 0,
                    bonusAmount = it.bonusAmount.toDoubleOrNull() ?: 0.0,
                    type = if (it.type == "LevelUp") "One time reward" else it.type,
                    date = it.claimed_at
                )
            }
            Result.success(mapped)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
