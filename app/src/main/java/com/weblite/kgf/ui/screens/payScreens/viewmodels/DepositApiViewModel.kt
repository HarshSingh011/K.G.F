package com.weblite.kgf.ui.screens.payScreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.data.model.DepositRequest
import com.weblite.kgf.data.model.DepositResponse
import com.weblite.kgf.data.repository.UpiQrRepository
import com.weblite.kgf.Api2.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DepositApiViewModel @Inject constructor(
    private val repository: UpiQrRepository
) : ViewModel() {
    private val _depositResponse = MutableStateFlow<DepositResponse?>(null)
    val depositResponse: StateFlow<DepositResponse?> = _depositResponse.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun submitDeposit(amount: Int, utr: String) {
        // Use the same key as used for saving the phone number in SharedPrefManager
        val mobile = SharedPrefManager.getString("PHONE_NUMBER", "") ?: ""
        if (mobile.isBlank()) {
            _error.value = "Mobile number not found in SharedPreferences."
            return
        }
        _loading.value = true
        viewModelScope.launch {
            try {
                val request = DepositRequest(amount = amount, utr_no = utr, mobile = mobile)
                val response = repository.storeDeposit(request)
                if (response.isSuccessful) {
                    _depositResponse.value = response.body()
                    _error.value = null
                } else {
                    _error.value = response.errorBody()?.string() ?: "Unknown error"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }
}
