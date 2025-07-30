package com.weblite.kgf.data

data class K3PopupHistoryResponse(
    val status: String,
    val status_code: Int,
    val msg: String,
    val result: K3PopupHistoryResult
)

data class K3PopupHistoryResult(
    val total_winning_amount: Double,
    val getLatestWingoData: List<K3PopupHistoryItem>
)

data class K3PopupHistoryItem(
    val id: String,
    val datetime: String,
    val timer: String,
    val result: String,
    val bidNum: String,
    val bidOddEven: String,
    val bidBigSmall: String,
    val status: String,
    val adminWinStatus: String,
    val adminWinNum: String,
    val adminWinBigSmall: String,
    val adminWinOddEven: String,
    val updated_at: String,
    val current_dt: String
)
