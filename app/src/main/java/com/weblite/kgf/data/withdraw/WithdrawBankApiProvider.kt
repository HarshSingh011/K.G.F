package com.weblite.kgf.data.withdraw

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WithdrawBankApiProvider {
    private const val BASE_URL = "https://newkgfindia.com/"

    val api: WithdrawBankApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WithdrawBankApi::class.java)
    }
}
