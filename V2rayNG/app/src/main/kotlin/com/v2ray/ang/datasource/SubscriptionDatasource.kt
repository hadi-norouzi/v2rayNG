package com.v2ray.ang.datasource

import android.net.Uri
import android.util.Log
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.extension.toast
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface SubscriptionDatasource {

    suspend fun add()

    suspend fun remove()

    suspend fun update()

    suspend fun getConfigs(uri: Uri)

    suspend fun getConfigsFromAllSubs(): List<Pair<String, String>>
}


class SubscriptionDatasourceImpl : SubscriptionDatasource {
    override suspend fun add() {
        TODO("Not yet implemented")
    }

    override suspend fun remove() {
        TODO("Not yet implemented")
    }

    override suspend fun update() {
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

            val configText = withContext(Dispatchers.IO) {
                return@withContext try {
                    Utils.getUrlContentWithCustomUserAgent(url)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            if (configText.isNullOrEmpty()) continue
            configs.add(sub.first to configText)

        }
        return configs
    }

}