
package com.weblite.kgf.ui.screens.payScreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.data.repository.UpiQrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class QrCodeViewModel @Inject constructor(
    private val repository: UpiQrRepository
) : ViewModel() {
    private val _qrImageUrl = MutableStateFlow<String?>(null)
    val qrImageUrl: StateFlow<String?> = _qrImageUrl.asStateFlow()

    fun fetchQrCode() {
        viewModelScope.launch {
            val response = repository.fetchUpiQr()
            if (response.isSuccessful) {
                val qrUrl = response.body()?.result?.qr_code
                if (!qrUrl.isNullOrBlank()) {
                    _qrImageUrl.value = qrUrl
                }
            }
        }
    }
}
