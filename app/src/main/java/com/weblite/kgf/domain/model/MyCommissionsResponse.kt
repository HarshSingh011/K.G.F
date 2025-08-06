package com.weblite.kgf.domain.model

import com.google.gson.annotations.SerializedName

data class MyCommissionsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: MyCommissionsResult
)

data class MyCommissionsResult(
    @SerializedName("myCommissions") val myCommissions: List<MyCommission>,
    @SerializedName("totalCommission") val totalCommission: String
)

data class MyCommission(
    @SerializedName("day_commission_amount") val dayCommissionAmount: String,
    @SerializedName("date") val date: String
)
