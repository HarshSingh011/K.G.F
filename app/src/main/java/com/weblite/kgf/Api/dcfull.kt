package com.weblite.kgf.Api2

import com.google.gson.annotations.SerializedName


// Common response wrapper
data class ApiResponse<T>(
    val status: String,
    val status_code: Int,
    val msg: String,
    val result: T
)

// Request body
data class UpdatePasswordRequest(
    val mobile: String,
    val password: String
)

// Response
data class UpdatePasswordResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)


// Login
data class LoginRequest(
    @SerializedName("mobile")
    val mobile: String,

    @SerializedName("pass")
    val pass: String
)

data class LoginResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("status_code") val statusCode: Int?,
    @SerializedName("msg") val message: String?,
    @SerializedName("result") val result: ResultData?
)

data class ResultData(
    @SerializedName("user") val user: User?,
    @SerializedName("token") val token: String?
)


////////Profile

////////end profile

data class SignupRequest(
    @SerializedName("mobile")
    val mobile: String,

    @SerializedName("pass")
    val pass: String,

    @SerializedName("confirmPassword")
    val confirmPassword: String,

    @SerializedName("referral")
    val referral: String
)

data class SignupResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("status_code")
    val statusCode: Int,

    @SerializedName("msg")
    val message: String,

    @SerializedName("result")
    val result: SignupUserData?
)

data class SignupUserData(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("mobile")
    val mobile: String,

    @SerializedName("referral")
    val referral: String?, // Nullable because API returns null

    @SerializedName("joining_date")
    val joiningDate: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("id")
    val id: Int
)

// Dashboard

data class DashboardResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("status_code")
    val statusCode: Int,

    @SerializedName("msg")
    val message: String,

    @SerializedName("result")
    val result: DashboardResult
)

data class DashboardResult(
    @SerializedName("levelUser")
    val levelUser: String,

    @SerializedName("user")
    val user: DashboardUser,

    @SerializedName("total_balance")
    val totalBalance: Int
)

data class DashboardUser(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("mobile")
    val mobile: String,

    @SerializedName("joining_date")
    val joiningDate: String,

    @SerializedName("id")
    val id: String?,

    @SerializedName("userID")
    val userID: String?,

    @SerializedName("total_balance")
    val totalBalance: String?
)

// Deposit
data class DepositHistoryItem(
    val recharge_sl: String,
    val user_id: String,
    val order_no: String,
    val user_mobile: String,
    val pay_amount: String,
    val txn_id: String,
    val status: String,
    val post_date: String,
    val total_amount: String
)

data class StoreDepositRequest(
    val amount: Int,
    val utr_no: String,
    val mobile: String
)

data class StoreDepositResponse(
    val user_id: String,
    val order_no: String,
    val user_mobile: String,
    val pay_amount: Int,
    val txn_id: String,
    val post_date: String,
    val id: Int
)

// Bank Details
data class BankDetailsRequest(
    val bank: String,
    val recipient_name: String,
    val account_number: String,
    val phone_number: String,
    val ifsc_code: String
)

data class UpiDetailsRequest(
    val userName: String,
    val upi_id: String,
    val upiProvider: String
)

data class BankDetailsResponse(
    val id: String,
    val user_sl: String,
    val name: String,
    val contact_no: String?,
    val account_no: String?,
    val ifsc_code: String?,
    val bank_name: String?,
    val upi_id: String?,
    val upi_provider: String?
)

// Withdrawal
data class WithdrawRequestBank(
    val amount: Int,
    val paymentMethod: String,
    val username: String,
    val accountNo: String,
    val ifsccode: String
)

data class WithdrawRequestUpi(
    val amount: Int,
    val paymentMethod: String,
    val upiId: String,
    val upiProvider: String
)

data class WithdrawResponse(
    val user_id: String,
    val amount: Int,
    val payment_method: String,
    val transaction_id: String,
    val created_at: String
)

data class WithdrawHistoryItem(
    val id: String,
    val user_id: String,
    val amount: String,
    val payment_method: String,
    val transaction_id: String,
    val created_at: String,
    val status: String
)

