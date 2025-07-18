package com.weblite.kgf.data

import com.google.gson.annotations.SerializedName

data class PeriodIdResponse(
    @SerializedName("status") val status : String,
    @SerializedName("status_code") val statusCode : Int,
    @SerializedName("msg") val msg : String,
    @SerializedName("result") val result: List<ResultItem>
)

data class ResultItem(
    @SerializedName("datetime") val dateTime: String,
    @SerializedName("current") val currentTime: String
)