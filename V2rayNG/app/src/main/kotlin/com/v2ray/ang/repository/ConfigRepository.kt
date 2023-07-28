package com.v2ray.ang.repository

import com.v2ray.ang.datasource.ConfigDatasource
import com.v2ray.ang.dto.ServersCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.transform

interface ConfigRepository {
    val configs: Flow<List<ServersCache>>

    suspend fun removeConfig(server: ServersCache)

    suspend fun getAllConfigs()
}


class ConfigRepositoryImpl(
    private val datasource: ConfigDatasource
) : ConfigRepository {
    override val configs: StateFlow<List<ServersCache>>
        get() = datasource.configs

    override suspend fun removeConfig(server: ServersCache) = datasource.remove(server)
    override suspend fun getAllConfigs() = datasource.getAll()


}