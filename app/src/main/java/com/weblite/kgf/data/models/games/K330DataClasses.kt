package com.weblite.kgf.data

import com.google.gson.annotations.SerializedName

// Data classes for K3 30-second game history (distinct from K360)
data class K330GameHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: K330GameHistoryResult?
)

data class K330GameHistoryResult(
    @SerializedName("history") val history: List<K330GameHistoryItem>
)

data class K330GameHistoryItem(
    @SerializedName("id") val id: String?,
    @SerializedName("datetime") val period: String?,
    @SerializedName("bidNum") val bidNum: String?,
    @SerializedName("bidOddEven") val bidOddEven: String?,
    @SerializedName("bidBigSmall") val bidBigSmall: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("adminWinNum") val adminWinNum: String?,
    @SerializedName("current_dt") val currentDt: String?,
    @SerializedName("finalBidBigSmall") val finalBidBigSmall: String?,
    @SerializedName("finalBidOddEven") val finalBidOddEven: String?
)

// Data classes for K3 30-second game my history (distinct from K360)
data class K330MyHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: K330MyHistoryResult
)

data class K330MyHistoryResult(
    @SerializedName("history") val history: List<K330MyHistoryItem>
)

data class K330MyHistoryItem(
    @SerializedName("id") val id: String?,
    @SerializedName("bidNum") val bidNum: String?,
    @SerializedName("bidType") val bidType: String?,
    @SerializedName("period") val period: String?,
    @SerializedName("boost") val boost: String?,
    @SerializedName("userid") val userid: String?,
    @SerializedName("price") val price: String?,
    @SerializedName("quantity") val quantity: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("bet_choice_status") val betChoiceStatus: String?,
    @SerializedName("bet_choice_result") val betChoiceResult: String?,
    @SerializedName("totalamount") val totalamount: String?,
    @SerializedName("adminWinStatus") val adminWinStatus: String?,
    @SerializedName("adminWinBid") val adminWinBid: String?,
    @SerializedName("adminWinTotal") val adminWinTotal: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("calculated_totalAmount") val calculatedTotalAmount: Double?
)
