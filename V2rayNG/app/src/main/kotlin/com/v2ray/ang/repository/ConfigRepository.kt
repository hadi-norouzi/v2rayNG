package com.v2ray.ang.repository

import com.v2ray.ang.datasource.ConfigLocalDatasource
import com.v2ray.ang.datasource.ConfigRemoteDatasource
import com.v2ray.ang.dto.ServersCache
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.SpeedtestUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ConfigRepository {
    val configs: StateFlow<List<ServersCache>>

    suspend fun removeConfig(server: ServersCache)

    suspend fun getAllConfigs()

    suspend fun testPing(servers: List<ServersCache>)

    suspend fun realTest(server: List<ServersCache>)
}


class ConfigRepositoryImpl(
    private val datasource: ConfigLocalDatasource,
    private val remoteDatasource: ConfigRemoteDatasource,
) : ConfigRepository {


    private val tcpingTestScope by lazy { CoroutineScope(Dispatchers.IO) }


    override val configs: StateFlow<List<ServersCache>>
        get() = privateConfigs.asStateFlow()

    private val privateConfigs = MutableStateFlow(emptyList<ServersCache>())

    override suspend fun removeConfig(server: ServersCache) {
        val removed = datasource.remove(server)
        if (removed) privateConfigs.value.toMutableList().remove(server)
    }

    override suspend fun getAllConfigs() {
        val data = datasource.getAll()
        privateConfigs.value = data
    }

    override suspend fun testPing(servers: List<ServersCache>) {
        SpeedtestUtil.closeAllTcpSockets()
        MmkvManager.clearAllTestDelayResults()
        var job: Job? = null
        val updatedList = emptyList<ServersCache>()
        for (c in servers) {
            val outbound = c.config.getProxyOutbound() ?: continue
            val serverAddress = outbound.getServerAddress()
            val port = outbound.getServerPort()
            if (serverAddress != null && port != null) {
                job = tcpingTestScope.launch {
                    val testResult = SpeedtestUtil.tcping(serverAddress, port)
                    c.config.ping = testResult
                }
                job.join()
            }
        }
        privateConfigs.value = servers
    }

    override suspend fun realTest(server: List<ServersCache>) {
        TODO("Not yet implemented")
    }


}