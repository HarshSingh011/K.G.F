package com.weblite.kgf.data.withdraw

data class AddBankAccountRequest(
    val bank: String,
    val recipient_name: String,
    val account_number: String,
    val phone_number: String,
    val ifsc_code: String,
    val user_id: String
)

data class AddBankAccountResponse(
    val status: String?,
    val status_code: Int?,
    val msg: String?,
    val result: AddBankAccountResult?
)

data class AddBankAccountResult(
    val user_sl: String?,
    val contact_no: String?,
    val bank_name: String?,
    val name: String?,
    val account_no: String?,
    val ifsc_code: String?,
    val curr_dt: String?
)
