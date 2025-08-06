package com.weblite.kgf.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.domain.model.PromotionCommissionDateDetailsResponse
import com.weblite.kgf.data.repository.PromotionRepository
import com.weblite.kgf.Api2.Resource
import com.weblite.kgf.domain.model.MyCommissionsResponse
import com.weblite.kgf.domain.model.DirectTeamDataResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromotionViewModel @Inject constructor(
    // New API for direct team data by date

    private val repository: PromotionRepository
) : ViewModel() {
    var commissionDateDetailsState = mutableStateOf<Resource<PromotionCommissionDateDetailsResponse>?>(null)
        private set

    var commissionsState = mutableStateOf<Resource<MyCommissionsResponse>?>(null)
        private set

    var directTeamDataState = mutableStateOf<Resource<DirectTeamDataResponse>?>(null)
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

    fun fetchMyCommissions(userId: String) {
        viewModelScope.launch {
            commissionsState.value = Resource.Loading()
            try {
                val result = repository.getMyCommissions(userId)
                result.onSuccess {
                    commissionsState.value = Resource.Success(it)
                }.onFailure {
                    commissionsState.value = Resource.Error(it.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                commissionsState.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchDirectTeamData(userId: String) {
        viewModelScope.launch {
            directTeamDataState.value = Resource.Loading()
            try {
                val result = repository.getDirectTeamData(userId)
                result.onSuccess {
                    directTeamDataState.value = Resource.Success(it)
                }.onFailure {
                    directTeamDataState.value = Resource.Error(it.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                directTeamDataState.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchDirectTeamDataWithDate(userId: String, joiningDate: String) {
        viewModelScope.launch {
            directTeamDataState.value = Resource.Loading()
            try {
                val result = repository.getDirectTeamDataWithDate(userId, joiningDate)
                result.onSuccess { data: DirectTeamDataResponse ->
                    directTeamDataState.value = Resource.Success(data)
                }.onFailure { throwable ->
                    directTeamDataState.value = Resource.Error<DirectTeamDataResponse>(throwable.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                directTeamDataState.value = Resource.Error<DirectTeamDataResponse>(e.message ?: "Unknown error")
            }
        }
    }

    fun resetCommissionDateDetailsState() {
        commissionDateDetailsState.value = null
    }
    fun resetCommissionsState() {
        commissionsState.value = null
    }
    fun resetDirectTeamDataState() {
        directTeamDataState.value = null
    }
}
