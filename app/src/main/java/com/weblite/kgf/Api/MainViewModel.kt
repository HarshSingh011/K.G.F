package com.weblite.kgf.Api2

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.weblite.kgf.data.PeriodIdResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UserRepository,
    val timerService: WingoTimerService
) : ViewModel() {
    private val _profileResponse = MutableStateFlow<ProfileResponse?>(null)
    val profileResponse: StateFlow<ProfileResponse?> = _profileResponse

    private val _profileError = MutableStateFlow<String?>(null)
    val profileError: StateFlow<String?> = _profileError


    var loginState = mutableStateOf<Resource<LoginResponse>?>(null)
        private set

    fun login(mobile: String, password: String) {
        viewModelScope.launch {
            loginState.value = Resource.Loading()
            try {
                val response = repository.loginUser(mobile, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("LOGIN_RESPONSE", "Success: $it")
                        loginState.value = Resource.Success(it)
                    } ?: run {
                        loginState.value = Resource.Error("Empty response")
                        Log.e("LOGIN_RESPONSE", "Empty body")
                    }
                } else {
                    loginState.value = Resource.Error("Server error: ${response.code()}")
                    Log.e("LOGIN_RESPONSE", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                loginState.value = Resource.Error("Exception: ${e.message}")
                Log.e("LOGIN_RESPONSE", "Exception", e)
            }
        }
    }

    fun resetLoginState() {
        loginState.value = null
    }

    var signupState = mutableStateOf<Resource<SignupResponse>?>(null)
        private set

    fun signup(mobile: String, password: String, confirmPassword: String, referral: String) {
        viewModelScope.launch {
            signupState.value = Resource.Loading()
            try {
                val response = repository.signupUser(mobile, password, confirmPassword, referral)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("SIGNUP_RESPONSE", "Success: $it")
                        signupState.value = Resource.Success(it)
                    } ?: run {
                        signupState.value = Resource.Error("Empty response")
                        Log.e("SIGNUP_RESPONSE", "Empty body")
                    }
                } else {
                    signupState.value = Resource.Error("Server error: ${response.code()}")
                    Log.e("SIGNUP_RESPONSE", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                signupState.value = Resource.Error("Exception: ${e.message}")
                Log.e("SIGNUP_RESPONSE", "Exception", e)
            }
        }
    }

    fun resetSignupState() {
        signupState.value = null
    }


    var dashboardState = mutableStateOf<Resource<DashboardResponse>?>(null)
        private set

    fun fetchDashboard(userId: String?) {
        viewModelScope.launch {
            dashboardState.value = Resource.Loading()
            try {
                val response = repository.getDashboard(userId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("DASHBOARD_RESPONSE", "Success: $it")
                        dashboardState.value = Resource.Success(it)
                    } ?: run {
                        dashboardState.value = Resource.Error("Empty response")
                    }
                } else {
                    dashboardState.value =
                        Resource.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                dashboardState.value = Resource.Error("Exception: ${e.message}")
            }
        }
    }

    fun resetDashboardState() {
        dashboardState.value = null
    }

    var sendOtpState = mutableStateOf<Resource<SendOtpResponse>?>(null)
        private set

    fun sendOtp(mobile: String) {
        viewModelScope.launch {
            sendOtpState.value = Resource.Loading()
            try {
                val response = repository.sendOtp(mobile)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("SEND_OTP_RESPONSE", "Success: $it")
                        sendOtpState.value = Resource.Success(it)
                    } ?: run {
                        sendOtpState.value = Resource.Error("Empty response")
                        Log.e("SEND_OTP_RESPONSE", "Empty body")
                    }
                } else {
                    sendOtpState.value = Resource.Error("Server error: ${response.code()}")
                    Log.e("SEND_OTP_RESPONSE", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                sendOtpState.value = Resource.Error("Exception: ${e.message}")
                Log.e("SEND_OTP_RESPONSE", "Exception", e)
            }
        }
    }

    fun resetSendOtpState() {
        sendOtpState.value = null
    }

    var verifyOtpState = mutableStateOf<Resource<VerifyOtpResponse>?>(null)
        private set

    fun verifyOtp(mobile: String?, otp: String, newPass: String, confirmPass: String) {
        viewModelScope.launch {
            verifyOtpState.value = Resource.Loading()
            try {
                val response = repository.verifyOtp(mobile, otp, newPass, confirmPass)
                if (response.isSuccessful) {
                    response.body()?.let {
                        verifyOtpState.value = Resource.Success(it)
                        Log.d("VERIFY_OTP_RESPONSE", "Success: $it")
                    } ?: run {
                        verifyOtpState.value = Resource.Error("Empty response")
                    }
                } else {
                    verifyOtpState.value = Resource.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                verifyOtpState.value = Resource.Error("Exception: ${e.message}")
            }
        }
    }

    fun resetVerifyOtpState() {
        verifyOtpState.value = null
    }

    private val _updatePasswordState = mutableStateOf<Resource<UpdatePasswordResponse>?>(null)
    val updatePasswordState: State<Resource<UpdatePasswordResponse>?> = _updatePasswordState
//    None of the following candidates is applicable:
//    object State : Any
//    typealias State = Int


    fun updatePassword(mobile: String, password: String) {
        viewModelScope.launch {
            _updatePasswordState.value = Resource.Loading()
            try {
                val response = repository.updatePassword(mobile, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _updatePasswordState.value = Resource.Success(it)//Assignment type mismatch: actual type is 'Resource.Success<VerifyOtpResponse>',
                    } ?: run {
                        _updatePasswordState.value = Resource.Error("Empty response")
                    }
                } else {
                    _updatePasswordState.value = Resource.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _updatePasswordState.value = Resource.Error("Exception: ${e.message}")
            }
        }
    }


    fun resetUpdatePasswordState() {
        _updatePasswordState.value = null//Unresolved reference '_updatePasswordState'.
    }

    // get profile
    var commissionState = mutableStateOf<Resource<CommissionResponse>?>(null)
        private set
    fun fetchCommissions(userId: String?) {
        viewModelScope.launch {
            commissionState.value = Resource.Loading()
            try {
                val response = repository.getCommissions(userId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("COMMISSION_RESPONSE", "Success: $it")
                        commissionState.value = Resource.Success(it)
                    } ?: run {
                        commissionState.value = Resource.Error("Empty response")
                    }
                } else {
                    commissionState.value = Resource.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                commissionState.value = Resource.Error("Exception: ${e.message}")
            }
        }
    }

    fun resetCommissionState() {
        commissionState.value = null
    }

    ///direct and indirect
    // State
    var promotionViewState = mutableStateOf<Resource<PromotionViewResponse>?>(null)
        private set

    // Fetch Function
    fun fetchPromotionView(userId: String) {
        viewModelScope.launch {
            promotionViewState.value = Resource.Loading()
            try {
                val response = repository.getPromotionView(userId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        promotionViewState.value = Resource.Success(it)
                    } ?: run {
                        promotionViewState.value = Resource.Error("Empty response")
                    }
                } else {
                    promotionViewState.value = Resource.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                promotionViewState.value = Resource.Error("Exception: ${e.message}")
            }
        }
    }

    //  Reset function
    fun resetPromotionViewState() {
        promotionViewState.value = null
    }

    sealed class UiState<out T> {
        object Loading : UiState<Nothing>()
        data class Success<T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()
    }

    private val _profileResult = MutableStateFlow<Result<ProfileResponse>?>(null)
    val profileResult: StateFlow<Result<ProfileResponse>?> = _profileResult

    private val _profileState = MutableStateFlow<Result<ProfileResponse>?>(null)
    val profileState: StateFlow<Result<ProfileResponse>?> = _profileState

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            val result = repository.fetchUserProfile(userId)
            _profileState.value = result
        }
    }
}
