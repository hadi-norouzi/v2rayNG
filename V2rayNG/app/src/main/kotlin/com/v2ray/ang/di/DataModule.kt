package com.v2ray.ang.di

import com.v2ray.ang.data.AppsDatasource
import com.v2ray.ang.data.AppsDatasourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {


    @Binds
    @Singleton
    abstract fun provideAppDatasource(datasource: AppsDatasourceImpl): AppsDatasource
}