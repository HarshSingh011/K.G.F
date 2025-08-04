package com.weblite.kgf.Api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import com.weblite.kgf.data.models.payments.DepositRequest
import com.weblite.kgf.data.models.payments.DepositResponse
import com.weblite.kgf.data.models.payments.DepositHistoryResponse
import com.weblite.kgf.data.models.payments.UpiQrResponse
import retrofit2.http.Query

interface PaymentApiService {
    @GET("web/Api/UPI_qrcode")
    suspend fun getUpiQr(): Response<UpiQrResponse>

    @POST("web/Api/storeDeposite")
    suspend fun storeDeposit(@Body request: DepositRequest): Response<DepositResponse>

    @GET("web/Api/depositeHistory")
    suspend fun getDepositHistory(@Query("user_id") userId: String): Response<DepositHistoryResponse>
}