package com.weblite.kgf.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import com.weblite.kgf.data.remote.api.SendMessageRequest
import com.weblite.kgf.data.remote.api.SendMessageResponse
import com.weblite.kgf.data.remote.api.CustomerMessagesResponse


interface SupportApi {
    @POST("web/Api/sendMassage")
    suspend fun sendMessage(@Body request: SendMessageRequest): SendMessageResponse

    @GET("web/Api/CustomerMsg")
    suspend fun getCustomerMessages(@Query("user_id") userId: String): CustomerMessagesResponse
}
