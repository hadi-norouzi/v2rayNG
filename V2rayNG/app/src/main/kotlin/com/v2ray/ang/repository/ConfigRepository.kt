package com.v2ray.ang.repository

import com.v2ray.ang.datasource.ConfigDatasource
import com.v2ray.ang.dto.ServersCache
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    val configs: Flow<List<ServersCache>>

    suspend fun removeConfig(server: ServersCache)
}


class ConfigRepositoryImpl(
    private val datasource: ConfigDatasource
) : ConfigRepository {
    override val configs: Flow<List<ServersCache>>
        get() = datasource.configs

    override suspend fun removeConfig(server: ServersCache) = datasource.remove(server)


}