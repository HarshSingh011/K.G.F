package com.weblite.kgf.presentation.withdraw

import com.weblite.kgf.data.withdraw.UpdateUpiDetailsRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.data.withdraw.UpiDetailsState
import com.weblite.kgf.data.withdraw.WithdrawApiProvider
import com.weblite.kgf.data.withdraw.UpiDetailsApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.weblite.kgf.data.models.auth.UpiDetailsRequest
import com.weblite.kgf.data.withdraw.WithdrawApiProvider.api

class WithdrawUpiViewModel : ViewModel() {
    // Update UPI details (edit mode)
    suspend fun updateUpiDetails(
        userId: String,
        name: String,
        upiId: String,
        upiProvider: String
    ) {
        _updateLoading.value = true
        _updateError.value = null
        try {
            // Always use Bank_User_Sl_Id if present
            val bankUserSlId = com.weblite.kgf.Api.SharedPrefManager.getString("Bank_User_Sl_Id")
            val finalUserId = if (!bankUserSlId.isNullOrBlank()) bankUserSlId else userId
            val request = UpdateUpiDetailsRequest(
                user_id = finalUserId,
                name = name,
                upi_id = upiId,
                upi_provider = upiProvider
            )
            val response = api.updateUpiDetails(request)
            if (!response.status) {
                _updateError.value = response.message
            }
        } catch (e: Exception) {
            _updateError.value = e.message
        } finally {
            _updateLoading.value = false
        }
    }
    private val _state = MutableStateFlow(UpiDetailsState())
    val state: StateFlow<UpiDetailsState> = _state

    private val _updateLoading = MutableStateFlow(false)
    val updateLoading: StateFlow<Boolean> = _updateLoading
    private val _updateError = MutableStateFlow<String?>(null)
    val updateError: StateFlow<String?> = _updateError

    fun fetchUpiDetails(userId: String) {
        _state.value = UpiDetailsState(loading = true)
        viewModelScope.launch {
            try {
                val response: com.weblite.kgf.data.withdraw.UpiDetailsApiResponse = com.weblite.kgf.data.withdraw.WithdrawApiProvider.api.getUpiDetails(userId)
                if (response.status == "success" && response.result != null) {
                    // Save user_sl to SharedPrefManager if present
                    val userSl = response.result.user_sl
                    if (!userSl.isNullOrEmpty()) {
                        try {
                            com.weblite.kgf.Api.SharedPrefManager.setString("Bank_User_Sl_Id", userSl)
                        } catch (e: Exception) {
                            android.util.Log.e("WithdrawUpiViewModel", "Failed to save Bank_User_Sl_Id from getUpiDetails: ${e.message}")
                        }
                    }
                    _state.value = UpiDetailsState(result = response.result)
                } else {
                    _state.value = UpiDetailsState(error = response.msg ?: "Unknown error")
                }
            } catch (e: Exception) {
                _state.value = UpiDetailsState(error = e.message)
            }
        }
    }

    fun userUpiDetails(userName: String, upiId: String, upiProvider: String, userId: String, onSuccess: () -> Unit = {}) {
        _updateLoading.value = true
        _updateError.value = null
        viewModelScope.launch {
            try {
                // Always use user_id from SharedPrefManager for userUpiDetails
                val userIdFinal = com.weblite.kgf.Api.SharedPrefManager.getString("USER_ID")
                    ?: com.weblite.kgf.Api.SharedPrefManager.getString("user_id")
                    ?: com.weblite.kgf.Api.SharedPrefManager.getString("id")
                    ?: userId
                val response = WithdrawApiProvider.api.userUpiDetails(
                    com.weblite.kgf.data.models.auth.UpiDetailsRequest(
                        user_id = userIdFinal,
                        userName = userName,
                        upi_id = upiId,
                        upiProvider = upiProvider
                    )
                )
                if (response.status == "success") {
                    onSuccess()
                    fetchUpiDetails(userIdFinal)
                } else {
                    _updateError.value = response.msg ?: "Unknown error"
                }
            } catch (e: Exception) {
                _updateError.value = e.message
            } finally {
                _updateLoading.value = false
            }
        }
    }
}
