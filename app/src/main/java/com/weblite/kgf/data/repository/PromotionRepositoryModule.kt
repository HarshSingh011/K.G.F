package com.weblite.kgf.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PromotionRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPromotionRepository(
        impl: PromotionRepositoryImpl
    ): PromotionRepository
}