// User Gift
data class UserGiftItem(
    val id: String,
    val user_id: String,
    val coupon_code: String,
    val amount: String,
    val redeemed_at: String
)

data class CouponRedeemRequest(
    val code: String
)

// K3 Games
data class K3PeriodResponse(
    val datetime: String
)

data class K3BetRequest(
    val userid: String,
    val bidNum: String,
    val bidType: String,
    val period: String,
    val price: String,
    val quantity: String
)

data class K3GameHistoryItem(
    val id: String,
    val datetime: String,
    val bidNum: String?,
    val bidOddEven: String?,
    val bidBigSmall: String?,
    val status: String?,
    val current_dt: String,
    val finalBidBigSmall: String?,
    val finalOddEven: String?
)

data class K3MyHistoryItem(
    val id: String,
    val bidNum: String,
    val bidType: String,
    val period: String,
    val price: String,
    val status: String,
    val created_at: String,
    val totalamount: String?
)

// Wingo Games
data class WingoPeriodResponse(
    val datetime: String?,
    val period_60: String?
)

data class WingoBetRequest(
    val userid: String,
    val bidNum: String,
    val bidType: String,
    val period: String,
    val quantity: String,
    val price: String,
    val agree: String?,
    val boost: String?
)

data class WingoGameHistoryItem(
    val id: String?,
    val bid_60: String?,
    val datetime: String?,
    val period_60: String?,
    val number_result: String?,
    val color_result: String?,
    val big_small_result: String?,
    val current_dt: String?,
    val update_at: String?,
    val finalBigSmall: String?,
    val finalcolor: String?
)

data class WingoMyHistoryItem(
    val id: String?,
    val betID: String?,
    val bidNum: String,
    val bidType: String,
    val period: String,
    val price: String,
    val status: String,
    val created_at: String,
    val number_result: String?,
    val color_result: String?,
    val totalamount: String?
)

// Dragon Tiger
data class DragonTigerPeriodResponse(
    val datetime: String
)

data class DragonTigerBetRequest(
    val period_id: Long,
    val coins: Int,
    val bet_choice: String
)

data class DragonTigerGameHistoryItem(
    val id: String,
    val datetime: String,
    val current_dt: String,
    val result: String
)

data class DragonTigerMyHistoryItem(
    val id: String,
    val period_id: String,
    val coins: String,
    val bet_choice: String,
    val created_at: String,
    val result: String,
    val totalamount: String?
)

// Countdown
data class CountdownResponse(
    val apiCountID: String,
    val period_id: String,
    val countdown: String,
    val created_at: String
)

// Promotion
data class DirectTeamData(
    val id: String,
    val user_id: String,
    val joining_date: String,
    val deposit_amount: String,
    val total_commission: String,
    val level: String
)

data class CommissionItem(
    val day_commission_amount: String,
    val date: String
)

data class CommissionDetailsItem(
    val id: String,
    val commission_amount: String,
    val giver_user_id: String,
    val created_at: String
)

//data class PromotionViewResponse(
//    val user_id: String,
//    val referralCount: Int,
//    val directDepositAmount: Int,
//    val myTodayCommission: String
//)

// VIP
data class VipResponse(
    val DaysLeft: Int,
    val userID: String,
    val totalExp: Int,
    val levelUser: String
)

data class UserExpResponse(
    val user_id: String,
    val total_exp: Int,
    val completed_levels: List<Int>,
    val benefit_levels: List<Int>,
    val next_level: Int,
    val remaining_exp: Int
)

data class RewardHistoryItem(
    val id: String,
    val level: String,
    val bonusAmount: String,
    val type: String,
    val claimed_at: String
)

data class SendOtpRequest(
    @SerializedName("mobile")
    val mobile: String
)

data class SendOtpResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("otp")
    val otp: Int,

    @SerializedName("otpGeneratedTime")
    val otpGeneratedTime: Long,

    @SerializedName("mobile_no")
    val mobileNo: Long,

    @SerializedName("debug_otp_in_session")
    val debugOtpInSession: Int,

    @SerializedName("session_id")
    val sessionId: String
)

