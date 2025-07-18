package com.weblite.kgf.Api2

import com.weblite.kgf.data.PeriodIdResponse
import com.weblite.kgf.data.Wingo60PeriodIdResponse // Import the new data class
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("web/Api/userLogin")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("web/Api/userSignup")
    suspend fun signupUser(@Body request: SignupRequest): Response<SignupResponse>

    @GET("web/Api/dashboard")
    suspend fun getDashboard(@Query("user_id") userId: String?): Response<DashboardResponse>

    @POST("web/Api/send_otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>

    @POST("web/Api/verify_otp")
    suspend fun verifyOtp(
        @Query("mobile_no") mobileNo: String?,
        @Body request: VerifyOtpRequest
    ): Response<VerifyOtpResponse>

    @POST("web/Api/update_password")
    suspend fun updatePassword(
        @Query("mobile_no") mobileNo: String,
        @Body body: Map<String, String>
    ): Response<UpdatePasswordResponse>

    @GET("web/Api/profile")
    suspend fun getUserProfile(
        @Query("user_id") userId: String
    ): Response<ProfileResponseWrapper>

    @GET("web/Api/myCommissions")
    suspend fun getCommissions(@Query("user_id") userId: String?): Response<CommissionResponse>

    @GET("web/Api/promotionView")
    suspend fun getPromotionView(@Query("user_id") userId: String): Response<PromotionViewResponse>

    @GET("web/Api/wingo30SecPeriodID")
    suspend fun getThirtySecondPeriodID() : Response<PeriodIdResponse>

    @GET("web/Api/wingo1MinPeriodID") // New API endpoint for 60-second game
    suspend fun getSixtySecondPeriodID(@Query("user_id") userId: String?) : Response<Wingo60PeriodIdResponse>

    @POST("web/Api/wingo30SecBet")
    suspend fun placeBet(@Body request: BetRequest): Response<BetResponse>

    @GET("web/Api/Wing30SecGameHistory")
    suspend fun getWingo30SecGameHistory(): Response<GameHistoryResponse>


    @GET("web/Api/wingo30secMyHistory")
    suspend fun getWingo30SecMyHistory(@Query("user_id") userId: String): Response<MyHistoryResponse>
}
