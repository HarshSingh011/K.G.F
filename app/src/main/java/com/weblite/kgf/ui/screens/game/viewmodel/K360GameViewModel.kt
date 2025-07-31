package com.weblite.kgf.ui.screens.game.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.data.K360GameHistoryItem
import com.weblite.kgf.data.repository.K360GameRepository
import com.weblite.kgf.data.K360MyHistoryItem
import com.weblite.kgf.data.models.games.K3PopupHistoryResponse
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

    // --- Popup History State ---
    private val _popupHistoryResponse = MutableStateFlow<K3PopupHistoryResponse?>(null)
    val popupHistoryResponse: StateFlow<K3PopupHistoryResponse?> = _popupHistoryResponse

    private val _k3GameHistory = MutableStateFlow<Resource<List<K360GameHistoryItem>>>(Resource.Loading())
    val k3GameHistory: StateFlow<Resource<List<K360GameHistoryItem>>> = _k3GameHistory.asStateFlow()

    private val _k3MyHistory = MutableStateFlow<Resource<List<K360MyHistoryItem>>>(Resource.Loading())
    val k3MyHistory: StateFlow<Resource<List<K360MyHistoryItem>>> = _k3MyHistory.asStateFlow()

    private val _activeHistoryTab = MutableStateFlow("Game History")
    val activeHistoryTab: StateFlow<String> = _activeHistoryTab.asStateFlow()

    private var timerJob: Job? = null
    private var gameHistoryPollingJob: Job? = null
    private var hasGameHistoryData = false
    private var hasMyHistoryData = false

    init {
        fetchK3PeriodIdAndStartTimer()
        startGameHistoryPolling()
    }

    fun setActiveHistoryTab(tab: String) {
        _activeHistoryTab.value = tab
        if (tab == "My History") {
            stopGameHistoryPolling()
            fetchK3MyHistory()
        } else {
            startGameHistoryPolling()
        }
    }

    private fun fetchK3PeriodIdAndStartTimer() {
        timerJob?.cancel()
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0")
            if (userId == "0") {
                Log.e("K360GameViewModel", "User ID not found in SharedPreferences. Cannot fetch period ID.")
                _k3PeriodId.value = "N/A"
                _k3TimeRemaining.value = 0L
                return@launch
            }

            try {
                val response = repository.getK360OneMinPeriodID(userId)
                if (response.isSuccessful) {
                    response.body()?.result?.let { result ->
                        _k3PeriodId.value = result.periodId

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                        val serverCurrentTimeMillis = try {
                            dateFormat.parse(result.currentTime)?.time ?: System.currentTimeMillis()
                        } catch (e: ParseException) {
                            Log.e("K360GameViewModel", "Error parsing current_time date string: ${result.currentTime}", e)
                            System.currentTimeMillis()
                        }

                        val periodDurationSeconds = result.time ?: 60
                        val periodDurationMillis = periodDurationSeconds.toLong() * 1000L

                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = serverCurrentTimeMillis
                            add(Calendar.SECOND, periodDurationSeconds)
                        }
                        val periodEndTimeMillis = calendar.timeInMillis

                        val remaining = (periodEndTimeMillis - System.currentTimeMillis()).coerceAtLeast(0L)

                        _k3TimeRemaining.value = remaining
                        Log.d("K360GameViewModel", "K3 Period ID: ${result.periodId}, Initial Time Remaining: ${remaining / 1000}s")

                        if (_k3TimeRemaining.value <= 0) {
                            Log.d("K360GameViewModel", "Timer already expired or very low, re-fetching immediately for next period.")
                            delay(500)
                            fetchK3PeriodIdAndStartTimer()
                        } else {
                            startTimerCountdown()
                        }
                    } ?: run {
                        Log.e("K360GameViewModel", "Failed to fetch K3 period ID: Empty response body or result is null")
                        _k3PeriodId.value = "Error"
                        _k3TimeRemaining.value = 0L
                    }
                } else {
                    Log.e("K360GameViewModel", "Failed to fetch K3 period ID: ${response.code()} - ${response.message()}")
                    _k3PeriodId.value = "Error"
                    _k3TimeRemaining.value = 0L
                }
            } catch (e: Exception) {
                Log.e("K360GameViewModel", "Error fetching K3 period ID", e)
                _k3PeriodId.value = "Error"
                _k3TimeRemaining.value = 0L
            }
        }
    }

    private fun startTimerCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_k3TimeRemaining.value > 0) {
                delay(1000)
                _k3TimeRemaining.value -= 1000
            }
            Log.d("K360GameViewModel", "Timer reached 0.")
            
            // Fetch popup history for the period that just ended
            Log.d("K3WinDialog", "Calling fetchK360PopupHistory after timer completion")
            fetchK360PopupHistory()

            // Then, fetch the new period ID and restart the timer
            Log.d("K360GameViewModel", "Fetching new period ID and restarting timer.")
            fetchK3PeriodIdAndStartTimer()
            
            if (_activeHistoryTab.value == "My History") {
                fetchK3MyHistory()
            }
        }
    }

    private fun startGameHistoryPolling() {
        gameHistoryPollingJob?.cancel()
        gameHistoryPollingJob = viewModelScope.launch {
            Log.d("K360GameViewModel", "Starting K3 Game History polling")
            while (true) {
                fetchK3GameHistory()
                delay(3000)
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
            if (!hasGameHistoryData && _k3GameHistory.value !is Resource.Error) {
                _k3GameHistory.value = Resource.Loading()
            }

            try {
                val response = repository.getK360GameHistory()
                if (response.isSuccessful) {
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
        return old.first().datetime == new.first().datetime
    }

    fun fetchK3MyHistory() {
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
            Log.d("K360GameViewModel", "Fetching K3 My History for User ID: $userId")

            if (userId == "0") {
                _k3MyHistory.value = Resource.Error("User not logged in")
                return@launch
            }

            if (!hasMyHistoryData && _k3MyHistory.value !is Resource.Error) {
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

    // --- K3 60 Popup History Function ---
    fun fetchK360PopupHistory() {
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
            Log.d("K3WinDialog", "Calling getK360PopupHistory with userId: $userId")
            try {
                val response = repository.getK360PopupHistory(userId)
                response.fold(
                    onSuccess = { data ->
                        if (data == null) {
                            Log.e("K3WinDialog", "API returned null data!")
                        } else {
                            Log.d("Full popup history API response", "${data}")
                            Log.d("K3WinDialog", "Popup history fetched successfully VM: $data")
                        }
                        _popupHistoryResponse.value = data
                    },
                    onFailure = { error ->
                        _popupHistoryResponse.value = null
                        Log.e("K3WinDialog", "Failed to fetch popup history: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _popupHistoryResponse.value = null
                Log.e("K3WinDialog", "Exception in fetchK360PopupHistory: ${e.message}", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        gameHistoryPollingJob?.cancel()
    }
}