data class VerifyOtpResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)

data class VerifyOtpRequest(
    @SerializedName("otp")
    val otp: String,

    @SerializedName("new_password")
    val newPassword: String,

    @SerializedName("reenter_password")
    val reenterPassword: String
)

// Customer Support
data class CustomerSupportRequest(
    val user_id: String,
    val message: String
)

data class CustomerMessage(
    val id: String,
    val user_id: String,
    val message: String,
    val datetime: String,
    val status: String
)

// Profile
data class ProfileResponseWrapper(
    val status: String,
    @SerializedName("status_code")
    val statusCode: Int,
    val msg: String,
    val result: ProfileResponse
)

data class ProfileResponse(
    val levelUser: String,
    val user: User,
    @SerializedName("total_balance") val totalBalance: String,
    @SerializedName("team_deposits_total_date") val teamDepositsTotalDate: String?,
    @SerializedName("team_betting_total_date") val teamBettingTotalDate: String?,
    @SerializedName("all_time_team_betting_total") val allTimeTeamBettingTotal: String?,
    @SerializedName("all_time_team_deposits_total") val allTimeTeamDepositsTotal: String?,
    @SerializedName("all_time_team_withdrawal_total") val allTimeTeamWithdrawalTotal: String?,
    @SerializedName("all_time_team_balance_total") val allTimeTeamBalanceTotal: String?,
    @SerializedName("all_time_team_profit") val allTimeTeamProfit: Double?,
    @SerializedName("day_team_deposits_total_fmt") val dayTeamDepositsTotalFmt: String?,
    @SerializedName("day_team_betting_total_fmt") val dayTeamBettingTotalFmt: String?
)

data class User(
    @SerializedName("id") val id: String?,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("otp") val otp: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("pass") val pass: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("joining_date") val joiningDate: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("pin") val pin: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("photo") val photo: String?,
    @SerializedName("wallet") val wallet: String?,
    @SerializedName("bonus_amount") val bonusAmount: String?,
    @SerializedName("first_deposite") val firstDeposite: String?,
    @SerializedName("withdrawal_amount") val withdrawalAmount: String?,
    @SerializedName("referral") val referral: String?,
    @SerializedName("referral_id") val referralId: String?,
    @SerializedName("franchise") val franchise: String?,
    @SerializedName("recharge_option") val rechargeOption: String?,
    @SerializedName("bonus_applied") val bonusApplied: String?,
    @SerializedName("total_income") val totalIncome: String?,
    @SerializedName("d_status") val dStatus: String?,
    @SerializedName("is_super_user") val isSuperUser: String?,
    @SerializedName("session_id") val sessionId: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("last_login") val lastLogin: String?,
    @SerializedName("auth_token") val authToken: String?
)


// Activity Award
data class ActivityAwardResponse(
    val total_betting_amount: Int,
    val total_award_earned: TotalAmountAward,
    val missions: List<Mission>
) {
    data class TotalAmountAward(
        val total_amount_award: String
    )

    data class Mission(
        val target: Int,
        val name: String,
        val award: Int,
        val date: String,
        val completed: Int,
        val remaining: Int
    )
}

// Referral Bonus
data class ReferralBonusResponse(
    val levels: List<Level>,
    val level_counts: List<Int>,
    val level_amounts: List<Int>
) {
    data class Level(
        val required: Int,
        val bonus: String
    )
}

///////////////////////////////////
data class CommissionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("commission_data") val commissionData: List<CommissionData>?
)

data class CommissionData(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("date") val date: String
)
/////////
data class PromotionViewResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val message: String,
    @SerializedName("result") val result: PromotionViewResult
)

data class PromotionViewResult(
    @SerializedName("user_id") val userId: String,
    @SerializedName("rowCount") val rowCount: Int,
    @SerializedName("referralCount") val referralCount: Int,
    @SerializedName("directDepositCount") val directDepositCount: Int,
    @SerializedName("directDepositAmount") val directDepositAmount: Int,
    @SerializedName("directFirstDepositCount") val directFirstDepositCount: Int,
    @SerializedName("indirectDepositCount") val indirectDepositCount: Int,
    @SerializedName("indirectDepositAmount") val indirectDepositAmount: Int,
    @SerializedName("indirectFirstDepositCount") val indirectFirstDepositCount: Int,
    @SerializedName("myTodayCommission") val myTodayCommission: String?,
    @SerializedName("totalCommission") val totalCommission: String
)

