package com.weblite.kgf.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.data.K330GameRepository
import com.weblite.kgf.data.GameBetK360
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class K330BettingViewModel @Inject constructor(
    private val repository: K330GameRepository
) : ViewModel() {

    suspend fun placeBet(
        bidNum: String,
        bidType: String,
        period: String,
        quantity: String,
        price: String
    ): Result<String> {
        return try {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"

            Log.d("K330BettingViewModel", "=== PLACING K3 BET (30-sec API) ===")
            Log.d("K330BettingViewModel", "User ID: $userId")
            Log.d("K330BettingViewModel", "Bet Type: $bidType")
            Log.d("K330BettingViewModel", "Bet Number/Type: $bidNum")
            Log.d("K330BettingViewModel", "Period: $period")
            Log.d("K330BettingViewModel", "Quantity: $quantity")
            Log.d("K330BettingViewModel", "Price: $price")
            Log.d("K330BettingViewModel", "===================")

            if (userId == "0") {
                Result.failure(Exception("User not logged in"))
            } else {
                val request = GameBetK360(
                    userid = userId,
                    bidNum = bidNum,
                    bidType = bidType,
                    period = period,
                    quantity = quantity,
                    price = price
                )
                val response = repository.placeBet3Sec(request)

                if (response.isSuccessful) {
                    response.body()?.let { betResponse ->
                        if (betResponse.status == "success") {
                            Log.d("K330BettingViewModel", "K3 Bet placed successfully: ${betResponse.msg}")
                            Result.success(betResponse.msg)
                        } else {
                            Log.e("K330BettingViewModel", "K3 Bet failed: ${betResponse.msg}")
                            Result.failure(Exception(betResponse.msg))
                        }
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Log.e("K330BettingViewModel", "K3 Bet placement failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("K3 Bet placement failed: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Log.e("K330BettingViewModel", "Error placing K3 bet", e)
            Result.failure(e)
        }
    }
}
