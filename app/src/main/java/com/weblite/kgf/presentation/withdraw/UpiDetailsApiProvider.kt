package com.weblite.kgf.presentation.withdraw

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UpiDetailsApiProvider {
    val api: UpiDetailsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://newkgfindia.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UpiDetailsApi::class.java)
    }
}
