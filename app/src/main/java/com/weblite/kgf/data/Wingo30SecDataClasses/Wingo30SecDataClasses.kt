package com.weblite.kgf.data.Wingo30SecDataClasses

// Data classes for BettingGameResult API response
data class BettingGameResultResponse(
    val status: String?,
    val status_code: Int?,
    val msg: String?,
    val result: BettingGameResultData?
)

data class BettingGameResultData(
    val total_winning_amount: String?,
    val getLatestWingoData: List<BettingGameResultItem>?
)

data class BettingGameResultItem(
    val id: String?,
    val datetime: String?,
    val timer: String?,
    val color_result: String?,
    val number_result: String?,
    val big_small_result: String?,
    val total: String?,
    val current: String?,
    val adminWinNum: String?,
    val adminWinColor: String?,
    val adminWinBigSmall: String?,
    val adminWinTotal: String?
)


