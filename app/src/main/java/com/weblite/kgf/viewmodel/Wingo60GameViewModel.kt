package com.weblite.kgf.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // Game History State - Initialize with Loading state
    private val _gameHistoryResponse = MutableStateFlow<Resource<GameHistory60SecResponse>?>(Resource.Loading())
    val gameHistoryResponse: StateFlow<Resource<GameHistory60SecResponse>?> = _gameHistoryResponse

    // My History State - Initialize with Loading state
    private val _myHistoryResponse = MutableStateFlow<Resource<Wingo60SecMyHistoryResponse>?>(Resource.Loading())
    val myHistoryResponse: StateFlow<Resource<Wingo60SecMyHistoryResponse>?> = _myHistoryResponse

    // Selected History Tab State
    private val _selectedHistoryTab = MutableStateFlow("Game History")
    val selectedHistoryTab: StateFlow<String> = _selectedHistoryTab

    // Track if we have valid data to avoid showing loading unnecessarily
    private var hasGameHistoryData = false
    private var hasMyHistoryData = false

    private var timerJob: Job? = null
    private var gameHistoryPollingJob: Job? = null
    // No separate myHistoryPollingJob, as per WingoGameViewModel logic

    init {
        // This ensures the API is called and timer is set when the ViewModel is first created
        fetchPeriodIdAndStartTimer()
        // Start game history polling by default when the ViewModel is initialized
        startGameHistoryPolling()
    }

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
                        // After fetching a new period (either initially or after timer reset),
                        // if My History tab is active, refresh My History.
                        // This is handled by the tab selection logic now.
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
            // After fetching a new period, if My History tab is active, refresh My History.
            if (_selectedHistoryTab.value == "My History") {
                fetchMyHistory()
            }
        }
    }

    private fun startGameHistoryPolling() {
        gameHistoryPollingJob?.cancel() // Cancel any existing polling job
        gameHistoryPollingJob = viewModelScope.launch {
            Log.d("WingoGameVM", "Starting Game History polling")
            while (true) { // Loop indefinitely while this job is active
                fetchGameHistory()
                delay(3000) // Poll every 3 seconds
            }
        }
    }

    private fun stopGameHistoryPolling() {
        Log.d("WingoGameVM", "Stopping Game History polling")
        gameHistoryPollingJob?.cancel()
        gameHistoryPollingJob = null
    }

    // Tab Selection Handlers
    fun onGameHistoryTabSelected() {
        Log.d("WingoGameVM", "Game History tab selected")
        _selectedHistoryTab.value = "Game History"
        startGameHistoryPolling() // Start polling for game history
    }

    fun onMyHistoryTabSelected() {
        Log.d("WingoGameVM", "My History tab selected")
        _selectedHistoryTab.value = "My History"
        stopGameHistoryPolling() // Stop game history polling
        fetchMyHistory() // Fetch my history once
    }

    fun fetchGameHistory() {
        viewModelScope.launch {
            try {
                Log.d("WingoGameVM", "Fetching Game History")

                // Only show loading if we don't have data yet
                if (!hasGameHistoryData) {
                    _gameHistoryResponse.value = Resource.Loading()
                }

                val response = repository.getWingo60SecGameHistory()
                if (response.isSuccessful) {
                    response.body()?.let { newGameHistoryResponse ->
                        val currentData = _gameHistoryResponse.value?.data
                        // Only update if the new data is different from the current data
                        if (currentData == null || !areGameHistoriesIdentical(currentData, newGameHistoryResponse)) {
                            _gameHistoryResponse.value = Resource.Success(newGameHistoryResponse)
                            hasGameHistoryData = true
                            Log.d("WingoGameVM", "Game history updated: ${newGameHistoryResponse.result.history.size} items")
                        } else {
                            Log.d("WingoGameVM", "Game history unchanged, skipping update.")
                        }
                    } ?: run {
                        // Only show error if we don't have existing data
                        if (!hasGameHistoryData) {
                            _gameHistoryResponse.value = Resource.Error("Empty response")
                        }
                        Log.e("WingoGameVM", "Game history response body is null")
                    }
                } else {
                    // Only show error if we don't have existing data
                    if (!hasGameHistoryData) {
                        _gameHistoryResponse.value = Resource.Error("Server error: ${response.code()}")
                    }
                    Log.e("WingoGameVM", "Game history API failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Only show error if we don't have existing data
                if (!hasGameHistoryData) {
                    _gameHistoryResponse.value = Resource.Error("Exception: ${e.message}")
                }
                Log.e("WingoGameVM", "Error fetching game history: ${e.message}", e)
            }
        }
    }

    // Helper function to compare GameHistory60SecResponse objects
    private fun areGameHistoriesIdentical(old: GameHistory60SecResponse, new: GameHistory60SecResponse): Boolean {
        if (old.result.history.size != new.result.history.size) return false
        if (old.result.history.isEmpty()) return true // Both empty, considered identical
        // Compare the first item's period_60 as a quick check for new data
        return old.result.history.first().period_60 == new.result.history.first().period_60
    }

    // Fetch My History using new API endpoint
    fun fetchMyHistory() {
        viewModelScope.launch {
            try {
                val userId = SharedPrefManager.getString("user_id", "0") ?: "0"

                Log.d("WingoGameVM", "=== FETCHING MY HISTORY ===")
                Log.d("WingoGameVM", "User ID: $userId")
                Log.d("WingoGameVM", "================================")

                if (userId == "0") {
                    _myHistoryResponse.value = Resource.Error("User not logged in")
                    return@launch
                }

                // Only show loading if we don't have data yet
                if (!hasMyHistoryData) {
                    _myHistoryResponse.value = Resource.Loading()
                }

                val response = repository.getWingo60SecMyHistory(userId)
                if (response.isSuccessful) {
                    response.body()?.let { newMyHistoryResponse ->
                        val currentData = _myHistoryResponse.value?.data
                        // Removed the areMyHistoriesIdentical check to ensure UI updates for status changes
                        // For My History, we generally want to update even if the period is the same,
                        // as the status or winning amount might change.
                        _myHistoryResponse.value = Resource.Success(newMyHistoryResponse)
                        hasMyHistoryData = true
                        Log.d("WingoGameVM", "My history updated: ${newMyHistoryResponse.result.history.size} items")
                    } ?: run {
                        // Only show error if we don't have existing data
                        if (!hasMyHistoryData) {
                            _myHistoryResponse.value = Resource.Error("Empty response")
                        }
                        Log.e("WingoGameVM", "My history response body is null")
                    }
                } else {
                    // Only show error if we don't have existing data
                    if (!hasMyHistoryData) {
                        _myHistoryResponse.value = Resource.Error("Server error: ${response.code()}")
                    }
                    Log.e("WingoGameVM", "My history API failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Only show error if we don't have existing data
                if (!hasMyHistoryData) {
                    _myHistoryResponse.value = Resource.Error("Exception: ${e.message}")
                }
                Log.e("WingoGameVM", "Error fetching my history: ${e.message}", e)
            }
        }
    }

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
                // Get current period from the ViewModel's state
                val currentPeriod = _sixtySecPeriodId.value ?: "2507021733" // Use a default if null

                val response = repository.place60SecBet(
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
                            // This will be handled by the fetchMyHistory() call if My History tab is active
                            fetchMyHistory() // Call fetchMyHistory once after a successful bet
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel() // Ensure timer is cancelled when ViewModel is destroyed
        gameHistoryPollingJob?.cancel() // Ensure polling is cancelled
    }
}
