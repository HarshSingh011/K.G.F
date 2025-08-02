package com.weblite.kgf.Api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import com.weblite.kgf.data.model.UpiQrResponse
import com.weblite.kgf.data.model.DepositRequest
import com.weblite.kgf.data.model.DepositResponse

interface PaymentApiService {
    @GET("web/Api/UPI_qrcode")
    suspend fun getUpiQr(): Response<UpiQrResponse>

    @POST("web/Api/storeDeposite")
    suspend fun storeDeposit(@Body request: DepositRequest): Response<DepositResponse>
}