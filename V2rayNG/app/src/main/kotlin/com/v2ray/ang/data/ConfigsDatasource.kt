package com.v2ray.ang.data

import com.v2ray.ang.dto.ServerConfig
import kotlinx.coroutines.flow.Flow

interface ConfigsDatasource {

    val configs: Flow<List<ServerConfig>>

    suspend fun removeConfig(config: ServerConfig)

    suspend fun updateConfig()

    suspend fun addConfig(config: ServerConfig)
}