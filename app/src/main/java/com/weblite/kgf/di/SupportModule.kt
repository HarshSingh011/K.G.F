package com.weblite.kgf.di

import com.weblite.kgf.data.remote.api.SupportApi
import com.weblite.kgf.data.repository.SupportRepositoryImpl
import com.weblite.kgf.domain.repository.SupportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupportModule {
    @Provides
    @Singleton
    fun provideSupportApi(retrofit: Retrofit): SupportApi =
        retrofit.create(SupportApi::class.java)

    @Provides
    @Singleton
    fun provideSupportRepository(api: SupportApi): SupportRepository =
        SupportRepositoryImpl(api)
}
