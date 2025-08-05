package com.weblite.kgf.data.remote.api

// Data class for request
data class SendMessageRequest(
    val user_id: String,
    val message: String
)

// Data class for response
data class SendMessageResponse(
    val status: Boolean,
    val message: String
)

// Data class for customer messages response
data class CustomerMessagesResponse(
    val status: String,
    val status_code: Int,
    val msg: String,
    val result: Map<String, CustomerMessageDto>
)

data class CustomerMessageDto(
    val id: String,
    val user_id: String,
    val message: String,
    val datetime: String,
    val status: String, // "user" or "admin"
    val user_status: String,
    val is_read: String
)
