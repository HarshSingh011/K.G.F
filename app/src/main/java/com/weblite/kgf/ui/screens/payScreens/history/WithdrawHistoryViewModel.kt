package com.weblite.kgf.ui.screens.payScreens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.data.withdraw.WithdrawHistoryApiProvider
import com.weblite.kgf.data.withdraw.WithdrawHistoryItem
import com.weblite.kgf.data.withdraw.WithdrawHistoryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WithdrawHistoryViewModel : ViewModel() {
    private val _history = MutableStateFlow<List<WithdrawHistoryItem>>(emptyList())
    val history: StateFlow<List<WithdrawHistoryItem>> = _history

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchWithdrawHistory(userId: String) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = WithdrawHistoryApiProvider.api.getWithdrawHistory(userId)
                if (response.status_code == 200 && response.result != null) {
                    _history.value = response.result.values.toList().sortedByDescending { it.created_at }
                } else {
                    _error.value = response.msg ?: "Unknown error"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
