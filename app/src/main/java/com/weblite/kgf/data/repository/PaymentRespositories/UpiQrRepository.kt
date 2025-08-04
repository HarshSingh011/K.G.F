package com.weblite.kgf.data.repository.PaymentRespositories

import com.weblite.kgf.Api.PaymentApiService
import com.weblite.kgf.data.models.payments.DepositRequest
import com.weblite.kgf.data.models.payments.DepositResponse
import com.weblite.kgf.data.models.payments.DepositHistoryResponse
import com.weblite.kgf.data.models.payments.UpiQrResponse
import retrofit2.Response

// Repository
class UpiQrRepository(private val api: PaymentApiService) {
    suspend fun fetchUpiQr(): Response<UpiQrResponse> = api.getUpiQr()

    suspend fun storeDeposit(request: DepositRequest): Response<DepositResponse> = api.storeDeposit(request)

    suspend fun getDepositHistory(userId: String): Response<DepositHistoryResponse> = api.getDepositHistory(userId)
}
