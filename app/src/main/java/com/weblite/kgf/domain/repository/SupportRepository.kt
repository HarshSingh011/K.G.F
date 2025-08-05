package com.weblite.kgf.domain.repository

import com.weblite.kgf.domain.model.SupportMessage

interface SupportRepository {
    suspend fun sendMessage(userId: String, message: String): Result<SupportMessage>

    suspend fun getCustomerMessages(userId: String): Result<List<SupportMessage>>
}
