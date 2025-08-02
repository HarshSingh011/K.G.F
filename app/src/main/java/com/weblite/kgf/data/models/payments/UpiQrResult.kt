package com.weblite.kgf.data.model

data class UpiQrResult(
    val id: String,
    val upi_id: String,
    val qr_code: String,
    val created_at: String,
    val status: String
)
