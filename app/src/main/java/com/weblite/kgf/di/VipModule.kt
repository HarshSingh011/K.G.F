package com.weblite.kgf.di

import com.weblite.kgf.data.remote.api.VipApi
import com.weblite.kgf.data.remote.vip.VipApiProvider
import com.weblite.kgf.data.repository.VipRepositoryImpl
import com.weblite.kgf.domain.repository.VipRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VipModule {
    @Provides
    @Singleton
    fun provideVipApi(): VipApi = VipApiProvider.api

    @Provides
    @Singleton
    fun provideVipRepository(api: VipApi): VipRepository = VipRepositoryImpl(api)
}
