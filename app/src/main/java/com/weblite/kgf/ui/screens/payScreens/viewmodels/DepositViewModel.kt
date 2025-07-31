package com.weblite.kgf.ui.screens.payScreens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DepositViewModel : ViewModel() {
    private val _amount = MutableStateFlow(100)
    val amount: StateFlow<Int> = _amount.asStateFlow()

    fun setAmount(newAmount: Int) {
        _amount.value = newAmount
    }
}
