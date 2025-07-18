package com.weblite.kgf.Api2

import android.util.Log
import com.google.gson.Gson
import com.weblite.kgf.data.PeriodIdResponse
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun loginUser(mobile: String, pass: String): Response<LoginResponse> {
        val request = LoginRequest(mobile, pass)
        return apiService.loginUser(request)
    }

    suspend fun signupUser(
        mobile: String,
        pass: String,
        confirmPassword: String,
        referral: String
    ): Response<SignupResponse> {
        val request = SignupRequest(mobile, pass, confirmPassword, referral)
        return apiService.signupUser(request)
    }

    suspend fun getDashboard(userId: String?): Response<DashboardResponse> {
        return apiService.getDashboard(userId)
    }

    suspend fun sendOtp(mobile: String): Response<SendOtpResponse> {
        val request = SendOtpRequest(mobile)
        return apiService.sendOtp(request)
    }

    suspend fun verifyOtp(
        mobile: String?,
        otp: String,
        newPassword: String,
        reenterPassword: String
    ): Response<VerifyOtpResponse> {
        val request = VerifyOtpRequest(
            otp = otp,
            newPassword = newPassword,
            reenterPassword = reenterPassword
        )
        return apiService.verifyOtp(
            mobileNo = mobile,
            request = request
        )
    }

    suspend fun updatePassword(mobile: String, password: String): Response<UpdatePasswordResponse> {
        val body = mapOf("mobile" to mobile, "password" to password)
        return apiService.updatePassword(mobile, body)
    }

    // profile
    suspend fun fetchUserProfile(userId: String): Result<ProfileResponse> {
        return try {
            val response = apiService.getUserProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                //'result' exists because body() is ProfileResponseWrapper
                Result.success(response.body()!!.result)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
//    suspend fun fetchUserProfileAndLog(userId: String) {
//        try {
//            val response = apiService.getUserProfile(userId)
//
//            if (response.isSuccessful) {
//                val body = response.body()
//                Log.d("API_SUCCESS", "Raw response: ${Gson().toJson(body)}")
//            } else {
//                Log.e("API_ERROR", "HTTP ${response.code()} - ${response.message()}")
//            }
//        } catch (e: Exception) {
//            Log.e("API_EXCEPTION", "Error: ${e.localizedMessage}")
//        }
//    }

    // commission
    suspend fun getCommissions(userId: String?): Response<CommissionResponse> {
        return apiService.getCommissions(userId)
    }

    suspend fun getPromotionView(userId: String): Response<PromotionViewResponse> {
        return apiService.getPromotionView(userId)
    }

//    suspend fun getWingo30SecPeriod(): Response<Wingo30SecPeriodResponse> {
//        return apiService.getWingo30SecPeriod()
//    }

    suspend fun placeBet(
        userid: String,
        bidNum: String,
        bidType: String,
        period: String,
        quantity: String,
        price: String,
        agree: String = "on",
        boost: String = "txt"
    ): Response<BetResponse> {
        val request = BetRequest(
            userid = userid,
            bidNum = bidNum,
            bidType = bidType,
            period = period,
            quantity = quantity,
            price = price,
            agree = agree,
            boost = boost
        )
        return apiService.placeBet(request)
    }

    suspend fun getWingo30SecGameHistory(): Response<GameHistoryResponse> {
        return apiService.getWingo30SecGameHistory()
    }

    suspend fun getWingo30SecMyHistory(userId: String): Response<MyHistoryResponse> {
        return apiService.getWingo30SecMyHistory(userId)
    }

    suspend fun getPeriodId() : Result<PeriodIdResponse>{
        return try {
            val response = apiService.getThirtySecondPeriodID()
            if (response.isSuccessful && response.body() != null) {
                Timber.tag("CheckPeriodId").d("Success : ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Timber.tag("CheckPeriodId").d("Failure : $${response.errorBody()?.string()}")
                Result.failure(Exception("Failed to send OTP: ${response.errorBody()?.string()}"))
            }
        }
        catch (e : Exception){
            Timber.tag("Check123").d("Error Occurred1 : $e")
            Result.failure(e)
        }
    }
}
