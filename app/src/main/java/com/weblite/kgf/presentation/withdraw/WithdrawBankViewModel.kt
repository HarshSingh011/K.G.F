package com.weblite.kgf.presentation.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.data.withdraw.BankDetailsResult
import com.weblite.kgf.data.withdraw.BankDetailsState
import com.weblite.kgf.data.withdraw.WithdrawApiProvider
import com.weblite.kgf.data.withdraw.BankDetailsApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WithdrawBankViewModel : ViewModel() {
    private val _state = MutableStateFlow(BankDetailsState())
    val state: StateFlow<BankDetailsState> = _state

    private val _addBankLoading = MutableStateFlow(false)
    val addBankLoading: StateFlow<Boolean> = _addBankLoading
    private val _addBankError = MutableStateFlow<String?>(null)
    val addBankError: StateFlow<String?> = _addBankError

    fun addBankAccount(
        bank: String,
        recipientName: String,
        accountNumber: String,
        phoneNumber: String,
        ifscCode: String,
        userId: String
    ) {
        _addBankLoading.value = true
        _addBankError.value = null
        viewModelScope.launch {
            try {
                val response = com.weblite.kgf.data.withdraw.AddBankAccountRequest(
                    bank = bank,
                    recipient_name = recipientName,
                    account_number = accountNumber,
                    phone_number = phoneNumber,
                    ifsc_code = ifscCode,
                    user_id = userId
                )
                val result = WithdrawApiProvider.api.addBankAccount(response)
                if (result.status == "success") {
                    fetchBankDetails(userId)
                } else {
                    _addBankError.value = result.msg ?: "Failed to add bank account"
                }
            } catch (e: Exception) {
                _addBankError.value = e.message
            } finally {
                _addBankLoading.value = false
            }
        }
    }

    fun fetchBankDetails(userId: String) {
        _state.value = BankDetailsState(loading = true)
        viewModelScope.launch {
            try {
                val response: BankDetailsApiResponse = WithdrawApiProvider.api.getBankDetails(userId)
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