// Wingo 30 Second Period API Models
data class Wingo30SecPeriodResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: List<Wingo30SecPeriodResult>
)

data class Wingo30SecPeriodResult(
    @SerializedName("datetime") val datetime: String? = null,
    @SerializedName("current") val current: String? = null,
    @SerializedName("period_60") val period60: String? = null
) {
    // Helper property to get the period value regardless of field name
    val periodValue: String
        get() = datetime ?: period60 ?: "2507021733"
}

// Betting API Models
data class BetRequest(
    @SerializedName("userid") val userid: String,
    @SerializedName("bidNum") val bidNum: String,
    @SerializedName("bidType") val bidType: String,
    @SerializedName("period") val period: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("price") val price: String,
    @SerializedName("agree") val agree: String,
    @SerializedName("boost") val boost: String
)

data class BetResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: BetResult?
)

data class BetResult(
    @SerializedName("userid") val userid: String,
    @SerializedName("bidNum") val bidNum: String,
    @SerializedName("bidType") val bidType: String,
    @SerializedName("period") val period: String,
    @SerializedName("price") val price: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("created_at") val createdAt: String
)


// Wingo 30-second Game History API Models
data class GameHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: GameHistoryResult
)

data class GameHistoryResult(
    @SerializedName("history") val history: List<GameHistoryItem>,
    @SerializedName("latest_id") val latestId: String
)

data class GameHistoryItem(
    @SerializedName("id") val id: String,
    @SerializedName("datetime") val datetime: String,
    @SerializedName("number_result") val numberResult: String,
    @SerializedName("color_result") val colorResult: String,
    @SerializedName("big_small_result") val bigSmallResult: String,
    @SerializedName("adminWinNum") val adminWinNum: String,
    @SerializedName("current") val current: String,
    @SerializedName("finalBigSmall") val finalBigSmall: String,
    @SerializedName("finalcolor") val finalcolor: String
)

// Wingo 30-second My History API Models
data class MyHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("result") val result: MyHistoryResult
)

data class MyHistoryResult(
    @SerializedName("history") val history: List<MyHistoryItem>
)

data class MyHistoryItem(
    @SerializedName("id") val id: String,
    @SerializedName("bidNum") val bidNum: String,
    @SerializedName("bidType") val bidType: String,
    @SerializedName("period") val period: String,
    @SerializedName("boost") val boost: String,
    @SerializedName("userid") val userid: String,
    @SerializedName("price") val price: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("agree") val agree: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("number_result") val numberResult: String,
    @SerializedName("color_result") val colorResult: String,
    @SerializedName("big_small_result") val bigSmallResult: String,
    @SerializedName("total") val total: String,
    @SerializedName("resultStatus") val resultStatus: String,
    @SerializedName("winning_amount") val winningAmount: Any, // Can be Int or String
    @SerializedName("adminWinStatus") val adminWinStatus: String,
    @SerializedName("adminNumberResult") val adminNumberResult: String,
    @SerializedName("adminColorResult") val adminColorResult: String,
    @SerializedName("adminBigSmallResult") val adminBigSmallResult: String,
    @SerializedName("adminTotal") val adminTotal: String,
    @SerializedName("dateAdded") val dateAdded: String,
    @SerializedName("calculated_totalAmount") val calculatedTotalAmount: Any // Can be Int or String
)


data class CountdownTimeResponse(
    @SerializedName("minutesTens") val minutesTens: Int,
    @SerializedName("minutesUnits") val minutesUnits: Int,
    @SerializedName("seconds") val seconds: String,
    @SerializedName("raw") val raw: Int
)

// NEW: Latest Row API Models
data class LatestRowResponse(
    @SerializedName("latest_period_id") val latestPeriodId: String
)
