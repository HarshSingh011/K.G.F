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
import com.weblite.kgf.data.withdraw.UpdateAccountDetailsRequest
import com.weblite.kgf.data.withdraw.WithdrawApiProvider.api

class WithdrawBankViewModel : ViewModel() {
    // Update bank details (edit mode)
    suspend fun updateAccountDetails(
        userId: String,
        bank: String,
        recipientName: String,
        accountNumber: String,
        phoneNumber: String,
        ifscCode: String
    ) {
        _addBankLoading.value = true
        _addBankError.value = null
        try {
            // Always use Bank_User_Sl_Id if present
            val bankUserSlId = com.weblite.kgf.Api.SharedPrefManager.getString("Bank_User_Sl_Id")
            val finalUserId = if (!bankUserSlId.isNullOrBlank()) bankUserSlId else userId
            val request = UpdateAccountDetailsRequest(
                user_id = finalUserId,
                bank = bank,
                recipient_name = recipientName,
                account_number = accountNumber,
                phone_number = phoneNumber,
                ifsc_code = ifscCode
            )
            val response = api.updateAccountDetails(request)
            if (!response.status) {
                _addBankError.value = response.message
            }
        } catch (e: Exception) {
            _addBankError.value = e.message
        } finally {
            _addBankLoading.value = false
        }
    }
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
                val request = com.weblite.kgf.data.withdraw.AddBankAccountRequest(
                    bank = bank,
                    recipient_name = recipientName,
                    account_number = accountNumber,
                    phone_number = phoneNumber,
                    ifsc_code = ifscCode,
                    user_id = userId
                )
                val result = WithdrawApiProvider.api.addBankAccount(request)
                if (result.status == "success") {
                    // Save user_sl to SharedPrefManager if present
                    val userSl = result.result?.user_sl
                    if (!userSl.isNullOrEmpty()) {
                        try {
                            com.weblite.kgf.Api.SharedPrefManager.setString("Bank_User_Sl_Id", userSl)
                        } catch (e: Exception) {
                            android.util.Log.e("WithdrawBankViewModel", "Failed to save Bank_User_Sl_Id: ${e.message}")
                        }
                    }
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
                    // Save user_sl to SharedPrefManager if present
                    val userSl = response.result.user_sl
                    if (!userSl.isNullOrEmpty()) {
                        try {
                            com.weblite.kgf.Api.SharedPrefManager.setString("Bank_User_Sl_Id", userSl)
                        } catch (e: Exception) {
                            android.util.Log.e("WithdrawBankViewModel", "Failed to save Bank_User_Sl_Id from getBankDetails: ${e.message}")
                        }
                    }
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
