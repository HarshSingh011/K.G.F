
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.data.models.payments.DepositHistoryResponse
import com.weblite.kgf.data.repository.PaymentRespositories.UpiQrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DepositHistoryViewModel @Inject constructor(
    private val repository: UpiQrRepository
) : ViewModel() {
    private val _history = MutableStateFlow<DepositHistoryResponse?>(null)
    val history: StateFlow<DepositHistoryResponse?> = _history.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchDepositHistory(userId: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getDepositHistory(userId)
                if (response.isSuccessful) {
                    _history.value = response.body()
                    _error.value = null
                } else {
                    _error.value = response.errorBody()?.string() ?: "Unknown error"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
// Duplicate file removed to resolve redeclaration error.
                _loading.value = false
            }
        }
    }
}
