package com.v2ray.ang.datasource

import android.net.Uri

interface SubscriptionRemoteDatasource {


    suspend fun fetchConfigsFromUri(uri: Uri)

}