package com.weblite.kgf.presentation.withdraw

data class UpiDetailsRequest(
    val userName: String,
    val upi_id: String,
    val upiProvider: String,
    val user_id: String
)

data class UpiDetailsResponse(
    val status: String?,
    val status_code: Int?,
    val msg: String?,
    val result: UpiDetailsResult?
)

data class UpiDetailsResult(
    val user_sl: String?,
    val name: String?,
    val upi_id: String?,
    val upi_provider: String?,
    val curr_dt: String?
)
