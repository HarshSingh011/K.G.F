package com.weblite.kgf.data.TigerAndDragonDataClasses

import com.google.gson.annotations.SerializedName

// --- Tiger and Dragon Game History API Response ---
data class TigerGameHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: TigerGameHistoryResult?
)

data class TigerGameHistoryResult(
    @SerializedName("history") val history: List<TigerGameHistoryItem>?,
    @SerializedName("latest_id") val latestId: String?
)

data class TigerGameHistoryItem(
    @SerializedName("id") val id: String,
    @SerializedName("datetime") val period: String,
    @SerializedName("current_dt") val currentDt: String,
    @SerializedName("result") val result: String // "Tiger", "Dragon", "Draw", or "Running"
)

data class TigerPeriodIdResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: List<TigerPeriodIdResult>
)

data class TigerPeriodIdResult(
    @SerializedName("datetime") val periodId: String, // Correctly map "datetime" from API to periodId
    @SerializedName("current_dt") val currentTime: String
)

// --- My History API Response (CORRECTED to match actual API structure) ---
data class MyHistoryApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: MyHistoryResult?
)

data class MyHistoryResult(
    @SerializedName("history") val history: List<MyHistoryItem>?
)

data class MyHistoryItem(
    @SerializedName("id") val id: String?,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("period_id") val periodId: String?,
    @SerializedName("coins") val coins: String?,
    @SerializedName("bet_choice") val betChoice: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("result") val result: String?,
    @SerializedName("bet_choice_result") val betChoiceResult: String?,
    @SerializedName("totalamount") val totalAmount: String?
)

// --- UI Mapping Data Class ---
data class GameHistoryItem(
    val period: String,
    val result: String? = null, // For game history tab
    val betOn: Int? = null,     // For my history tab
    val coinType: String? = null,
    val winLossStatus: String? = null,
    val winningAmount: Double? = null
)

// --- New: Tiger and Dragon Bet Request and Response ---
data class DragonTigerBetRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("period_id") val periodId: String,
    @SerializedName("coins") val coins: Int,
    @SerializedName("bet_choice") val betChoice: String
)

data class DragonTigerBetResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: DragonTigerBetResult?
)

data class DragonTigerBetResult(
    @SerializedName("user_id") val userId: String,
    @SerializedName("period_id") val periodId: String,
    @SerializedName("coins") val coins: Int,
    @SerializedName("bet_choice") val betChoice: String
)
