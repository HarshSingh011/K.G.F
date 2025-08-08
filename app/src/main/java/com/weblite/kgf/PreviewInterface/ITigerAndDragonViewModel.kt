package com.weblite.kgf.PreviewInterface

import com.weblite.kgf.Api.Resource
import com.weblite.kgf.data.models.games.TigerGameHistoryResponse
import com.weblite.kgf.data.models.games.TigerPeriodIdResponse
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ITigerAndDragonViewModel {
    val secondsRemaining: StateFlow<Int>
    val gameHistory: StateFlow<Resource<TigerGameHistoryResponse?>>
    val timerEnded: StateFlow<Boolean>
    val periodId: SharedFlow<Resource<TigerPeriodIdResponse>>
    fun startGameHistoryPolling()
    fun fetchGameHistory()
    fun stopGameHistoryPolling()
    fun fetchMyHistory()
    fun fetchPeriodId()
    fun placeBet(type: String, amount: Int)
}