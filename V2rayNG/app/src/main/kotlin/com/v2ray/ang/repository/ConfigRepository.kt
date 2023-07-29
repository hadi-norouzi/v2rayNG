package com.v2ray.ang.repository

import com.v2ray.ang.datasource.ConfigLocalDatasource
import com.v2ray.ang.datasource.ConfigRemoteDatasource
import com.v2ray.ang.dto.ServersCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ConfigRepository {
    val configs: Flow<List<ServersCache>>

    suspend fun removeConfig(server: ServersCache)

    suspend fun getAllConfigs()

    suspend fun testPing(servers: List<ServersCache>)

    suspend fun realTest(server: List<ServersCache>)
}


class ConfigRepositoryImpl(
    private val datasource: ConfigLocalDatasource,
    private val remoteDatasource: ConfigRemoteDatasource,
) : ConfigRepository {
    override val configs: StateFlow<List<ServersCache>>
        get() = datasource.configs

    override suspend fun removeConfig(server: ServersCache) = datasource.remove(server)
    override suspend fun getAllConfigs() = datasource.getAll()
    override suspend fun testPing(servers: List<ServersCache>) {
        TODO("Not yet implemented")
    }

    override suspend fun realTest(server: List<ServersCache>) {
        TODO("Not yet implemented")
    }


}