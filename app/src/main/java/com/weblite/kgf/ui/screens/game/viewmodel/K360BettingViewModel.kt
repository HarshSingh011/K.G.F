package com.weblite.kgf.ui.screens.game.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.data.repository.GameRespsitories.K360GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class K360BettingViewModel @Inject constructor(
    private val repository: K360GameRepository
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

            Log.d("K360BettingViewModel", "=== PLACING K3 BET ===")
            Log.d("K360BettingViewModel", "User ID: $userId")
            Log.d("K360BettingViewModel", "Bet Type: $bidType")
            Log.d("K360BettingViewModel", "Bet Number/Type: $bidNum")
            Log.d("K360BettingViewModel", "Period: $period")
            Log.d("K360BettingViewModel", "Quantity: $quantity")
            Log.d("K360BettingViewModel", "Price: $price")
            Log.d("K360BettingViewModel", "===================")

            if (userId == "0") {
                Result.failure(Exception("User not logged in"))
            } else {
                val response = repository.placeK360Bet(
                    userid = userId,
                    bidNum = bidNum,
                    bidType = bidType,
                    period = period,
                    quantity = quantity,
                    price = price
                )

                if (response.isSuccessful) {
                    response.body()?.let { betResponse ->
                        if (betResponse.status == "success") {
                            Log.d("K360BettingViewModel", "K3 Bet placed successfully: ${betResponse.msg}")
                            Result.success(betResponse.msg)
                        } else {
                            Log.e("K360BettingViewModel", "K3 Bet failed: ${betResponse.msg}")
                            Result.failure(Exception(betResponse.msg))
                        }
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Log.e("K360BettingViewModel", "K3 Bet placement failed: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("K3 Bet placement failed: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Log.e("K360BettingViewModel", "Error placing K3 bet", e)
            Result.failure(e)
        }
    }
}
