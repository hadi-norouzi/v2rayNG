package com.v2ray.ang.domain

import com.v2ray.ang.dto.ServerConfig
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {


    val configs: Flow<List<ServerConfig>>

    val selectedConfig: Flow<ServerConfig?>
    suspend fun testPingAll()

    suspend fun updateConfig(vararg configs: ServerConfig)

    suspend fun getAllConfigs(): List<ServerConfig>

    suspend fun selectConfig(item: ServerConfig)


}