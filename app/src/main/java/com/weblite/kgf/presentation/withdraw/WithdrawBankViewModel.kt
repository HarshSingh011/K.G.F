package com.weblite.kgf.presentation.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.data.withdraw.BankDetailsResult
import com.weblite.kgf.data.withdraw.BankDetailsState
import com.weblite.kgf.data.withdraw.WithdrawBankApiProvider
import com.weblite.kgf.data.withdraw.BankDetailsApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WithdrawBankViewModel : ViewModel() {
    private val _state = MutableStateFlow(BankDetailsState())
    val state: StateFlow<BankDetailsState> = _state

    fun fetchBankDetails(userId: String) {
        _state.value = BankDetailsState(loading = true)
        viewModelScope.launch {
            try {
                val response: BankDetailsApiResponse = WithdrawBankApiProvider.api.getBankDetails(userId)
                if (response.status == "success" && response.result != null) {
                    _state.value = BankDetailsState(result = response.result)
                } else {
                    _state.value = BankDetailsState(error = response.msg ?: "Unknown error")
                }
            } catch (e: Exception) {
                _state.value = BankDetailsState(error = e.message)
            }
        }
    }
}
