package com.weblite.kgf.data.Wingo60SecDataClasses

import com.google.gson.annotations.SerializedName

data class Wingo60PeriodIdResponse(
    val status: String,
    @SerializedName("status_code") val statusCode: Int,
    val msg: String,
    val result: List<Wingo60PeriodIdResult>
)

data class Wingo60PeriodIdResult(
    @SerializedName("period_60") val period60: String,
    @SerializedName("update_at") val updateAt: String
)

data class GameHistory60SecResponse(
    val msg: String,
    val result: GameHistory60SecResult,
    val status: String,
    val status_code: Int
)

data class GameHistory60SecResult(
    val history: List<GameHistory60SecHistory>,
    val latest_id: String
)

data class GameHistory60SecHistory(
    val adminWinNum: String,
    val bid_60: String,
    val big_small_result: String,
    val color_result: String,
    val finalBigSmall: String,
    val finalcolor: String,
    val number_result: String,
    val period_60: String,
    val status: String,
    val update_at: String
)

data class Wingo60SecMyHistoryResponse(
    val msg: String,
    val result: Wingo60SecMyHistoryResult,
    val status: String,
    val status_code: Int
)

data class Wingo60SecMyHistoryResult(
    val history: List<Wingo60SecMyHistory>
)

data class Wingo60SecMyHistory(
    val adminBigSmallResult: String,
    val adminColorResult: String,
    val adminNumberResult: String,
    val adminTotal: String,
    val adminWinStatus: String,
    val agree: String,
    val betID: String,
    val bidNum: String,
    val bidType: String,
    val big_small_result: String,
    val boost: String,
    val calculated_totalAmount: String?,
    val color_result: String,
    val created_at: String,
    val dateAdded: String,
    val number_result: String,
    val period: String,
    val price: String,
    val quantity: String,
    val resultStatus: String,
    val total: String,
    val userid: String,
    val winning_amount: String?,
    val winning_amount_bk: String
)

data class GameBet60Sec(
    val agree: String,
    val bidNum: String,
    val bidType: String,
    val boost: String,
    val period: String,
    val price: String,
    val quantity: String,
    val userid: String
)

data class Game60SecBettingResponse(
    val msg: String,
    val result: BetResult60Sec,
    val status: String,
    val status_code: Int
)

data class BetResult60Sec(
    val bidNum: String,
    val bidType: String,
    val created_at: String,
    val id: Int,
    val period: String,
    val price: String,
    val quantity: String,
    val userid: String
)
