package com.weblite.kgf.ui.screens.game

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import com.weblite.kgf.Api2.UserRepository
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.PeriodIdUIEvent
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.Api2.GameHistoryResponse
import com.weblite.kgf.Api2.MyHistoryResponse
import com.weblite.kgf.data.PeriodIdResponse
import javax.inject.Inject

@HiltViewModel
class WingoGameViewModel @Inject constructor(
    application: Application,
    private val repository: UserRepository
) : AndroidViewModel(application) {

    // Game History State
    private val _gameHistoryResponse = MutableStateFlow<Resource<GameHistoryResponse>?>(null)
    val gameHistoryResponse: StateFlow<Resource<GameHistoryResponse>?> = _gameHistoryResponse

    // My History State
    private val _myHistoryResponse = MutableStateFlow<Resource<MyHistoryResponse>?>(null)
    val myHistoryResponse: StateFlow<Resource<MyHistoryResponse>?> = _myHistoryResponse

    // Period ID events - using the same logic as MainViewModel
    private val _periodId = MutableSharedFlow<PeriodIdUIEvent>()
    val periodId: SharedFlow<PeriodIdUIEvent> = _periodId

    // Expose current period value directly for simple access
    private var _currentPeriodValue: String? = null
    val currentPeriodValue: String?
        get() = _currentPeriodValue

    // Polling jobs
    private var gameHistoryJob: Job? = null
    private var isGameHistoryActive = true

    init {
        Log.d("WingoGameVM", "ViewModel initialized")
        startGameHistoryPolling()
    }

    override fun onCleared() {
        super.onCleared()
        gameHistoryJob?.cancel()
        Log.d("WingoGameVM", "ViewModel cleared")
    }

    // Fetch Period ID - EXACTLY like MainViewModel
    fun fetchPeriodId() {
        viewModelScope.launch {
            try {
                val result = repository.getPeriodId()
                result.fold(
                    onSuccess = { periodResponse ->
                        _periodId.emit(PeriodIdUIEvent.Success(periodResponse.copy()))
                        _currentPeriodValue = periodResponse.result.firstOrNull()?.dateTime
                        Log.d("WingoGameVM", "Period ID fetched successfully: ${periodResponse.result.firstOrNull()?.dateTime}")
                    },
                    onFailure = { error ->
                        _periodId.emit(PeriodIdUIEvent.Failure(error.message ?: "Unknown Error"))
                        Log.e("WingoGameVM", "Failed to fetch period ID: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _periodId.emit(PeriodIdUIEvent.Failure(e.message ?: "Unknown Error"))
                Log.e("WingoGameVM", "Exception fetching period ID", e)
            }
        }
    }

    // Game History Polling Management
    private fun startGameHistoryPolling() {
        gameHistoryJob?.cancel()
        isGameHistoryActive = true
        gameHistoryJob = viewModelScope.launch {
            Log.d("WingoGameVM", "Starting Game History polling")
            while (isGameHistoryActive) {
                fetchGameHistory()
                delay(3000) // Poll every 3 seconds
            }
            Log.d("WingoGameVM", "Game History polling stopped")
        }
    }

    private fun stopGameHistoryPolling() {
        Log.d("WingoGameVM", "Stopping Game History polling")
        isGameHistoryActive = false
        gameHistoryJob?.cancel()
        gameHistoryJob = null
    }

    // Tab Selection Handlers
    fun onGameHistoryTabSelected() {
        Log.d("WingoGameVM", "Game History tab selected")
        stopGameHistoryPolling()
        startGameHistoryPolling()
    }

    fun onMyHistoryTabSelected() {
        Log.d("WingoGameVM", "My History tab selected")
        stopGameHistoryPolling()
        fetchMyHistory()
    }

    // Fetch Game History using Repository
    fun fetchGameHistory() {
        viewModelScope.launch {
            // Do NOT set Resource.Loading() here to prevent flickering during polling
            try {
                Log.d("WingoGameVM", "Fetching Game History")

                val response = repository.getWingo30SecGameHistory()
                if (response.isSuccessful) {
                    response.body()?.let { newGameHistoryResponse ->
                        val currentData = _gameHistoryResponse.value?.data
                        // Only update if the new data is different from the current data
                        if (currentData == null || !areGameHistoriesIdentical(currentData, newGameHistoryResponse)) {
                            _gameHistoryResponse.value = Resource.Success(newGameHistoryResponse)
                            Log.d("WingoGameVM", "Game history updated: ${newGameHistoryResponse.result.history.size} items")
                        } else {
                            Log.d("WingoGameVM", "Game history unchanged, skipping update.")
                        }
                    } ?: run {
                        _gameHistoryResponse.value = Resource.Error("Empty response")
                        Log.e("WingoGameVM", "Game history response body is null")
                    }
                } else {
                    _gameHistoryResponse.value = Resource.Error("Server error: ${response.code()}")
                    Log.e("WingoGameVM", "Game history API failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _gameHistoryResponse.value = Resource.Error("Exception: ${e.message}")
                Log.e("WingoGameVM", "Error fetching game history: ${e.message}", e)
            }
        }
    }

    // Helper function to compare GameHistoryResponse objects
    private fun areGameHistoriesIdentical(old: GameHistoryResponse, new: GameHistoryResponse): Boolean {
        if (old.result.history.size != new.result.history.size) return false
        if (old.result.history.isEmpty()) return true // Both empty, considered identical
        // Compare the first item's datetime as a quick check for new data
        return old.result.history.first().datetime == new.result.history.first().datetime
    }

    // Fetch My History using new API endpoint
    fun fetchMyHistory() {
        viewModelScope.launch {
            // Do NOT set Resource.Loading() here to prevent flickering
            try {
                val userId = SharedPrefManager.getString("user_id", "0") ?: "0"

                Log.d("WingoGameVM", "=== FETCHING MY HISTORY ===")
                Log.d("WingoGameVM", "User ID: $userId")
                Log.d("WingoGameVM", "API: https://newkgfindia.com/web/Api/wingo30secMyHistory?user_id=$userId")
                Log.d("WingoGameVM", "================================")

                if (userId == "0") {
                    _myHistoryResponse.value = Resource.Error("User not logged in")
                    return@launch
                }

                val response = repository.getWingo30SecMyHistory(userId)
                if (response.isSuccessful) {
                    response.body()?.let { newMyHistoryResponse ->
                        // Removed the areMyHistoriesIdentical check to ensure UI updates for status changes
                        _myHistoryResponse.value = Resource.Success(newMyHistoryResponse)
                        Log.d("WingoGameVM", "My history updated: ${newMyHistoryResponse.result.history.size} items")
                    } ?: run {
                        _myHistoryResponse.value = Resource.Error("Empty response")
                        Log.e("WingoGameVM", "My history response body is null")
                    }
                } else {
                    _myHistoryResponse.value = Resource.Error("Server error: ${response.code()}")
                    Log.e("WingoGameVM", "My history API failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _myHistoryResponse.value = Resource.Error("Exception: ${e.message}")
                Log.e("WingoGameVM", "Error fetching my history: ${e.message}", e)
            }
        }
    }

    // Helper function to compare MyHistoryResponse objects (removed for now to force updates)
    // private fun areMyHistoriesIdentical(old: MyHistoryResponse, new: MyHistoryResponse): Boolean {
    //     if (old.result.history.size != new.result.history.size) return false
    //     if (old.result.history.isEmpty()) return true // Both empty, considered identical
    //     // Compare the first item's period as a quick check for new data
    //     return old.result.history.first().period == new.result.history.first().period
    // }

    // Place Bet using Repository
    suspend fun placeBet(
        bidNum: String,
        bidType: String,
        quantity: String,
        price: String
    ): Result<String> {
        return try {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"

            Log.d("WingoGameVM", "=== PLACING BET ===")
            Log.d("WingoGameVM", "User ID: $userId")
            Log.d("WingoGameVM", "Bet Type: $bidType")
            Log.d("WingoGameVM", "Bet Number/Color: $bidNum")
            Log.d("WingoGameVM", "Quantity: $quantity")
            Log.d("WingoGameVM", "Price: $price")
            Log.d("WingoGameVM", "===================")

            if (userId == "0") {
                Result.failure(Exception("User not logged in"))
            } else {
                // Get current period from the screen's timer logic
                val currentPeriod = _currentPeriodValue ?: "2507021733" // Use the ViewModel's current period value

                val response = repository.placeBet(
                    userid = userId,
                    bidNum = bidNum,
                    bidType = bidType,
                    period = currentPeriod,
                    quantity = quantity,
                    price = price
                )

                if (response.isSuccessful) {
                    response.body()?.let { betResponse ->
                        if (betResponse.status == "success") {
                            Log.d("WingoGameVM", "Bet placed successfully: ${betResponse.msg}")
                            // Refresh my history after successful bet
                            fetchMyHistory()
                            Result.success(betResponse.msg)
                        } else {
                            Log.e("WingoGameVM", "Bet failed: ${betResponse.msg}")
                            Result.failure(Exception(betResponse.msg))
                        }
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Log.e("WingoGameVM", "Bet placement failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("Bet placement failed: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Log.e("WingoGameVM", "Error placing bet", e)
            Result.failure(e)
        }
    }

    // Refresh methods
    fun refreshGameHistory() {
        fetchGameHistory()
    }

    fun refreshMyHistory() {
        fetchMyHistory()
    }

    // Debug methods
    fun logCurrentInfo() {
        val userId = SharedPrefManager.getString("user_id", "not_found")

        Log.d("WingoGameVM", "====== CURRENT APP STATE ======")
        Log.d("WingoGameVM", "User ID: $userId")
        Log.d("WingoGameVM", "Timestamp: ${System.currentTimeMillis()}")
        Log.d("WingoGameVM", "================================")
    }
}
