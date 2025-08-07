package com.weblite.kgf.domain.model

import com.google.gson.annotations.SerializedName

data class PromotionViewResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val message: String,
    @SerializedName("result") val result: PromotionViewResult
)

data class PromotionViewResult(
    @SerializedName("user_id") val userId: String,
    @SerializedName("rowCount") val rowCount: Int,
    @SerializedName("referralCount") val referralCount: Int,
    @SerializedName("directDepositCount") val directDepositCount: Int,
    @SerializedName("directDepositAmount") val directDepositAmount: Int,
    @SerializedName("directFirstDepositCount") val directFirstDepositCount: Int,
    @SerializedName("indirectDepositCount") val indirectDepositCount: Int,
    @SerializedName("indirectDepositAmount") val indirectDepositAmount: Int,
    @SerializedName("indirectFirstDepositCount") val indirectFirstDepositCount: Int,
    @SerializedName("myTodayCommission") val myTodayCommission: String?,
    @SerializedName("totalCommission") val totalCommission: String
)
