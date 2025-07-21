package com.weblite.kgf.Api2

import com.weblite.kgf.data.GameBetK360
import com.weblite.kgf.data.GameK360BettingResponse
import com.weblite.kgf.data.K330GameHistoryResponse
import com.weblite.kgf.data.K330MyHistoryResponse
import com.weblite.kgf.data.PeriodIdResponse
import com.weblite.kgf.data.K360GameHistoryResponse
import com.weblite.kgf.data.K360MyHistoryResponse
import com.weblite.kgf.data.K360PeriodIdResponse
import com.weblite.kgf.data.Wingo60SecDataClasses.Game60SecBettingResponse
import com.weblite.kgf.data.Wingo60SecDataClasses.GameBet60Sec
import com.weblite.kgf.data.Wingo60SecDataClasses.GameHistory60SecResponse
import com.weblite.kgf.data.Wingo60SecDataClasses.Wingo60PeriodIdResponse
import com.weblite.kgf.data.Wingo60SecDataClasses.Wingo60SecMyHistoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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

    // Wingo 30-second game APIs

    @GET("web/Api/wingo30SecPeriodID")
    suspend fun getThirtySecondPeriodID() : Response<PeriodIdResponse>

    @POST("web/Api/wingo30SecBet")
    suspend fun placeBet(@Body request: BetRequest): Response<BetResponse>

    @GET("web/Api/Wing30SecGameHistory")
    suspend fun getWingo30SecGameHistory(): Response<GameHistoryResponse>

    @GET("web/Api/wingo30secMyHistory")
    suspend fun getWingo30SecMyHistory(@Query("user_id") userId: String): Response<MyHistoryResponse>

    // Wingo 60-second game APIs

    @GET("web/Api/wingo1MinPeriodID")
    suspend fun getSixtySecondPeriodID(@Query("user_id") userId: String?) : Response<Wingo60PeriodIdResponse>

    @GET("web/Api/Wing1MinGameHistory")
    suspend fun getWingo60SecGameHistory(): Response<GameHistory60SecResponse>

    @GET("web/Api/wingo1MinMyHistory")
    suspend fun getWingo60SecMyHistory(@Query("user_id") userId: String): Response<Wingo60SecMyHistoryResponse>

    @POST("web/Api/wingo1MinBet")
    suspend fun place60SecBet(@Body request: GameBet60Sec): Response<Game60SecBettingResponse>

    // K3 1-minute game APIs
    @GET("web/Api/k31MinperiodID")
    suspend fun getK3O60neMinPeriodID(@Query("user_id") userId: String?): Response<K360PeriodIdResponse>

    @POST("web/Api/k3_1MinBet") // FIXED: Added underscore here!
    suspend fun placeK360Bet(@Body request: GameBetK360): Response<GameK360BettingResponse>

    // K3 Game History API
    @GET("web/Api/GameHistoryK31Min")
    suspend fun getK360GameHistory(): Response<K360GameHistoryResponse>

    // Add K3 my history APIs here if available
    @GET("web/Api/k3_1MinMyHistory")
    suspend fun getK360MyHistory(@Query("user_id") userId: String): Response<K360MyHistoryResponse>

    // K3 30-second game APIs
//    @GET("web/Api/k3periodID")
//    suspend fun getThirtySecondPeriodIDForK3(@Query("user_id") userId: String): Response<K360PeriodIdResponse>
//
//    @POST("/web/Api/k3_30SecBet")
//    suspend fun placeBet3Sec(@Body request: GameBetK360) : Response<GameK360BettingResponse>
//
//    @GET("web/Api/getGameHistory")
//    suspend fun getK3GameHistory(@Query("user_id") userId: String) : Response<GameHistoryResponse>
//
//    @GET("web/Api/k3_30sec_MyHistory")
//    suspend fun getMyK3GameHistory(@Query("user_id") userId: String) : Response<K360MyHistoryResponse>

    // K3 30-second game APIs (K330) - Using new data classes for history
    @GET("web/Api/k3periodID")
    suspend fun getThirtySecondPeriodIDForK3(@Query("user_id") userId: String): Response<K360PeriodIdResponse> // Period ID structure is same

    @POST("/web/Api/k3_30SecBet")
    suspend fun placeBet3Sec(@Body request: GameBetK360) : Response<GameK360BettingResponse> // Betting structure is same

    @GET("web/Api/getGameHistory")
    suspend fun getK330GameHistory(@Query("user_id") userId: String) : Response<K330GameHistoryResponse> // Use new K330 specific history

    @GET("web/Api/k3_30sec_MyHistory")
    suspend fun getK330MyHistory(@Query("user_id") userId: String) : Response<K330MyHistoryResponse> // Use new K330 specific my history

}
