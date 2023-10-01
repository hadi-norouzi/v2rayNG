package com.v2ray.ang.domain

import android.text.TextUtils
import android.util.Log
import com.v2ray.ang.AppConfig
import com.v2ray.ang.data.ConfigsDatasource
import com.v2ray.ang.data.SubscriptionDatasource
import com.v2ray.ang.data.SubscriptionRemoteDatasource
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionDatasource: SubscriptionDatasource,
    private val subscriptionRemoteDatasource: SubscriptionRemoteDatasource,
    private val configsDatasource: ConfigsDatasource,
) : SubscriptionRepository {
    override val subscriptions: Flow<List<SubscriptionItem>>
        get() = subscriptionDatasource.subscriptions

    override suspend fun getSubscriptionData(subId: String) {
        val sub = MmkvManager.decodeSubscriptions().firstOrNull { subId == it.first } ?: return
        getSubscriptionData(sub.second)
    }

    override suspend fun getSubscriptionData(subscriptionItem: SubscriptionItem) {
        try {
            if (TextUtils.isEmpty(subscriptionItem.id) || TextUtils.isEmpty(subscriptionItem.remarks) || TextUtils.isEmpty(
                    subscriptionItem.url
                )
            ) {
                return
            }
            val url = Utils.idnToASCII(subscriptionItem.url)
            if (!Utils.isValidUrl(url)) return

            Log.d(AppConfig.ANG_PACKAGE, url)

            val configText = subscriptionRemoteDatasource.getSubscriptionData(subscriptionItem)
            println(configText)

            val configs = configText.lines()
                .reversed()

            configs.forEach {
                configsDatasource.addConfig(it)
            }
            upsertSubscription(
                subscriptionItem.copy(
                    updatedAt = System.currentTimeMillis(),
                )
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun removeSubscription(item: SubscriptionItem) =
        subscriptionDatasource.removeSubscription(item)

    override suspend fun upsertSubscription(item: SubscriptionItem) =
        subscriptionDatasource.upsertSubscription(item)

    override suspend fun getSubscriptionById(id: String): SubscriptionItem =
        subscriptionDatasource.getSubscriptionById(id)
}