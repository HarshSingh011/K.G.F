package com.weblite.kgf.presentation.withdraw

import retrofit2.http.Body
import retrofit2.http.POST

interface UpiDetailsApi {
    @POST("web/Api/userUpiDetails")
    suspend fun updateUpiDetails(@Body request: UpiDetailsRequest): UpiDetailsResponse
}
