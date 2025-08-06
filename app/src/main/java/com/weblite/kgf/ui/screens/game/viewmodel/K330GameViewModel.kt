package com.weblite.kgf.ui.screens.game.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.data.repository.GameRespsitories.K330GameRepository
import com.weblite.kgf.data.K360GameHistoryItem
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
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class K330GameViewModel @Inject constructor(
    private val repository: K330GameRepository
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
        // Removed polling: fetchK3GameHistory will be called only at correct times
    }

    fun setActiveHistoryTab(tab: String) {
        _activeHistoryTab.value = tab
        if (tab == "My History") {
            fetchK3MyHistory()
        } else {
            fetchK3GameHistory()
        }
    }

    private fun fetchK3PeriodIdAndStartTimer() {
        timerJob?.cancel()
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
            if (userId == "0") {
                Log.e("K330GameViewModel", "User ID not found in SharedPreferences. Cannot fetch period ID.")
                _k3PeriodId.value = "N/A"
                _k3TimeRemaining.value = 0L
                return@launch
            }

            try {
                val response = repository.getThirtySecondPeriodIDForK3(userId)
                if (response.isSuccessful) {
                    response.body()?.result?.let { result ->
                        _k3PeriodId.value = result.periodId

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val serverCurrentTimeMillis = try {
                            dateFormat.parse(result.currentTime)?.time ?: System.currentTimeMillis()
                        } catch (e: ParseException) {
                            Log.e("K330GameViewModel", "Error parsing current_time date string: ${result.currentTime}", e)
                            System.currentTimeMillis()
                        }

                        val periodDurationSeconds = result.time ?: 30
                        val periodDurationMillis = periodDurationSeconds.toLong() * 1000L

                        val periodEndTimeMillis = serverCurrentTimeMillis + periodDurationMillis

                        val remaining = (periodEndTimeMillis - System.currentTimeMillis()).coerceAtLeast(0L)

                        _k3TimeRemaining.value = remaining
                        Log.d("K330GameViewModel", "K330 Period ID: ${result.periodId}, Initial Time Remaining: ${remaining / 1000}s")

                        if (_k3TimeRemaining.value <= 0) {
                            Log.d("K330GameViewModel", "Timer already expired or very low, re-fetching immediately for next period.")
                            delay(500)
                            fetchK3PeriodIdAndStartTimer()
                        } else {
                            // Fetch dice/game history at the start of each period
                            fetchK3GameHistory()
                            startTimerCountdown()
                        }
                    } ?: run {
                        Log.e("K330GameViewModel", "Failed to fetch K330 period ID: Empty response body or result is null")
                        _k3PeriodId.value = "Error"
                        _k3TimeRemaining.value = 0L
                    }
                } else {
                    Log.e("K330GameViewModel", "Failed to fetch K330 period ID: ${response.code()} - ${response.message()}")
                    _k3PeriodId.value = "Error"
                    _k3TimeRemaining.value = 0L
                }
            } catch (e: Exception) {
                Log.e("K330GameViewModel", "Error fetching K330 period ID", e)
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
            Log.d("K330GameViewModel", "Timer reached 0.")
            
            // Fetch popup history for the period that just ended
            Log.d("K3WinDialog", "Calling fetchK330PopupHistory after timer completion")
            fetchK330PopupHistory()

            // Then, fetch the new period ID and restart the timer
            Log.d("K330GameViewModel", "Fetching new period ID and restarting timer.")
            fetchK3PeriodIdAndStartTimer()
            
            if (_activeHistoryTab.value == "My History") {
                fetchK3MyHistory()
            }
        }
    }

    // Removed polling job. No longer needed.

    // Removed polling job. No longer needed.

    fun fetchK3GameHistory() {
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
            if (userId == "0") {
                _k3GameHistory.value = Resource.Error("User not logged in")
                return@launch
            }

            Log.d("K330GameViewModel", "Fetching K330 Game History")
            if (!hasGameHistoryData && _k3GameHistory.value !is Resource.Error) {
                _k3GameHistory.value = Resource.Loading()
            }

            try {
                val response = repository.getK330GameHistory(userId)
                if (response.isSuccessful) {
                    response.body()?.result?.history?.let { history ->
                        val transformedHistory = history.map { item ->
                            val number = if (item.bidNum.isNullOrBlank()) "Loading..." else item.bidNum
                            val bigSmall = if (item.bidBigSmall.isNullOrBlank() && item.finalBidBigSmall.isNullOrBlank()) "Loading..." else (item.bidBigSmall ?: item.finalBidBigSmall ?: "")
                            val oddEven = if (item.bidOddEven.isNullOrBlank() && item.finalBidOddEven.isNullOrBlank()) "Loading..." else (item.bidOddEven ?: item.finalBidOddEven ?: "")
                            K360GameHistoryItem(
                                id = item.id ?: "",
                                datetime = item.period ?: "",
                                number = number ?: "Loading...",
                                oddEven = oddEven ?: "Loading...",
                                bigSmall = bigSmall ?: "Loading...",
                                status = item.status ?: "Loading...",
                                createdAt = item.currentDt ?: "Loading..."
                            )
                        }

                        val currentData = (_k3GameHistory.value as? Resource.Success)?.data
                        if (currentData == null || !areK3GameHistoriesIdentical(currentData, transformedHistory)) {
                            _k3GameHistory.value = Resource.Success(transformedHistory)
                            hasGameHistoryData = true
                            Log.d("K330GameViewModel", "Fetched K330 Game History: ${transformedHistory.size} items")
                        } else {
                            Log.d("K330GameViewModel", "K330 Game history unchanged, skipping update.")
                        }
                    } ?: run {
                        if (!hasGameHistoryData) {
                            _k3GameHistory.value = Resource.Error("Empty history response")
                        }
                        Log.e("K330GameViewModel", "K330 Game history response body is null or result.history is null")
                    }
                } else {
                    if (!hasGameHistoryData) {
                        _k3GameHistory.value = Resource.Error("Failed to fetch K330 game history: ${response.code()} - ${response.message()}")
                    }
                    Log.e("K330GameViewModel", "Failed to fetch K330 game history: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                if (!hasGameHistoryData) {
                    _k3GameHistory.value = Resource.Error("Error fetching K330 game history: ${e.message}")
                }
                Log.e("K330GameViewModel", "Error fetching K330 game history", e)
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
            Log.d("K330GameViewModel", "Fetching K330 My History for User ID: $userId")

            if (userId == "0") {
                _k3MyHistory.value = Resource.Error("User not logged in")
                return@launch
            }

            if (!hasMyHistoryData && _k3MyHistory.value !is Resource.Error) {
                _k3MyHistory.value = Resource.Loading()
            }

            try {
                val response = repository.getK330MyHistory(userId)
                if (response.isSuccessful) {
                    response.body()?.result?.history?.let { history ->
                        val transformedHistory = history.map { item ->
                            K360MyHistoryItem(
                                k60beatID = item.id ?: "",
                                period = item.period ?: "",
                                bidNum = item.bidNum ?: "",
                                price = item.price ?: "",
                                status = item.status ?: "",
                                totalamount = item.totalamount ?: "",
                                bidType = item.bidType ?: "",
                                quantity = item.quantity ?: "",
                                betChoiceStatus = item.betChoiceStatus ?: "",
                                betChoiceResult = item.betChoiceResult ?: "",
                                winning_amount = item.calculatedTotalAmount?.toString() ?: "",
                                adminWinStatus = item.adminWinStatus ?: "",
                                adminWinBid = item.adminWinBid ?: "",
                                adminWinTotal = item.adminWinTotal ?: "",
                                createdAt = item.createdAt ?: "",
                                boost = item.boost ?: "",
                                userid = item.userid ?: ""
                            )
                        }
                        _k3MyHistory.value = Resource.Success(transformedHistory)
                        hasMyHistoryData = true
                        Log.d("K330GameViewModel", "Fetched K330 My History: ${transformedHistory.size} items")
                    } ?: run {
                        if (!hasMyHistoryData) {
                            _k3MyHistory.value = Resource.Error("Empty history response")
                        }
                        Log.e("K330GameViewModel", "K330 My history response body is null or result is null")
                    }
                } else {
                    if (!hasMyHistoryData) {
                        _k3MyHistory.value = Resource.Error("Failed to fetch K330 my history: ${response.code()} - ${response.message()}")
                    }
                    Log.e("K330GameViewModel", "Failed to fetch K330 my history: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                if (!hasMyHistoryData) {
                    _k3MyHistory.value = Resource.Error("Error fetching K330 my history: ${e.message}")
                }
                Log.e("K330GameViewModel", "Error fetching K330 my history", e)
            }
        }
    }

    // --- K3 30 Popup History Function ---
    fun fetchK330PopupHistory() {
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
            Log.d("K3WinDialog", "Calling getK330PopupHistory with userId: $userId")
            try {
                val response = repository.getK330PopupHistory(userId)
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
                Log.e("K3WinDialog", "Exception in fetchK330PopupHistory: ${e.message}", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        gameHistoryPollingJob?.cancel()
    }
}
