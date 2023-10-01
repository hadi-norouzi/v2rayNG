package com.v2ray.ang.data

import com.v2ray.ang.dto.ServerConfig
import kotlinx.coroutines.flow.Flow

interface ConfigsDatasource {

    val configs: Flow<List<ServerConfig>>

    val selectedConfig: Flow<ServerConfig?>

    suspend fun removeConfig(config: ServerConfig)

    suspend fun updateConfig(vararg configs: ServerConfig)

    suspend fun addConfig(vararg config: ServerConfig)

    suspend fun addConfig(url: String, allowInsecure: Boolean = false): Int

    fun getAllConfigs(): List<ServerConfig>


    fun selectConfig(item: ServerConfig)
}