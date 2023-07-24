package com.v2ray.ang.repo

import com.v2ray.ang.datasource.ConfigDatasource
import com.v2ray.ang.dto.ServersCache
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    val configs: Flow<List<ServersCache>>
}


class ConfigRepositoryImpl(
    private val datasource: ConfigDatasource
) : ConfigRepository {
    override val configs: Flow<List<ServersCache>>
        get() = datasource.configs
}