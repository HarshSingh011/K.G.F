package com.weblite.kgf.data.remote.vip

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.weblite.kgf.data.remote.api.VipApi

object VipApiProvider {
    private const val BASE_URL = "https://newkgfindia.com/"

    val api: VipApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VipApi::class.java)
    }
}
