package com.v2ray.ang.domain

import com.v2ray.ang.data.ConfigsDatasource
import com.v2ray.ang.dto.ServerConfig
import com.v2ray.ang.util.SpeedtestUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val configsDatasource: ConfigsDatasource,
) : ConfigRepository {
    override val configs: Flow<List<ServerConfig>>
        get() = configsDatasource.configs

    override suspend fun testPingAll() {
        val configs = configsDatasource.getAllConfigs()

        for (config in configs) {
            config.getProxyOutbound()?.let { outbound ->
                val serverAddress = outbound.getServerAddress()
                val serverPort = outbound.getServerPort()
                if (serverAddress != null && serverPort != null) {

                    val speed = SpeedtestUtil.tcping(serverAddress, serverPort)
                    println("speed is $speed")
                    configsDatasource.updateConfig(config.copy(ping = speed))

                }
            }
        }
    }

    override suspend fun updateConfig(vararg configs: ServerConfig) {
        configsDatasource.updateConfig(*configs)
    }

    override suspend fun getAllConfigs(): List<ServerConfig> {
        return configsDatasource.getAllConfigs()
    }
}