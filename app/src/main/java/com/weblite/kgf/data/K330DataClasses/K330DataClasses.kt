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
    val period: String,
    val result: String, // This is the winning number/sum
    val color: String,
    val size: String // This is Big/Small
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
    val period: String,
    val bidNum: String,
    val price: String,
    val status: String,
    val totalamount: String
)
