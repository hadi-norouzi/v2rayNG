package com.v2ray.ang.data

import com.v2ray.ang.dto.SubscriptionItem
import kotlinx.coroutines.flow.Flow

interface SubscriptionDatasource {


    val subscriptions: Flow<List<SubscriptionItem>>


    suspend fun addSubscription(item: SubscriptionItem)

    suspend fun removeSubscription(item: SubscriptionItem)


    suspend fun updateSubscription(item: SubscriptionItem)


}