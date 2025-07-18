package com.weblite.kgf.data

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
