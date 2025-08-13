package com.weblite.kgf.data.withdraw

data class UpdateUpiDetailsRequest(
    val user_id: String,
    val name: String,
    val upi_id: String,
    val upi_provider: String
)

data class UpdateUpiDetailsResponse(
    val status: Boolean,
    val message: String
)

data class UpdateAccountDetailsRequest(
    val user_id: String,
    val bank: String,
    val recipient_name: String,
    val account_number: String,
    val phone_number: String,
    val ifsc_code: String
)

data class UpdateAccountDetailsResponse(
    val status: Boolean,
    val message: String
)


data class BankDetailsResult(
    val id: String?,
    val user_sl: String?,
    val name: String?,
    val contact_no: String?,
    val account_no: String?,
    val ifsc_code: String?,
    val curr_dt: String?,
    val bank_name: String?,
    val upi_id: String?,
    val upi_provider: String?
)

data class BankDetailsState(
    val loading: Boolean = false,
    val error: String? = null,
    val result: BankDetailsResult? = null
)
