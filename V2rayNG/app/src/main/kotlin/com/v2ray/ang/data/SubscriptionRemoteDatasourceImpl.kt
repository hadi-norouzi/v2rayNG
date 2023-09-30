package com.v2ray.ang.data

import com.v2ray.ang.BuildConfig
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

class SubscriptionRemoteDatasourceImpl @Inject constructor() : SubscriptionRemoteDatasource {
    override suspend fun getSubscriptionData(item: SubscriptionItem): String {
        val url = URL(item.url)
        val conn = withContext(Dispatchers.IO) {
            url.openConnection()
        }
        conn.setRequestProperty("Connection", "close")
        conn.setRequestProperty("User-agent", "v2rayNG/${BuildConfig.VERSION_NAME}")
        url.userInfo?.let {
            conn.setRequestProperty("Authorization",
                "Basic ${Utils.encode(Utils.urlDecode(it))}")
        }
        conn.useCaches = false
        return conn.inputStream.use {
            it.bufferedReader().readText()
        }
    }
}