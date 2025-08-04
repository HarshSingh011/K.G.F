package com.weblite.kgf.data.models.payments


data class UpiQrResponse(
    val status: String,
    val status_code: Int,
    val msg: String,
    val result: UpiQrResult
)