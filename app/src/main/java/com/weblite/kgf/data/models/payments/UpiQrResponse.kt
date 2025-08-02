package com.weblite.kgf.data.model

data class UpiQrResponse(
    val status: String,
    val status_code: Int,
    val msg: String,
    val result: UpiQrResult
)
