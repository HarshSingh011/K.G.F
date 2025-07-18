package com.weblite.kgf.ui.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.data.Wingo60GameRepository
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

    private var timerJob: Job? = null

    init {
        // This ensures the API is called and timer is set when the ViewModel is first created
        // (i.e., when navigating to the screen).
        fetchPeriodIdAndStartTimer()
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel() // Ensure timer is cancelled when ViewModel is destroyed
    }
}
