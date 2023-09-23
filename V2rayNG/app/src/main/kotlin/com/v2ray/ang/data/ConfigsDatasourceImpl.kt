package com.v2ray.ang.data

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.dto.ServerConfig
import com.v2ray.ang.util.MmkvManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ConfigsDatasourceImpl @Inject constructor(
    private var storage: MMKV,
    private var serverStorage: MMKV
) : ConfigsDatasource {


    init {
        storage = MMKV.mmkvWithID(MmkvManager.ID_MAIN, MMKV.MULTI_PROCESS_MODE)
        serverStorage = MMKV.mmkvWithID(MmkvManager.ID_SERVER_CONFIG, MMKV.MULTI_PROCESS_MODE)

    }

    private val _configs: MutableStateFlow<List<ServerConfig>> = MutableStateFlow(value = getAllConfigs())
    override val configs: Flow<List<ServerConfig>>
        get() = _configs

    override suspend fun removeConfig(config: ServerConfig) {
        val configs = getAllConfigs().toMutableList()
        configs.remove(config)
        _configs.update { configs }
    }

    override suspend fun updateConfig() {
        TODO("Not yet implemented")
    }

    override suspend fun addConfig(config: ServerConfig) {
        val configs = getAllConfigs().toMutableList()
        configs.add(0, config)
        _configs.update { configs }
    }

    private fun getAllConfigs(): List<ServerConfig> {
        val json = storage.decodeString(MmkvManager.KEY_ANG_CONFIGS)
        val list = if (json.isNullOrBlank()) {
            mutableListOf()
        } else {
            Gson().fromJson(json, Array<String>::class.java).toMutableList()
        }

        return list.mapNotNull { decodeServerConfig(it) }
    }
    fun decodeServerConfig(guid: String): ServerConfig? {
        if (guid.isBlank()) {
            return null
        }
        val json = serverStorage.decodeString(guid)
        if (json.isNullOrBlank()) {
            return null
        }
        return Gson().fromJson(json, ServerConfig::class.java)
    }
}