package com.weblite.kgf.Api2

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WingoTimerService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: UserRepository
) {

    private val _timeRemaining = MutableStateFlow(30)
    val timeRemaining: StateFlow<Int> = _timeRemaining

    private val _currentPeriod = MutableStateFlow("2507021733")
    val currentPeriod: StateFlow<String> = _currentPeriod

    // Timer cycle completion callback
    private val _onTimerCycleComplete = MutableStateFlow<Boolean>(false)
    val onTimerCycleComplete: StateFlow<Boolean> = _onTimerCycleComplete

    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("wingo_period", Context.MODE_PRIVATE)
    }

    init {
        // Don't start timer automatically - wait for screen navigation
    }

//    private fun startCountdownTimer() {
//        timerJob?.cancel()
//        timerJob = serviceScope.launch {
//            while (true) {
//                val (seconds, periodId) = fetchCountdownData()
//
//                val count = if (seconds != null) {
//                    val secValue = seconds.toIntOrNull() ?: 0
//                    val calculatedCount = if (secValue >= 30) {
//                        60 - secValue + 1
//                    } else {
//                        31 - secValue
//                    }
//                    Log.d("WingoTimer", "API seconds: $secValue, calculated count: $calculatedCount")
//                    calculatedCount
//                } else {
//                    Log.d("WingoTimer", "API failed, using default count: 30")
//                    30
//                }
//
//
//
//                // Countdown from calculated count to 0
//                for (countdown in count downTo 0) {
//                    _timeRemaining.value = countdown
//                    delay(1000) // Wait 1 second
//                }
//
//                // Timer cycle completed, trigger callback
//                _onTimerCycleComplete.value = !_onTimerCycleComplete.value
//
//                // Small delay before starting next cycle
//                delay(100)
//            }
//        }
//    }

//    if (periodId != null) {
//        _currentPeriod.value = periodId
//        sharedPrefs.edit()
//            .putString("latest_period_id", periodId)
//            .apply()
//    }

//    private suspend fun fetchCountdownData(): Pair<String?, String?> = coroutineScope {
//        val timeResponse = async { repository.getCountdownTime() }
//        val rowResponse = async { repository.getLatestRow() }
//
//        val timeResult = timeResponse.await()
//        val rowResult = rowResponse.await()
//
//        // ... rest of your code
//        val seconds = if (timeResult.isSuccessful) {
//            timeResult.body()?.seconds
//        } else {
//            Log.e("WingoTimer", "Time API failed: ${timeResult.code()}")
//            null
//        }
//
//        val periodId = if (rowResult.isSuccessful) {
//            rowResult.body()?.latestPeriodId
//        } else {
//            Log.e("WingoTimer", "Row API failed: ${rowResult.code()}")
//            null
//        }
//
//        Log.d("WingoTimer", "Fetched - seconds: $seconds, periodId: $periodId")
//
//        Pair(seconds, periodId)
//    }

    fun updatePeriod(newPeriod: String) {
        _currentPeriod.value = newPeriod
    }

    fun forceReset() {
        _timeRemaining.value = 30
    }

    fun destroy() {
        timerJob?.cancel()
        serviceScope.cancel()
    }

//    fun startTimer() {
//        if (timerJob?.isActive != true) {
//            startCountdownTimer()
//        }
//    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    // Call this when navigating to game screen
//    suspend fun fetchInitialData() {
//        val (seconds, periodId) = fetchCountdownData()
//        if (periodId != null) {
//            _currentPeriod.value = periodId
//        }
//    }
}
