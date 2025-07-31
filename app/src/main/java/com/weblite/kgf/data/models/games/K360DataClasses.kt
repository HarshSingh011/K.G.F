package com.weblite.kgf.data

import com.google.gson.annotations.SerializedName

// K3 1-minute game data classes
data class K360PeriodIdResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: K360PeriodIdResult? // Made nullable to handle potential null results
)

data class K360PeriodIdResult(
    @SerializedName("datetime") val periodId: String, // Correctly map "datetime" from API to periodId
    @SerializedName("time") val time: Int?, // Changed to Int? to match API's integer value
    @SerializedName("current_dt") val currentTime: String // This is the server's current timestamp
)

data class GameBetK360(
    @SerializedName("userid") val userid: String,
    @SerializedName("bidNum") val bidNum: String,
    @SerializedName("bidType") val bidType: String,
    @SerializedName("period") val period: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("price") val price: String,
    @SerializedName("agree") val agree: String = "on", // Default to "on"
    @SerializedName("boost") val boost: String = "txt" // Default to "txt"
)

data class GameK360BettingResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: GameK360BettingResult? // Nullable as it might not always be present on error
)

data class GameK360BettingResult(
    @SerializedName("userid") val userid: String,
    @SerializedName("bidNum") val bidNum: String,
    @SerializedName("bidType") val bidType: String,
    @SerializedName("period") val period: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("price") val price: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("id") val id: Int
)

// K3 Game History
data class K360GameHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: K360GameHistoryWrapper // Changed to wrapper object
)

data class K360GameHistoryWrapper( // New data class to match the 'result' object
    @SerializedName("history") val history: List<K360GameHistoryItem>
)

data class K360GameHistoryItem(
    @SerializedName("id") val id: String,
    @SerializedName("datetime") val datetime: String, // Changed from periodId to datetime based on logs
    @SerializedName("bidNum") val number: String, // Changed from number to bidNum based on logs
    @SerializedName("bidOddEven") val oddEven: String, // New field for Odd/Even
    @SerializedName("bidBigSmall") val bigSmall: String, // New field for Big/Small
    @SerializedName("status") val status: String,
    @SerializedName("current_dt") val createdAt: String // Changed from createdAt to current_dt based on logs
)

// K3 My History
data class K360MyHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: K360MyHistoryResult
)

data class K360MyHistoryResult(
    @SerializedName("history") val history: List<K360MyHistoryItem>
)

data class K360MyHistoryItem(
    @SerializedName("k60beatID") val k60beatID: String,
    @SerializedName("bidNum") val bidNum: String,
    @SerializedName("bidType") val bidType: String, // This seems to be the multiplier, e.g., "1.92X"
    @SerializedName("period") val period: String,
    @SerializedName("boost") val boost: String,
    @SerializedName("userid") val userid: String,
    @SerializedName("price") val price: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("status") val status: String, // e.g., "Pending", "Loss", "Win"
    @SerializedName("bet_choice_status") val betChoiceStatus: String,
    @SerializedName("bet_choice_result") val betChoiceResult: String,
    @SerializedName("winning_amount") val winning_amount: String,
    @SerializedName("totalamount") val totalamount: String, // This seems to be the actual win amount
    @SerializedName("adminWinStatus") val adminWinStatus: String,
    @SerializedName("adminWinBid") val adminWinBid: String,
    @SerializedName("adminWinTotal") val adminWinTotal: String,
    @SerializedName("created_at") val createdAt: String
)
