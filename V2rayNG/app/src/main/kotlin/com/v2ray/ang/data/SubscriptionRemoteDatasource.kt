package com.v2ray.ang.data

import com.v2ray.ang.dto.SubscriptionItem

interface SubscriptionRemoteDatasource {

    suspend fun getSubscriptionData(item: SubscriptionItem): String
}