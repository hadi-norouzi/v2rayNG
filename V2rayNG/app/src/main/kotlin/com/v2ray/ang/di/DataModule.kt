package com.v2ray.ang.di

import com.v2ray.ang.data.AppsDatasource
import com.v2ray.ang.data.AppsDatasourceImpl
import com.v2ray.ang.data.ConfigsDatasource
import com.v2ray.ang.data.ConfigsDatasourceImpl
import com.v2ray.ang.data.SubscriptionDatasource
import com.v2ray.ang.data.SubscriptionDatasourceImpl
import com.v2ray.ang.data.SubscriptionRemoteDatasource
import com.v2ray.ang.data.SubscriptionRemoteDatasourceImpl
import com.v2ray.ang.domain.ConfigRepository
import com.v2ray.ang.domain.ConfigRepositoryImpl
import com.v2ray.ang.domain.SubscriptionRepository
import com.v2ray.ang.domain.SubscriptionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {


    @Binds
    @Singleton
    abstract fun provideAppDatasource(datasource: AppsDatasourceImpl): AppsDatasource


    @Binds
    @Singleton
    abstract fun provideSubDatasource(datasource: SubscriptionDatasourceImpl): SubscriptionDatasource

    @Binds
    @Singleton
    abstract fun provideSubRemoteDatasource(datasource: SubscriptionRemoteDatasourceImpl): SubscriptionRemoteDatasource


    @Binds
    @Singleton
    abstract fun provideConfigRepository(repository: ConfigRepositoryImpl): ConfigRepository

    @Binds
    @Singleton
    abstract fun provideConfigDatasource(datasource: ConfigsDatasourceImpl): ConfigsDatasource

    @Binds
    @Singleton
    abstract fun provideSubscriptionRepository(repository: SubscriptionRepositoryImpl): SubscriptionRepository
}