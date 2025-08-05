package com.weblite.kgf.domain.repository

import com.weblite.kgf.domain.model.RewardHistory
import com.weblite.kgf.domain.model.VipInfo

interface VipRepository {
    suspend fun getVipInfo(userId: String): Result<VipInfo>

    suspend fun getRewardHistory(userId: String): Result<List<RewardHistory>>
}
