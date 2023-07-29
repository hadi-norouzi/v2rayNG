package com.v2ray.ang.datasource

import android.net.Uri
import android.util.Log
import com.v2ray.ang.AppConfig
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils

interface SubscriptionDatasource {

    suspend fun add(vararg sub: SubscriptionItem)

    suspend fun remove(vararg sub: SubscriptionItem)

    suspend fun update(sub: SubscriptionItem)

    suspend fun getConfigs(uri: Uri)

    suspend fun getConfigsFromAllSubs(): List<Pair<String, String>>
}


class SubscriptionDatasourceImpl : SubscriptionDatasource {
    override suspend fun add(vararg sub: SubscriptionItem) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(vararg sub: SubscriptionItem) {
        TODO("Not yet implemented")
    }

    override suspend fun update(sub: SubscriptionItem) {
        TODO("Not yet implemented")
    }

    override suspend fun getConfigs(uri: Uri) {
        TODO("Not yet implemented")
    }

    override suspend fun getConfigsFromAllSubs(): List<Pair<String, String>> {
        println("start update all")
        val subs = MmkvManager.decodeSubscriptions()

        val configs = mutableListOf<Pair<String, String>>()

        for (sub in subs) {
            if (!sub.second.enabled) continue
            if (sub.first.isEmpty() or sub.second.remarks.isEmpty() or sub.second.url.isEmpty()) continue

            val url = Utils.idnToASCII(sub.second.url)
            if (!Utils.isValidUrl(url)) continue

            Log.d(AppConfig.ANG_PACKAGE, url)

            val configText = try {
                Utils.getUrlContentWithCustomUserAgent(url)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (configText.isNullOrEmpty()) continue
            configs.add(sub.first to configText)

        }
        return configs
    }

}