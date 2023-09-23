package com.v2ray.ang.data

import com.v2ray.ang.dto.AppInfo
import kotlinx.coroutines.flow.Flow

interface AppsDatasource {


    fun supportedApps(): Flow<List<AppInfo>>

    suspend fun enabledApps()


    suspend fun storeApps(list: List<AppInfo>)


}