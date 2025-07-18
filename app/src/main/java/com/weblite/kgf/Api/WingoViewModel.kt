package com.weblite.kgf.Api2

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.data.PeriodIdResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WingoViewModel @Inject constructor(
    private val repository: UserRepository,
    private val timerService: WingoTimerService
) : ViewModel() {

    // Expose timer service states
    val timeRemaining: StateFlow<Int> = timerService.timeRemaining
    val currentPeriod: StateFlow<String> = timerService.currentPeriod

    // Game History State
    private val _gameHistoryResponse = MutableStateFlow<Resource<GameHistoryResponse>?>(null)
    val gameHistoryResponse: StateFlow<Resource<GameHistoryResponse>?> = _gameHistoryResponse

    // My History State
    private val _myHistoryResponse = MutableStateFlow<Resource<MyHistoryResponse>?>(null)
    val myHistoryResponse: StateFlow<Resource<MyHistoryResponse>?> = _myHistoryResponse

    // Game History polling job
    private var gameHistoryPollingJob: kotlinx.coroutines.Job? = null

    init {
        // Fetch initial data and start timer
//        viewModelScope.launch {
//            timerService.fetchInitialData()
//            timerService.startTimer()
//        }

        // Fetch game history initially
        fetchGameHistory()
        // Fetch my history initially
        fetchMyHistory()

        // Listen for timer cycle completions to auto-refresh data
        viewModelScope.launch {
            timerService.onTimerCycleComplete.collect {
                // Refresh data every time timer cycle completes (every 30 seconds)
                fetchGameHistory()
                fetchMyHistory()
            }
        }
    }

    // Call this when navigating to game screen
//    suspend fun fetchPeriodInfoAndSave() {
//        timerService.fetchInitialData()
//    }

    private val _periodId = MutableSharedFlow<PeriodIdUIEvent>()
    val periodId : SharedFlow<PeriodIdUIEvent> = _periodId

    fun fetchPeriodId(){
        viewModelScope.launch {
            val result = repository.getPeriodId()
            result.fold(
                onSuccess = {
                    _periodId.emit(PeriodIdUIEvent.Success(it.copy()))
                },
                onFailure = {
                    _periodId.emit(PeriodIdUIEvent.Failure(it.message ?: "Unknown Error"))
                }
            )
        }
    }

    suspend fun placeBet(
        bidNum: String,
        bidType: String,
        period: String,
        quantity: String,
        price: String
    ): Result<BetResponse> {
        return try {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"

            if (userId == "0") {
                Result.failure(Exception("User not logged in"))
            } else {
                val response = repository.placeBet(
                    userid = userId,
                    bidNum = bidNum,
                    bidType = bidType,
                    period = period,
                    quantity = quantity,
                    price = price
                )

                if (response.isSuccessful) {
                    response.body()?.let { betResponse ->
                        Result.success(betResponse)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Bet placement failed: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun fetchGameHistory() {
        viewModelScope.launch {
            _gameHistoryResponse.value = Resource.Loading()
            try {
                val response = repository.getWingo30SecGameHistory()
                if (response.isSuccessful) {
                    response.body()?.let { gameHistoryResponse ->
                        _gameHistoryResponse.value = Resource.Success(gameHistoryResponse)
                    } ?: run {
                        _gameHistoryResponse.value = Resource.Error("Empty response")
                    }
                } else {
                    _gameHistoryResponse.value = Resource.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _gameHistoryResponse.value = Resource.Error("Exception: ${e.message}")
            }
        }
    }

    fun fetchMyHistory() {
        viewModelScope.launch {
            _myHistoryResponse.value = Resource.Loading()
            try {
                val userId = SharedPrefManager.getString("user_id", "0") ?: "0"

                if (userId == "0") {
                    _myHistoryResponse.value = Resource.Error("User not logged in")
                    return@launch
                }

                val response = repository.getWingo30SecMyHistory(userId)
                if (response.isSuccessful) {
                    response.body()?.let { myHistoryResponse ->
                        _myHistoryResponse.value = Resource.Success(myHistoryResponse)
                    } ?: run {
                        _myHistoryResponse.value = Resource.Error("Empty response")
                    }
                } else {
                    _myHistoryResponse.value = Resource.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _myHistoryResponse.value = Resource.Error("Exception: ${e.message}")
            }
        }
    }

    // Start frequent polling for game history (called when Game History tab is selected)
    fun onGameHistoryTabSelected() {
        gameHistoryPollingJob?.cancel()
        gameHistoryPollingJob = viewModelScope.launch {
            while (true) {
                fetchGameHistory()
                kotlinx.coroutines.delay(2000) // Poll every 2 seconds
            }
        }
    }

    // Stop polling and fetch my history once (called when My History tab is selected)
    fun onMyHistoryTabSelected() {
        gameHistoryPollingJob?.cancel()
        fetchMyHistory()
    }

    fun refreshGameHistory() {
        fetchGameHistory()
    }

    fun refreshMyHistory() {
        fetchMyHistory()
    }



    override fun onCleared() {
        super.onCleared()
        gameHistoryPollingJob?.cancel()
        timerService.stopTimer()
    }
}

sealed class PeriodIdUIEvent {
    data class Success(val response: PeriodIdResponse) : PeriodIdUIEvent()
    data class Failure(val msg: String) : PeriodIdUIEvent()
}