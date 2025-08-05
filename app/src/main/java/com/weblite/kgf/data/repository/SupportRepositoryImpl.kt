package com.weblite.kgf.data.repository

import com.weblite.kgf.data.remote.api.SupportApi
import com.weblite.kgf.data.remote.api.SendMessageRequest
import com.weblite.kgf.data.remote.api.CustomerMessageDto
import com.weblite.kgf.data.remote.api.CustomerMessagesResponse
import com.weblite.kgf.domain.model.SupportMessage
import com.weblite.kgf.domain.repository.SupportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class SupportRepositoryImpl(private val api: SupportApi) : SupportRepository {
    override suspend fun sendMessage(userId: String, message: String): Result<SupportMessage> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = SendMessageRequest(user_id = userId, message = message)
            val response = api.sendMessage(request)
            val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            if (response.status) {
                Result.success(
                    SupportMessage(
                        text = response.message,
                        time = time,
                        isUser = false // admin reply
                    )
                )
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCustomerMessages(userId: String): Result<List<SupportMessage>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getCustomerMessages(userId)
            if (response.status_code == 200 && response.status == "success") {
                val messages = response.result.values.sortedBy { it.id.toInt() }.map { dto ->
                    SupportMessage(
                        text = dto.message,
                        time = dto.datetime,
                        isUser = dto.status == "user"
                    )
                }
                Result.success(messages)
            } else {
                Result.failure(Exception(response.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
