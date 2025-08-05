package com.weblite.kgf.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.domain.model.PromotionCommissionDateDetailsResponse
import com.weblite.kgf.data.repository.PromotionRepository
import com.weblite.kgf.Api2.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromotionViewModel @Inject constructor(
    private val repository: PromotionRepository
) : ViewModel() {
    var commissionDateDetailsState = mutableStateOf<Resource<PromotionCommissionDateDetailsResponse>?>(null)
        private set

    fun fetchPromotionCommissionDateDetails(userId: String, date: String) {
        viewModelScope.launch {
            commissionDateDetailsState.value = Resource.Loading()
            try {
                val result = repository.getPromotionCommissionDateDetails(userId, date)
                result.onSuccess {
                    commissionDateDetailsState.value = Resource.Success(it)
                }.onFailure {
                    commissionDateDetailsState.value = Resource.Error(it.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                commissionDateDetailsState.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetCommissionDateDetailsState() {
        commissionDateDetailsState.value = null
    }
}
