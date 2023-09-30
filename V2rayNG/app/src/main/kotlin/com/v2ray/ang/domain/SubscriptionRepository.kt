package com.v2ray.ang.domain

import com.v2ray.ang.dto.SubscriptionItem
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {

    val subscriptions: Flow<List<SubscriptionItem>>

    suspend fun getSubscriptionData(subId: String)
    suspend fun getSubscriptionData(subscriptionItem: SubscriptionItem)

    suspend fun addSubscription(item: SubscriptionItem)

    suspend fun removeSubscription(item: SubscriptionItem)

    suspend fun updateSubscription(item: SubscriptionItem)

}