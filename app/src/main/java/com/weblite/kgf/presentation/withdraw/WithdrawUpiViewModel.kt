package com.weblite.kgf.presentation.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.data.withdraw.UpiDetailsState
import com.weblite.kgf.data.withdraw.WithdrawApiProvider
import com.weblite.kgf.data.withdraw.UpiDetailsApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WithdrawUpiViewModel : ViewModel() {
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
                val response: com.weblite.kgf.data.withdraw.UpiDetailsApiResponse = com.weblite.kgf.data.withdraw.WithdrawUpiApiProvider.api.getUpiDetails(userId)
                if (response.status == "success" && response.result != null) {
                    _state.value = UpiDetailsState(result = response.result)
                } else {
                    _state.value = UpiDetailsState(error = response.msg ?: "Unknown error")
                }
            } catch (e: Exception) {
                _state.value = UpiDetailsState(error = e.message)
            }
        }
    }

    fun updateUpiDetails(userName: String, upiId: String, upiProvider: String, userId: String, onSuccess: () -> Unit = {}) {
        _updateLoading.value = true
        _updateError.value = null
        viewModelScope.launch {
            try {
                val response = com.weblite.kgf.presentation.withdraw.UpiDetailsApiProvider.api.updateUpiDetails(
                    com.weblite.kgf.presentation.withdraw.UpiDetailsRequest(
                        userName = userName,
                        upi_id = upiId,
                        upiProvider = upiProvider,
                        user_id = userId
                    )
                )
                if (response.status == "success") {
                    onSuccess()
                    fetchUpiDetails(userId)
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
