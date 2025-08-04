package com.weblite.kgf.data.models.games

import com.google.gson.annotations.SerializedName

data class DragonTigerWinResultResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: DragonTigerWinResult?
)

data class DragonTigerWinResult(
    @SerializedName("result") val result: String?
)
