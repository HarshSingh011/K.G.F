package com.weblite.kgf.data.remote.vip

import com.google.gson.annotations.SerializedName

// Maps the full API response

data class VipApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: VipResult
)

data class VipResult(
    @SerializedName("DaysLeft") val daysLeft: Int,
    @SerializedName("userID") val userId: String,
    @SerializedName("totalExp") val totalExp: Long,
    @SerializedName("levelUser") val levelUser: Int
)
