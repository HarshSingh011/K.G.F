package com.weblite.kgf.di

import com.weblite.kgf.Api.PaymentApiService
import com.weblite.kgf.data.repository.PaymentRespositories.UpiQrRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUpiQrRepository(paymentApiService: PaymentApiService): UpiQrRepository {
        return UpiQrRepository(paymentApiService)
    }
}
