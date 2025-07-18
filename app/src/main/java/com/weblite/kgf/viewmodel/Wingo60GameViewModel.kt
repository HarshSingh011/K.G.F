package com.weblite.kgf.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.Api2.GameHistoryResponse
import com.weblite.kgf.Api2.MyHistoryResponse
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.data.Wingo60GameRepository
import com.weblite.kgf.data.Wingo60SecDataClasses.GameHistory60SecResponse
import com.weblite.kgf.data.Wingo60SecDataClasses.Wingo60SecMyHistoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class Wingo60GameViewModel @Inject constructor(
    private val repository: Wingo60GameRepository
) : ViewModel() {

    private val _sixtySecPeriodId = MutableStateFlow<String?>(null)
    val sixtySecPeriodId: StateFlow<String?> = _sixtySecPeriodId

    private val _timeRemaining = MutableStateFlow(60)
    val timeRemaining: StateFlow<Int> = _timeRemaining

    // Game History State
    private val _gameHistoryResponse = MutableStateFlow<Resource<GameHistory60SecResponse>?>(null)
    val gameHistoryResponse: StateFlow<Resource<GameHistory60SecResponse>?> = _gameHistoryResponse

    // My History State
    private val _myHistoryResponse = MutableStateFlow<Resource<Wingo60SecMyHistoryResponse>?>(null)
    val myHistoryResponse: StateFlow<Resource<Wingo60SecMyHistoryResponse>?> = _myHistoryResponse

    private var timerJob: Job? = null

    init {
        // This ensures the API is called and timer is set when the ViewModel is first created
        // (i.e., when navigating to the screen).
        fetchPeriodIdAndStartTimer()
        startGameHistoryPolling()
    }

    // Polling jobs
    private var gameHistoryJob: Job? = null
    private var isGameHistoryActive = true

    fun fetchPeriodIdAndStartTimer() {
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id") // Get user ID from SharedPrefManager
            if (userId == null) {
                Log.e("Wingo60GameViewModel", "User ID not found in SharedPreferences.")
                _timeRemaining.value = 60 // Fallback to full timer
                startTimer()
                return@launch
            }

            val result = repository.getSixtySecondPeriodID(userId) // Pass user ID to repository
            result.onSuccess { response ->
                if (response.status == "success" && response.result.isNotEmpty()) {
                    val periodId = response.result[0].period60
                    val updateAtString = response.result[0].updateAt
                    _sixtySecPeriodId.value = periodId // Update period ID immediately
                    Log.d("Wingo60GameViewModel", "Fetched Period ID: $periodId, Update At: $updateAtString")

                    // Calculate time remaining based on update_at timestamp for better synchronization
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    try {
                        val updateAtDate = dateFormat.parse(updateAtString)
                        val calendar = Calendar.getInstance()
                        calendar.time = updateAtDate
                        calendar.add(Calendar.SECOND, 60) // Add 60 seconds to get the end of the period

                        val periodEndTimeMillis = calendar.timeInMillis
                        val currentTimeMillis = System.currentTimeMillis()
                        val calculatedRemaining = ((periodEndTimeMillis - currentTimeMillis) / 1000).toInt()

                        _timeRemaining.value = calculatedRemaining.coerceAtLeast(0) // Ensure it's not negative
                        Log.d("Wingo60GameViewModel", "Calculated time remaining: ${_timeRemaining.value}s (Period ends at: ${dateFormat.format(Date(periodEndTimeMillis))})")

                        // If the calculated remaining time is very small or zero, it means we might have just missed the period end.
                        // In this case, we should immediately re-fetch to get the *next* period.
                        if (_timeRemaining.value <= 0) {
                            Log.d("Wingo60GameViewModel", "Timer already expired or very low, re-fetching immediately for next period.")
                            fetchPeriodIdAndStartTimer() // Re-fetch for the next period
                        } else {
                            startTimer()
                        }
                    } catch (e: Exception) {
                        Log.e("Wingo60GameViewModel", "Error parsing date or calculating time: $updateAtString", e)
                        _timeRemaining.value = 60 // Fallback to full timer if parsing fails
                        startTimer()
                    }
                } else {
                    Log.e("Wingo60GameViewModel", "Failed to fetch 60-second period ID: ${response.msg}")
                    _timeRemaining.value = 60 // Fallback to full timer
                    startTimer()
                }
            }.onFailure { error ->
                Log.e("Wingo60GameViewModel", "Error fetching 60-second period ID", error)
                _timeRemaining.value = 60 // Fallback to full timer
                startTimer()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel() // Cancel any existing timer to prevent multiple timers running
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0) {
                delay(1000)
                _timeRemaining.value--
            }
            // Timer reached 0, fetch new period ID and restart the cycle
            Log.d("Wingo60GameViewModel", "Timer reached 0, fetching new period ID.")
            fetchPeriodIdAndStartTimer()
        }
    }



    private fun getWingo60SecGameHistory(){

    }

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

    fun fetchGameHistory() {
        viewModelScope.launch {
            // Do NOT set Resource.Loading() here to prevent flickering during polling
            try {
                Log.d("WingoGameVM", "Fetching Game History")

                val response = repository.getWingo60SecGameHistory()
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
    private fun areGameHistoriesIdentical(old: GameHistory60SecResponse, new: GameHistory60SecResponse): Boolean {
        if (old.result.history.size != new.result.history.size) return false
        if (old.result.history.isEmpty()) return true
        return old.result.history.first().period_60 == new.result.history.first().period_60
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

                val response = repository.getWingo60SecMyHistory(userId)
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel() // Ensure timer is cancelled when ViewModel is destroyed
        gameHistoryJob?.cancel()
    }
}
