package com.weblite.kgf.domain.model

import com.google.gson.annotations.SerializedName

data class DirectTeamDataResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: DirectTeamDataResult
)

data class DirectTeamDataResult(
    @SerializedName("result") val result: List<DirectTeamMember>
)

data class DirectTeamMember(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("joining_date") val joiningDate: String,
    @SerializedName("deposit_amount") val depositAmount: String,
    @SerializedName("total_commission") val totalCommission: String,
    @SerializedName("level") val level: String
)
