package com.weblite.kgf.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.data.K360GameHistoryItem
import com.weblite.kgf.data.K360GameRepository
import com.weblite.kgf.data.K360MyHistoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class K360GameViewModel @Inject constructor(
    private val repository: K360GameRepository
) : ViewModel() {

    private val _k3PeriodId = MutableStateFlow<String?>(null)
    val k3PeriodId: StateFlow<String?> = _k3PeriodId.asStateFlow()

    private val _k3TimeRemaining = MutableStateFlow<Long>(0L)
    val k3TimeRemaining: StateFlow<Long> = _k3TimeRemaining.asStateFlow()

    private val _k3GameHistory = MutableStateFlow<Resource<List<K360GameHistoryItem>>>(Resource.Loading())
    val k3GameHistory: StateFlow<Resource<List<K360GameHistoryItem>>> = _k3GameHistory.asStateFlow()

    private val _k3MyHistory = MutableStateFlow<Resource<List<K360MyHistoryItem>>>(Resource.Loading())
    val k3MyHistory: StateFlow<Resource<List<K360MyHistoryItem>>> = _k3MyHistory.asStateFlow()

    private val _activeHistoryTab = MutableStateFlow("Game History") // "Game History" or "My History"
    val activeHistoryTab: StateFlow<String> = _activeHistoryTab.asStateFlow()

    private var timerJob: Job? = null
    private var gameHistoryPollingJob: Job? = null
    private var hasGameHistoryData = false
    private var hasMyHistoryData = false

    init {
        fetchK3PeriodIdAndStartTimer()
        startGameHistoryPolling() // Start game history polling by default
    }

    fun setActiveHistoryTab(tab: String) {
        _activeHistoryTab.value = tab
        if (tab == "My History") {
            stopGameHistoryPolling()
            fetchK3MyHistory() // Fetch My History only when tab is selected
        } else {
            startGameHistoryPolling() // Restart Game History polling
        }
    }

    private fun fetchK3PeriodIdAndStartTimer() {
        timerJob?.cancel() // Cancel any existing timer job
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0")
            if (userId == "0") {
                Log.e("K360GameViewModel", "User ID not found in SharedPreferences. Cannot fetch period ID.")
                _k3PeriodId.value = "N/A"
                _k3TimeRemaining.value = 0L // No timer if no period ID
                return@launch
            }

            try {
                val response = repository.getK360OneMinPeriodID(userId)
                if (response.isSuccessful) {
                    response.body()?.result?.let { result ->
                        _k3PeriodId.value = result.periodId // This now correctly maps to "datetime" from API

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                        val serverCurrentTimeMillis = try {
                            dateFormat.parse(result.currentTime)?.time ?: System.currentTimeMillis()
                        } catch (e: ParseException) {
                            Log.e("K360GameViewModel", "Error parsing current_time date string: ${result.currentTime}", e)
                            System.currentTimeMillis()
                        }

                        // Safely get period duration in seconds, defaulting to 60
                        val periodDurationSeconds = result.time ?: 60
                        val periodDurationMillis = periodDurationSeconds.toLong() * 1000L

                        // Calculate the end time of the current period based on server's current_dt and period duration
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = serverCurrentTimeMillis
                            add(Calendar.SECOND, periodDurationSeconds)
                        }
                        val periodEndTimeMillis = calendar.timeInMillis

                        // Calculate remaining time from now until the period end
                        val remaining = (periodEndTimeMillis - System.currentTimeMillis()).coerceAtLeast(0L)

                        _k3TimeRemaining.value = remaining
                        Log.d("K360GameViewModel", "K3 Period ID: ${result.periodId}, Initial Time Remaining: ${remaining / 1000}s")

                        if (_k3TimeRemaining.value <= 0) {
                            Log.d("K360GameViewModel", "Timer already expired or very low, re-fetching immediately for next period.")
                            delay(500) // Add a small delay to allow server to update to the next period
                            fetchK3PeriodIdAndStartTimer() // Re-fetch for the next period
                        } else {
                            startTimerCountdown()
                        }
                    } ?: run {
                        Log.e("K360GameViewModel", "Failed to fetch K3 period ID: Empty response body or result is null")
                        _k3PeriodId.value = "Error"
                        _k3TimeRemaining.value = 0L // Show 0 time if error
                    }
                } else {
                    Log.e("K360GameViewModel", "Failed to fetch K3 period ID: ${response.code()} - ${response.message()}")
                    _k3PeriodId.value = "Error"
                    _k3TimeRemaining.value = 0L // Show 0 time if error
                }
            } catch (e: Exception) {
                Log.e("K360GameViewModel", "Error fetching K3 period ID", e)
                _k3PeriodId.value = "Error"
                _k3TimeRemaining.value = 0L // Show 0 time if error
            }
        }
    }

    private fun startTimerCountdown() {
        timerJob?.cancel() // Cancel any existing timer to prevent multiple timers running
        timerJob = viewModelScope.launch {
            while (_k3TimeRemaining.value > 0) {
                delay(1000) // Decrement every second
                _k3TimeRemaining.value -= 1000
            }
            // Timer reached 0, fetch new period ID and restart the cycle
            Log.d("K360GameViewModel", "Timer reached 0, fetching new period ID.")
            fetchK3PeriodIdAndStartTimer()
            // After fetching a new period, if My History tab is active, refresh My History.
            if (_activeHistoryTab.value == "My History") {
                fetchK3MyHistory()
            }
        }
    }

    private fun startGameHistoryPolling() {
        gameHistoryPollingJob?.cancel() // Cancel any existing polling job
        gameHistoryPollingJob = viewModelScope.launch {
            Log.d("K360GameViewModel", "Starting K3 Game History polling")
            while (true) {
                fetchK3GameHistory()
                delay(3000) // Poll every 3 seconds
            }
        }
    }

    private fun stopGameHistoryPolling() {
        Log.d("K360GameViewModel", "Stopping K3 Game History polling")
        gameHistoryPollingJob?.cancel()
        gameHistoryPollingJob = null
    }

    fun fetchK3GameHistory() {
        viewModelScope.launch {
            Log.d("K360GameViewModel", "Fetching K3 Game History")
            if (!hasGameHistoryData && _k3GameHistory.value !is Resource.Error) { // Only show loading if no data or not already in error
                _k3GameHistory.value = Resource.Loading()
            }

            try {
                val response = repository.getK360GameHistory()
                if (response.isSuccessful) {
                    // Access the 'history' list inside the 'result' object
                    response.body()?.result?.history?.let { history ->
                        val currentData = (_k3GameHistory.value as? Resource.Success)?.data
                        if (currentData == null || !areK3GameHistoriesIdentical(currentData, history)) {
                            _k3GameHistory.value = Resource.Success(history)
                            hasGameHistoryData = true
                            Log.d("K360GameViewModel", "Fetched K3 Game History: ${history.size} items")
                        } else {
                            Log.d("K360GameViewModel", "K3 Game history unchanged, skipping update.")
                        }
                    } ?: run {
                        if (!hasGameHistoryData) {
                            _k3GameHistory.value = Resource.Error("Empty history response")
                        }
                        Log.e("K360GameViewModel", "K3 Game history response body is null or result.history is null")
                    }
                } else {
                    if (!hasGameHistoryData) {
                        _k3GameHistory.value = Resource.Error("Failed to fetch K3 game history: ${response.code()} - ${response.message()}")
                    }
                    Log.e("K360GameViewModel", "Failed to fetch K3 game history: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                if (!hasGameHistoryData) {
                    _k3GameHistory.value = Resource.Error("Error fetching K3 game history: ${e.message}")
                }
                Log.e("K360GameViewModel", "Error fetching K3 game history", e)
            }
        }
    }

    private fun areK3GameHistoriesIdentical(old: List<K360GameHistoryItem>, new: List<K360GameHistoryItem>): Boolean {
        if (old.size != new.size) return false
        if (old.isEmpty()) return true
        return old.first().datetime == new.first().datetime // Compare by datetime for simplicity
    }

    fun fetchK3MyHistory() {
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
            Log.d("K360GameViewModel", "Fetching K3 My History for User ID: $userId")

            if (userId == "0") {
                _k3MyHistory.value = Resource.Error("User not logged in")
                return@launch
            }

            if (!hasMyHistoryData && _k3MyHistory.value !is Resource.Error) { // Only show loading if no data or not already in error
                _k3MyHistory.value = Resource.Loading()
            }

            try {
                val response = repository.getK360MyHistory(userId)
                if (response.isSuccessful) {
                    response.body()?.result?.history?.let { history ->
                        _k3MyHistory.value = Resource.Success(history)
                        hasMyHistoryData = true
                        Log.d("K360GameViewModel", "Fetched K3 My History: ${history.size} items")
                    } ?: run {
                        if (!hasMyHistoryData) {
                            _k3MyHistory.value = Resource.Error("Empty history response")
                        }
                        Log.e("K360GameViewModel", "K3 My history response body is null or result is null")
                    }
                } else {
                    if (!hasMyHistoryData) {
                        _k3MyHistory.value = Resource.Error("Failed to fetch K3 my history: ${response.code()} - ${response.message()}")
                    }
                    Log.e("K360GameViewModel", "Failed to fetch K3 my history: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                if (!hasMyHistoryData) {
                    _k3MyHistory.value = Resource.Error("Error fetching K3 my history: ${e.message}")
                }
                Log.e("K360GameViewModel", "Error fetching K3 my history", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        gameHistoryPollingJob?.cancel()
    }
}
