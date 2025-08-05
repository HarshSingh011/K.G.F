package com.weblite.kgf.presentation.vip

import com.weblite.kgf.domain.model.VipInfo

sealed class VipUiState {
    object Loading : VipUiState()
    data class Success(val vipInfo: VipInfo) : VipUiState()
    data class Error(val message: String) : VipUiState()
}
