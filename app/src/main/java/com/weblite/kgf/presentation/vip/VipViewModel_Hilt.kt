package com.weblite.kgf.presentation.vip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.domain.model.VipInfo
import com.weblite.kgf.domain.repository.VipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.weblite.kgf.presentation.vip.VipUiState
import com.weblite.kgf.Api2.SharedPrefManager
import com.weblite.kgf.domain.model.RewardHistory

@HiltViewModel
class VipViewModel @Inject constructor(
    private val repository: VipRepository
) : ViewModel() {
    private val _vipState = MutableStateFlow<VipUiState>(VipUiState.Loading)
    val vipState: StateFlow<VipUiState> = _vipState

    private val _rewardHistory = MutableStateFlow<List<RewardHistory>>(emptyList())
    val rewardHistory: StateFlow<List<RewardHistory>> = _rewardHistory

    fun fetchVipInfo() {
        _vipState.value = VipUiState.Loading
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
            val result = repository.getVipInfo(userId)
            _vipState.value = result.fold(
                onSuccess = { VipUiState.Success(it) },
                onFailure = { VipUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun fetchRewardHistory() {
        viewModelScope.launch {
            val userId = SharedPrefManager.getString("user_id", "0") ?: "0"
            val result = repository.getRewardHistory(userId)
            _rewardHistory.value = result.getOrDefault(emptyList())
        }
    }
}
