package com.v2ray.ang.repository

import com.v2ray.ang.datasource.SubscriptionRemoteDatasource

interface SubscriptionRepository {
}


class SubscriptionRepositoryImpl(
    val remoteDatasource: SubscriptionRemoteDatasource,
    val localDatasource: SubscriptionRemoteDatasource,
) : SubscriptionRepository {}