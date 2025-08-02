package com.weblite.kgf.data.repository

import com.weblite.kgf.Api.PaymentApiService
import com.weblite.kgf.data.model.UpiQrResponse
import com.weblite.kgf.data.model.DepositRequest
import com.weblite.kgf.data.model.DepositResponse
import retrofit2.Response

// Repository
class UpiQrRepository(private val api: PaymentApiService) {
    suspend fun fetchUpiQr(): Response<UpiQrResponse> = api.getUpiQr()

    suspend fun storeDeposit(request: DepositRequest): Response<DepositResponse> = api.storeDeposit(request)
}
