package com.weblite.kgf.data.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PromotionApiModule {
    @Provides
    @Singleton
    fun providePromotionApi(retrofit: Retrofit): PromotionApi {
        return retrofit.create(PromotionApi::class.java)
    }
}
