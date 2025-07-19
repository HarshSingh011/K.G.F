package com.weblite.kgf.data.K360DataClassses


import com.google.gson.annotations.SerializedName

// Data class for K3 Period ID API response
data class K3PeriodIdResponse(
    val status: String,
    @SerializedName("status_code") val statusCode: Int,
    val msg: String,
    val result: K3PeriodIdResult
)

data class K3PeriodIdResult(
    val datetime: String, // This will be the period ID
    val time: Int,       // This will be the initial time remaining in seconds
    @SerializedName("current_dt") val currentDt: String // Current datetime from server
)

// Data classes for K3 Betting
data class GameBetK3(
    val agree: String,
    val bidNum: String,
    val bidType: String,
    val boost: String,
    val period: String,
    val price: String,
    val quantity: String,
    val userid: String
)

data class GameK3BettingResponse(
    val msg: String,
    val result: BetResultK3,
    val status: String,
    val status_code: Int
)

data class BetResultK3(
    val bidNum: String,
    val bidType: String,
    val created_at: String,
    val id: Int,
    val period: String,
    val price: String,
    val quantity: String,
    val userid: String
)

// Placeholder for K3 Game History and My History if they were to be fetched via API
// You would define these based on actual API responses for K3 history
/*
data class K3GameHistoryResponse(
    // ... define fields based on K3 game history API response
)

data class K3MyHistoryResponse(
    // ... define fields based on K3 my history API response
)
*/
