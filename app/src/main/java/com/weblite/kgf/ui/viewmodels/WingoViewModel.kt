//package com.weblite.kgf.ui
//import android.app.Application
//import android.util.Log
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.SharedFlow
//import dagger.hilt.android.lifecycle.HiltViewModel
//import com.weblite.kgf.Api2.WingoTimerService
//import com.weblite.kgf.Api2.SharedPrefManager
//import com.weblite.kgf.Api2.UserRepository
//import com.weblite.kgf.Api2.Resource
//import com.weblite.kgf.Api2.PeriodIdUIEvent
//import com.weblite.kgf.data.PeriodIdResponse
//import javax.inject.Inject
//
//// Data classes for API responses
//data class GameHistoryItem(
//    val id: String,
//    val datetime: String,
//    val number_result: String,
//    val color_result: String,
//    val big_small_result: String,
//    val current: String
//)
//
//data class GameHistoryResponse(
//    val status: String,
//    val result: GameHistoryResult
//)
//
//data class GameHistoryResult(
//    val history: List<GameHistoryItem>
//)
//
//data class MyHistoryItem(
//    val id: String,
//    val bidNum: String,
//    val bidType: String,
//    val period: String,
//    val price: String,
//    val quantity: String,
//    val resultStatus: String,
//    val winning_amount: String
//)
//
//data class MyHistoryResponse(
//    val status: String,
//    val result: MyHistoryResult
//)
//
//data class MyHistoryResult(
//    val history: List<MyHistoryItem>
//)
//
//
//
//data class BetResult(
//    val userid: String,
//    val bidNum: String,
//    val bidType: String,
//    val period: String,
//    val price: String,
//    val quantity: String,
//    val created_at: String
//)
//
//
//
//@HiltViewModel
//class WingoGameViewModel @Inject constructor(
//    application: Application,
//    private val timerService: WingoTimerService,
//    private val repository: UserRepository
//) : AndroidViewModel(application) {
//
//    // Timer states from service - these are always updated
//    val timeRemaining: StateFlow<Int> = timerService.timeRemaining
//    val currentPeriod: StateFlow<String> = timerService.currentPeriod
//
//    // Game History State
//    private val _gameHistoryResponse = MutableStateFlow<Resource<GameHistoryResponse>?>(null)
//    val gameHistoryResponse: StateFlow<Resource<GameHistoryResponse>?> = _gameHistoryResponse
//
//    // My History State
//    private val _myHistoryResponse = MutableStateFlow<Resource<MyHistoryResponse>?>(null)
//    val myHistoryResponse: StateFlow<Resource<MyHistoryResponse>?> = _myHistoryResponse
//
//    // Period ID events
//    private val _periodId = MutableSharedFlow<PeriodIdUIEvent>()
//    val periodId: SharedFlow<PeriodIdUIEvent> = _periodId
//
//    // Polling jobs
//    private var gameHistoryJob: Job? = null
//    private var isGameHistoryActive = true
//
//    init {
//        // Log initialization
//        val userId = SharedPrefManager.getString("user_id", "not_found")
//        val currentPeriod = timerService.currentPeriod.value
//        Log.d("WingoGameVM", "ViewModel initialized with User ID: $userId")
//        Log.d("WingoGameVM", "ViewModel initialized with Period: $currentPeriod")
//
//        // Start game history polling when ViewModel is created
//        startGameHistoryPolling()
//
//        // Observe period changes and timer cycle completions
//        viewModelScope.launch {
//            timerService.currentPeriod.collect { period ->
//                Log.d("WingoGameVM", "Period changed to: $period")
//                // Refresh data when period changes
//                fetchGameHistory()
//                fetchMyHistory()
//            }
//        }
//
//        // Listen for timer cycle completions
//        viewModelScope.launch {
//            timerService.onTimerCycleComplete.collect {
//                Log.d("WingoGameVM", "Timer cycle completed, refreshing data")
//                fetchGameHistory()
//                fetchMyHistory()
//            }
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        // Don't stop the timer service as it should continue running
//        // Just cancel our polling jobs
//        gameHistoryJob?.cancel()
//        Log.d("WingoGameVM", "ViewModel cleared, polling stopped")
//    }
//
//    // Game History Polling Management
//    private fun startGameHistoryPolling() {
//        gameHistoryJob?.cancel()
//        isGameHistoryActive = true
//        gameHistoryJob = viewModelScope.launch {
//            Log.d("WingoGameVM", "Starting Game History polling")
//            while (isGameHistoryActive) {
//                fetchGameHistory()
//                delay(3000) // Poll every 3 seconds
//            }
//            Log.d("WingoGameVM", "Game History polling stopped")
//        }
//    }
//
//    private fun stopGameHistoryPolling() {
//        Log.d("WingoGameVM", "Stopping Game History polling")
//        isGameHistoryActive = false
//        gameHistoryJob?.cancel()
//        gameHistoryJob = null
//    }
//
//    // Tab Selection Handlers
//    fun onGameHistoryTabSelected() {
//        Log.d("WingoGameVM", "Game History tab selected")
//        stopGameHistoryPolling()
//        startGameHistoryPolling()
//    }
//
//    fun onMyHistoryTabSelected() {
//        Log.d("WingoGameVM", "My History tab selected")
//        stopGameHistoryPolling()
//        fetchMyHistory()
//    }
//
//    // Fetch Game History using Repository
//    fun fetchGameHistory() {
//        viewModelScope.launch {
//            _gameHistoryResponse.value = Resource.Loading()
//            try {
//                val currentPeriod = timerService.currentPeriod.value
//                Log.d("WingoGameVM", "Fetching Game History for period: $currentPeriod")
//
//                val response = repository.getWingo30SecGameHistory()
//                if (response.isSuccessful) {
//                    response.body()?.let { gameHistoryResponse ->
//                        _gameHistoryResponse.value = Resource.Success(gameHistoryResponse)
//                        Log.d("WingoGameVM", "Game history fetched successfully: ${gameHistoryResponse.result.history.size} items")
//                    } ?: run {
//                        _gameHistoryResponse.value = Resource.Error("Empty response")
//                        Log.e("WingoGameVM", "Game history response body is null")
//                    }
//                } else {
//                    _gameHistoryResponse.value = Resource.Error("Server error: ${response.code()}")
//                    Log.e("WingoGameVM", "Game history API failed: ${response.code()} - ${response.message()}")
//                }
//            } catch (e: Exception) {
//                _gameHistoryResponse.value = Resource.Error("Exception: ${e.message}")
//                Log.e("WingoGameVM", "Error fetching game history: ${e.message}", e)
//
//                // Set mock data for testing if API fails
//                setMockGameHistory()
//            }
//        }
//    }
//
//    // Fetch My History using new API endpoint
//    fun fetchMyHistory() {
//        viewModelScope.launch {
//            _myHistoryResponse.value = Resource.Loading()
//            try {
//                val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
//                val currentPeriod = timerService.currentPeriod.value
//
//                Log.d("WingoGameVM", "=== FETCHING MY HISTORY ===")
//                Log.d("WingoGameVM", "User ID: $userId")
//                Log.d("WingoGameVM", "Current Period: $currentPeriod")
//                Log.d("WingoGameVM", "API: https://newkgfindia.com/web/Api/wingo30secMyHistory?user_id=$userId")
//                Log.d("WingoGameVM", "================================")
//
//                if (userId == "0") {
//                    _myHistoryResponse.value = Resource.Error("User not logged in")
//                    return@launch
//                }
//
//                val response = repository.getWingo30SecMyHistory(userId)
//                if (response.isSuccessful) {
//                    response.body()?.let { myHistoryResponse ->
//                        _myHistoryResponse.value = Resource.Success(myHistoryResponse)
//                        Log.d("WingoGameVM", "My history fetched successfully: ${myHistoryResponse.result.history.size} items")
//                    } ?: run {
//                        _myHistoryResponse.value = Resource.Error("Empty response")
//                        Log.e("WingoGameVM", "My history response body is null")
//                    }
//                } else {
//                    _myHistoryResponse.value = Resource.Error("Server error: ${response.code()}")
//                    Log.e("WingoGameVM", "My history API failed: ${response.code()} - ${response.message()}")
//                }
//            } catch (e: Exception) {
//                _myHistoryResponse.value = Resource.Error("Exception: ${e.message}")
//                Log.e("WingoGameVM", "Error fetching my history: ${e.message}", e)
//
//                // Set mock data for testing if API fails
//                setMockMyHistory()
//            }
//        }
//    }
//
//    // Place Bet using Repository
//    suspend fun placeBet(
//        bidNum: String,
//        bidType: String,
//        quantity: String,
//        price: String
//    ): Result<String> {
//        return try {
//            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
//            val currentPeriod = timerService.currentPeriod.value
//
//            Log.d("WingoGameVM", "=== PLACING BET ===")
//            Log.d("WingoGameVM", "User ID: $userId")
//            Log.d("WingoGameVM", "Period/DateTime: $currentPeriod")
//            Log.d("WingoGameVM", "Bet Type: $bidType")
//            Log.d("WingoGameVM", "Bet Number/Color: $bidNum")
//            Log.d("WingoGameVM", "Quantity: $quantity")
//            Log.d("WingoGameVM", "Price: $price")
//            Log.d("WingoGameVM", "===================")
//
//            if (userId == "0") {
//                Result.failure(Exception("User not logged in"))
//            } else {
//                val response = repository.placeBet(
//                    userid = userId,
//                    bidNum = bidNum,
//                    bidType = bidType,
//                    period = currentPeriod,
//                    quantity = quantity,
//                    price = price
//                )
//
//                if (response.isSuccessful) {
//                    response.body()?.let { betResponse ->
//                        if (betResponse.status == "success") {
//                            Log.d("WingoGameVM", "Bet placed successfully: ${betResponse.msg}")
//                            // Refresh my history after successful bet
//                            fetchMyHistory()
//                            Result.success(betResponse.msg)
//                        } else {
//                            Log.e("WingoGameVM", "Bet failed: ${betResponse.msg}")
//                            Result.failure(Exception(betResponse.msg))
//                        }
//                    } ?: Result.failure(Exception("Empty response"))
//                } else {
//                    Log.e("WingoGameVM", "Bet placement failed: ${response.code()} - ${response.message()}")
//                    Result.failure(Exception("Bet placement failed: ${response.message()}"))
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("WingoGameVM", "Error placing bet", e)
//            Result.failure(e)
//        }
//    }
//
//    // Fetch Period ID
//    fun fetchPeriodId() {
//        viewModelScope.launch {
//            try {
//                val result = repository.getPeriodId()
//                result.fold(
//                    onSuccess = { periodResponse ->
//                        _periodId.emit(PeriodIdUIEvent.Success(periodResponse.copy()))
//                        Log.d("WingoGameVM", "Period ID fetched successfully: ${periodResponse.period_id}")
//
//                        // Update timer service with new period if needed
//                        periodResponse.period_id?.let { newPeriod ->
//                            timerService.updatePeriod(newPeriod)
//                        }
//                    },
//                    onFailure = { error ->
//                        _periodId.emit(PeriodIdUIEvent.Failure(error.message ?: "Unknown Error"))
//                        Log.e("WingoGameVM", "Failed to fetch period ID: ${error.message}")
//                    }
//                )
//            } catch (e: Exception) {
//                _periodId.emit(PeriodIdUIEvent.Failure(e.message ?: "Unknown Error"))
//                Log.e("WingoGameVM", "Exception fetching period ID", e)
//            }
//        }
//    }
//
//    // Mock data methods for testing
//    private fun setMockGameHistory() {
//        val mockHistoryList = listOf(
//            GameHistoryItem(
//                id = "1",
//                datetime = "2507130001",
//                number_result = "8",
//                color_result = "RED",
//                big_small_result = "BIG",
//                current = "2025-07-13 01:30:33"
//            ),
//            GameHistoryItem(
//                id = "2",
//                datetime = "2507130002",
//                number_result = "1",
//                color_result = "GREEN",
//                big_small_result = "SMALL",
//                current = "2025-07-13 01:31:03"
//            )
//        )
//
//        _gameHistoryResponse.value = Resource.Success(
//            GameHistoryResponse(
//                status = "success",
//                result = GameHistoryResult(history = mockHistoryList)
//            )
//        )
//
//        Log.d("WingoGameVM", "Using mock game history data")
//    }
//
//    private fun setMockMyHistory() {
//        val mockHistoryList = listOf(
//            MyHistoryItem(
//                id = "1",
//                bidNum = "Green",
//                bidType = "color",
//                period = "2507130001",
//                price = "100",
//                quantity = "1",
//                resultStatus = "Win",
//                winning_amount = "196"
//            ),
//            MyHistoryItem(
//                id = "2",
//                bidNum = "7",
//                bidType = "number",
//                period = "2507130002",
//                price = "50",
//                quantity = "1",
//                resultStatus = "Loss",
//                winning_amount = "0"
//            )
//        )
//
//        _myHistoryResponse.value = Resource.Success(
//            MyHistoryResponse(
//                status = "success",
//                result = MyHistoryResult(history = mockHistoryList)
//            )
//        )
//
//        Log.d("WingoGameVM", "Using mock my history data")
//    }
//
//    // Refresh methods
//    fun refreshGameHistory() {
//        fetchGameHistory()
//    }
//
//    fun refreshMyHistory() {
//        fetchMyHistory()
//    }
//
//    // Debug methods
//    fun logCurrentInfo() {
//        val userId = SharedPrefManager.getString("user_id", "not_found")
//        val currentPeriod = timerService.currentPeriod.value
//        val timeRemaining = timerService.timeRemaining.value
//
//        Log.d("WingoGameVM", "====== CURRENT APP STATE ======")
//        Log.d("WingoGameVM", "User ID: $userId")
//        Log.d("WingoGameVM", "Current Period: $currentPeriod")
//        Log.d("WingoGameVM", "Time Remaining: $timeRemaining seconds")
//        Log.d("WingoGameVM", "Timestamp: ${System.currentTimeMillis()}")
//        Log.d("WingoGameVM", "================================")
//    }
//
//    // Test method for betting API
//    fun testBettingAPI() {
//        viewModelScope.launch {
//            try {
//                val result = placeBet(
//                    bidNum = "Green",
//                    bidType = "color",
//                    quantity = "1",
//                    price = "100"
//                )
//
//                if (result.isSuccess) {
//                    Log.d("WingoGameVM", "Test bet successful: ${result.getOrNull()}")
//                } else {
//                    Log.e("WingoGameVM", "Test bet failed: ${result.exceptionOrNull()?.message}")
//                }
//            } catch (e: Exception) {
//                Log.e("WingoGameVM", "Test bet exception", e)
//            }
//        }
//    }
//}
