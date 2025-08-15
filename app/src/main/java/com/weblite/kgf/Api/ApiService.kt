package com.weblite.kgf.Api

import com.weblite.kgf.data.GameBetK360
import com.weblite.kgf.data.GameK360BettingResponse
import com.weblite.kgf.data.K330GameHistoryResponse
import com.weblite.kgf.data.K330MyHistoryResponse
import com.weblite.kgf.data.PeriodIdResponse
import com.weblite.kgf.data.K360GameHistoryResponse
import com.weblite.kgf.data.K360MyHistoryResponse
import com.weblite.kgf.data.K360PeriodIdResponse
import com.weblite.kgf.data.models.auth.AwardResponse
import com.weblite.kgf.data.models.auth.BetRequest
import com.weblite.kgf.data.models.auth.BetResponse
import com.weblite.kgf.data.models.auth.DashboardResponse
import com.weblite.kgf.data.models.auth.FilteredMissionsResponse
import com.weblite.kgf.data.models.auth.GameHistoryResponse
import com.weblite.kgf.data.models.auth.LoginRequest
import com.weblite.kgf.data.models.auth.LoginResponse
import com.weblite.kgf.data.models.auth.MyHistoryResponse
import com.weblite.kgf.data.models.auth.ProfileResponseWrapper
import com.weblite.kgf.data.models.auth.PromotionViewResponse
import com.weblite.kgf.data.models.auth.ReferralBonusResponse
import com.weblite.kgf.data.models.auth.SendOtpRequest
import com.weblite.kgf.data.models.auth.SendOtpResponse
import com.weblite.kgf.data.models.auth.SignupRequest
import com.weblite.kgf.data.models.auth.SignupResponse
import com.weblite.kgf.data.models.auth.UpdatePasswordResponse
import com.weblite.kgf.data.models.auth.VerifyOtpRequest
import com.weblite.kgf.data.models.auth.VerifyOtpResponse
import com.weblite.kgf.data.models.games.K3PopupHistoryResponse
import com.weblite.kgf.data.models.games.Game60SecBettingResponse
import com.weblite.kgf.data.models.games.GameBet60Sec
import com.weblite.kgf.data.models.games.GameHistory60SecResponse
import com.weblite.kgf.data.models.games.Wingo60PeriodIdResponse
import com.weblite.kgf.data.models.games.Wingo60SecMyHistoryResponse
import com.weblite.kgf.data.models.games.BettingGameResultResponse
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

    // Wingo 30-sec popup history API
    @GET("web/Api/wingo30secPopupHistoryApi")
    suspend fun getWingo30SecPopupHistory(@Query("user_id") userId: String): Response<BettingGameResultResponse>

    // Wingo 60-second game APIs
    @GET("web/Api/wingo1MinPeriodID")
    suspend fun getSixtySecondPeriodID(@Query("user_id") userId: String?) : Response<Wingo60PeriodIdResponse>

    @GET("web/Api/Wing1MinGameHistory")
    suspend fun getWingo60SecGameHistory(): Response<GameHistory60SecResponse>

    @GET("web/Api/wingo1MinMyHistory")
    suspend fun getWingo60SecMyHistory(@Query("user_id") userId: String): Response<Wingo60SecMyHistoryResponse>

    @POST("web/Api/wingo1MinBet")
    suspend fun place60SecBet(@Body request: GameBet60Sec): Response<Game60SecBettingResponse>

    // Wingo 60-sec popup history API - using the provided endpoint and same data class as Wingo30
    @GET("web/Api/wingo60secPopupHistoryApi")
    suspend fun getWingo60SecPopupHistory(@Query("user_id") userId: String): Response<BettingGameResultResponse>

    // K3 1-minute game APIs
    @GET("web/Api/k31MinperiodID")
    suspend fun getK3O60neMinPeriodID(@Query("user_id") userId: String?): Response<K360PeriodIdResponse>

    @POST("web/Api/k3_1MinBet")
    suspend fun placeK360Bet(@Body request: GameBetK360): Response<GameK360BettingResponse>

    // K3 Game History API
    @GET("web/Api/GameHistoryK31Min")
    suspend fun getK360GameHistory(): Response<K360GameHistoryResponse>

    // Add K3 my history APIs here if available
    @GET("web/Api/k3_1MinMyHistory")
    suspend fun getK360MyHistory(@Query("user_id") userId: String): Response<K360MyHistoryResponse>

    // K3 60-sec popup history API - using the provided endpoint
    @GET("/web/Api/K360secPopupHistoryApi")
    suspend fun getK360PopupHistory(@Query("user_id") userId: String): Response<K3PopupHistoryResponse>

    // K3 30-second game APIs (K330) - Using new data classes for history
    @GET("web/Api/k3periodID")
    suspend fun getThirtySecondPeriodIDForK3(@Query("user_id") userId: String): Response<K360PeriodIdResponse>

    @POST("/web/Api/k3_30SecBet")
    suspend fun placeBet3Sec(@Body request: GameBetK360) : Response<GameK360BettingResponse>

    @GET("web/Api/getGameHistory")
    suspend fun getK330GameHistory(@Query("user_id") userId: String) : Response<K330GameHistoryResponse>

    @GET("web/Api/k3_30sec_MyHistory")
    suspend fun getK330MyHistory(@Query("user_id") userId: String) : Response<K330MyHistoryResponse>

    // K3 30-sec popup history API - using the provided endpoint
    @GET("web/Api/K3WinResultPopup")
    suspend fun getK330PopupHistory(@Query("user_id") userId: String): Response<K3PopupHistoryResponse>

    // Commission date details API
    @GET("web/Api/myCommissionDateDetails")
    suspend fun getMyCommissionDateDetails(
        @Query("user_id") userId: String,
        @Query("date") date: String
    ): Response<com.weblite.kgf.domain.model.MyCommissionDateDetailsResponse>

    @GET("web/Api/invitation_records")
    suspend fun invitationBonus(@Query("user_id") userId: String): ReferralBonusResponse?

    @GET("web/Api/mission_data")
    suspend fun missionData(@Query("user_id") userId: String): FilteredMissionsResponse

    @GET("web/Api/activityaward")
    suspend fun activityAward(@Query("user_id") userId: String): AwardResponse

    @GET("web/Api/mission_dataByDate")
    suspend fun missionDataByDate(
        @Query("user_id") userId: String,
        @Query("dateFilter") dateFilter: String
    ): FilteredMissionsResponse
}
