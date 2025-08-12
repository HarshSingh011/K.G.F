package com.weblite.kgf.data.withdraw

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
