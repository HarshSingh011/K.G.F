package com.weblite.kgf.ui.screens.game.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.Api.Resource
import com.weblite.kgf.Api.SharedPrefManager
import com.weblite.kgf.data.models.games.TigerPeriodIdResponse
import com.weblite.kgf.data.repository.GameRespsitories.TigerAndDragonRepository
import com.weblite.kgf.data.models.games.TigerGameHistoryResponse
import com.weblite.kgf.data.models.games.DragonTigerBetRequest
import com.weblite.kgf.data.models.games.DragonTigerBetResponse
import com.weblite.kgf.data.models.games.MyHistoryApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.Locale

@HiltViewModel
class TigerAndDragonViewModel @Inject constructor(
    private val repository: TigerAndDragonRepository
) : ViewModel() {

    // --- Win Result State ---
    private val _winResult = MutableStateFlow<String?>(null)
    val winResult: StateFlow<String?> = _winResult
    
    private val _showWinResult = MutableStateFlow(false)
    val showWinResult: StateFlow<Boolean> = _showWinResult

    fun fetchWinResult() {
        viewModelScope.launch {
            val result = repository.fetchDragonTigerWinResult()
            result.fold(
                onSuccess = { response ->
                    val win = response.result?.result
                    _winResult.value = win
                    if (win != null) {
                        _showWinResult.value = true
                        // Auto-hide after 3 seconds
                        kotlinx.coroutines.delay(3000)
                        _showWinResult.value = false
                    }
                },
                onFailure = {
                    _winResult.value = null
                    _showWinResult.value = false
                }
            )
        }
    }
    
    fun hideWinResult() {
        _showWinResult.value = false
    }

    private val _periodId = MutableSharedFlow<Resource<TigerPeriodIdResponse>>()
    val periodId: SharedFlow<Resource<TigerPeriodIdResponse>> = _periodId

    val userId = SharedPrefManager.getString("user_id", "0") ?: "0"

    private var _currentPeriodValue: String? = null
    val currentPeriodValue: String?
        get() = _currentPeriodValue
        
    private var _lastPeriodValue: String? = null

    // --- Timer State ---

    private val _secondsRemaining = MutableStateFlow(30)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining

    private var timerJob: Job? = null
    private var periodIdPollingJob: Job? = null
    private var serverPeriodEndTime: Long? = null

    init {
        fetchPeriodIdAndSyncTimer()
    }

    private fun fetchPeriodIdAndSyncTimer() {
        viewModelScope.launch {
            while (true) {
                try {
                    fetchPeriodId(syncTimer = true)
                    kotlinx.coroutines.delay(5000) // Poll every 5 seconds for better performance
                } catch (e: Exception) {
                    Log.e("TigerAndDragonVM", "Error in period ID polling: ${e.message}", e)
                    kotlinx.coroutines.delay(5000) // Continue polling even on error
                }
            }
        }
    }


    private val _timerEnded = MutableStateFlow(false)
    val timerEnded: StateFlow<Boolean> = _timerEnded

    private fun startTimerSync(endTimeMillis: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val remaining = ((endTimeMillis - now) / 1000).toInt().coerceAtLeast(0)
                _secondsRemaining.value = remaining
                if (remaining <= 0) {
                    _timerEnded.value = true
                    onTimerEndActions()
                    break
                }
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun onTimerEndActions() {
        // 1. Call bet API for placed coins
        placeAllBets()
        
        // 2. Wait a moment for bets to process, then fetch win result
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000) // Wait 1 second for bet processing
            fetchWinResult()
        }
        
        // 3. Refresh game history and my history
        fetchGameHistory()
        fetchMyHistory()
        
        // 4. Reset timerEnded after short delay so UI can react
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            _timerEnded.value = false
        }
    }

    private fun placeAllBets() {
        // TODO: Implement actual bet API call for all placed coins
        // Example: repository.placeBet(userId, placedCoins)
        Log.d("TigerAndDragonVM", "Placing all bets at timer end")
    }

    fun fetchPeriodId(syncTimer: Boolean = false) {
        viewModelScope.launch {
            _periodId.emit(Resource.Loading())
            try {
                val result = repository.fetchTigerPeriodId(userId)
                result.fold(
                    onSuccess = { periodResponse ->
                        _periodId.emit(Resource.Success(periodResponse.copy()))
                        val newPeriodId = periodResponse.result.firstOrNull()?.periodId
                        
                        // Check if period changed - if so, fetch win result for previous period
                        if (_currentPeriodValue != null && _lastPeriodValue != null && 
                            newPeriodId != _currentPeriodValue && newPeriodId != _lastPeriodValue) {
                            Log.d("TigerAndDragonVM", "Period changed from $_currentPeriodValue to $newPeriodId - fetching win result")
                            fetchWinResult()
                        }
                        
                        _lastPeriodValue = _currentPeriodValue
                        _currentPeriodValue = newPeriodId
                        
                        Log.d("TigerAndDragonVM", "Period ID fetched successfully: $newPeriodId")
                        
                        if (syncTimer && periodResponse.result.isNotEmpty()) {
                            val period = periodResponse.result[0]
                            val serverTime = try {
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(period.currentTime)?.time
                            } catch (e: Exception) { null }
                            val duration = 30 * 1000L // Default 30s, adjust if API provides
                            if (serverTime != null) {
                                serverPeriodEndTime = serverTime + duration
                                startTimerSync(serverPeriodEndTime!!)
                            }
                        }
                    },
                    onFailure = { error ->
                        _periodId.emit(Resource.Error(error.message ?: "Unknown Error"))
                        Log.e("TigerAndDragonVM", "Failed to fetch period ID: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _periodId.emit(Resource.Error(e.message ?: "Unknown Error"))
                Log.e("TigerAndDragonVM", "Exception fetching period ID", e)
            }
        }
    }

    fun fetchPeriodId() {
        viewModelScope.launch {
            _periodId.emit(Resource.Loading())
            try {
                val result = repository.fetchTigerPeriodId(userId)
                result.fold(
                    onSuccess = { periodResponse ->
                        _periodId.emit(Resource.Success(periodResponse.copy()))
                        val newPeriodId = periodResponse.result.firstOrNull()?.periodId
                        
                        // Check if period changed - if so, fetch win result for previous period
                        if (_currentPeriodValue != null && _lastPeriodValue != null && 
                            newPeriodId != _currentPeriodValue && newPeriodId != _lastPeriodValue) {
                            Log.d("TigerAndDragonVM", "Period changed from $_currentPeriodValue to $newPeriodId - fetching win result")
                            fetchWinResult()
                        }
                        
                        _lastPeriodValue = _currentPeriodValue
                        _currentPeriodValue = newPeriodId
                        
                        Log.d("TigerAndDragonVM", "Period ID fetched successfully: $newPeriodId")
                    },
                    onFailure = { error ->
                        _periodId.emit(Resource.Error(error.message ?: "Unknown Error"))
                        Log.e("TigerAndDragonVM", "Failed to fetch period ID: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _periodId.emit(Resource.Error(e.message ?: "Unknown Error"))
                Log.e("TigerAndDragonVM", "Exception fetching period ID", e)
            }
        }
    }

    // --- Game History State and Polling ---
    private val _gameHistory = MutableStateFlow<TigerGameHistoryResponse?>(null)
    val gameHistory: StateFlow<TigerGameHistoryResponse?> = _gameHistory

    // --- My History State ---
    private val _myHistory = MutableStateFlow<Resource<MyHistoryApiResponse>>(Resource.Loading())
    val myHistory: StateFlow<Resource<MyHistoryApiResponse>> = _myHistory

    private var gameHistoryPollingJob: Job? = null

    fun startGameHistoryPolling() {
        if (gameHistoryPollingJob?.isActive == true) return
        gameHistoryPollingJob = viewModelScope.launch {
            while (true) {
                try {
                    fetchGameHistory()
                    kotlinx.coroutines.delay(3000) // Poll every 3 seconds
                } catch (e: Exception) {
                    Log.e("TigerAndDragonVM", "Game history polling error: ${e.message}", e)
                    break
                }
            }
        }
        Log.d("TigerAndDragonVM", "Game history polling started")
    }

    fun stopGameHistoryPolling() {
        gameHistoryPollingJob?.cancel()
        gameHistoryPollingJob = null
        Log.d("TigerAndDragonVM", "Game history polling stopped")
    }

    fun fetchGameHistory() {
        viewModelScope.launch {
            try {
                val response = repository.fetchTigerGameHistory(userId)
                _gameHistory.value = response
                Log.d("TigerAndDragonVM", "Game history fetched successfully.")
            } catch (e: Exception) {
                _gameHistory.value = null
                Log.e("TigerAndDragonVM", "Error fetching game history: ${e.message}", e)
            }
        }
    }

    // --- Fetch My History ---
    fun fetchMyHistory() {
        viewModelScope.launch {
            _myHistory.value = Resource.Loading()
            try {
                val result = repository.fetchMyHistory(userId)
                result.fold(
                    onSuccess = { response ->
                        Log.d("TigerAndDragonVM", "Raw API response: $response")
                        Log.d("TigerAndDragonVM", "Response status: ${response.status}")
                        Log.d("TigerAndDragonVM", "Response result: ${response.result}")
                        Log.d("TigerAndDragonVM", "History list: ${response.result?.history}")

                        // Handle the nested structure properly
                        val historyList = response.result?.history ?: emptyList()
                        val safeResponse = response.copy(
                            result = response.result?.copy(history = historyList)
                        )

                        _myHistory.value = Resource.Success(safeResponse)
                        Log.d("TigerAndDragonVM", "My history fetched successfully. History count: ${historyList.size}")

                        // Debug: Log first few items
                        historyList.take(3).forEachIndexed { index, item ->
                            Log.d("TigerAndDragonVM", "History item $index: id=${item.id}, periodId=${item.periodId}, betChoice=${item.betChoice}, coins=${item.coins}, result=${item.result}")
                        }
                    },
                    onFailure = { error ->
                        _myHistory.value = Resource.Error(error.message ?: "Unknown error")
                        Log.e("TigerAndDragonVM", "Error fetching my history: ${error.message}", error)
                    }
                )
            } catch (e: Exception) {
                _myHistory.value = Resource.Error(e.message ?: "Unknown error")
                Log.e("TigerAndDragonVM", "Exception fetching my history: ${e.message}", e)
            }
        }
    }

    // New: Place Bet function
    suspend fun placeBet(betChoice: String, coins: Int): Result<DragonTigerBetResponse> {
        Log.d("TigerAndDragonVM", "placeBet called for $betChoice with $coins coins.")
        if (coins <= 0) {
            Log.e("TigerAndDragonVM", "Bet amount must be greater than 0 for $betChoice. Skipping API call.")
            return Result.failure(IllegalArgumentException("Bet amount must be greater than 0"))
        }
        val currentPeriod = _currentPeriodValue
        if (currentPeriod == null) {
            Log.e("TigerAndDragonVM", "Current period ID is not available for $betChoice. Cannot place bet.")
            return Result.failure(IllegalStateException("Current period ID is not available"))
        }

        val request = DragonTigerBetRequest(
            userId = userId,
            periodId = currentPeriod,
            coins = coins,
            betChoice = betChoice
        )

        return try {
            Log.d("TigerAndDragonVM", "Sending bet request for $betChoice: $request")
            val result = repository.placeDragonTigerBet(request)
            result.fold(
                onSuccess = { response ->
                    if (response.status == "success") {
                        Log.d("TigerAndDragonVM", "Bet placed successfully for $betChoice. Msg: ${response.msg}")
                        // Optionally refresh user balance or history here if needed
                        Result.success(response)
                    } else {
                        Log.e("TigerAndDragonVM", "Bet failed for $betChoice. Msg: ${response.msg}")
                        Result.failure(Exception(response.msg))
                    }
                },
                onFailure = { error ->
                    Log.e("TigerAndDragonVM", "API call failed for $betChoice: ${error.message}", error)
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e("TigerAndDragonVM", "Unexpected error placing bet for $betChoice: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        periodIdPollingJob?.cancel()
        gameHistoryPollingJob?.cancel()
        Log.d("TigerAndDragonVM", "ViewModel cleared, all jobs stopped")
    }
}
