package com.v2ray.ang.datasource

import com.v2ray.ang.dto.ServersCache

interface ConfigRemoteDatasource {

    suspend fun testPing(servers: List<ServersCache>)

    suspend fun testReal(servers: List<ServersCache>)
}

class ConfigRemoteDatasourceImpl(

): ConfigRemoteDatasource {
    override suspend fun testPing(servers: List<ServersCache>) {
        TODO("Not yet implemented")
    }

    override suspend fun testReal(servers: List<ServersCache>) {
        TODO("Not yet implemented")
    }
}