package com.v2ray.ang.datasource

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.dto.ServerConfig
import com.v2ray.ang.dto.ServersCache
import com.v2ray.ang.util.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface ConfigLocalDatasource {

    val configs: StateFlow<List<ServersCache>>

    suspend fun getAll(filter: String? = null): List<ServersCache>

    suspend fun remove(index: Int)

    suspend fun remove(config: ServersCache): Boolean

    suspend fun update(index: Int, newConfig: ServersCache)

    suspend fun update(config: ServersCache, newConfig: ServersCache)
}

class ConfigLocalDatasourceImpl : ConfigLocalDatasource {
    private val TAG = "ConfigDatasource"
    private val mainStorage by lazy {
        MMKV.mmkvWithID(
            MmkvManager.ID_MAIN,
            MMKV.MULTI_PROCESS_MODE
        )
    }
//    override val configs: Flow<List<ServersCache>> = flow {
//        val json = mainStorage?.decodeString(MmkvManager.KEY_ANG_CONFIGS)
//        val configs: MutableList<String> = if (json.isNullOrBlank()) {
//            mutableListOf()
//        } else {
//            Gson().fromJson(json, Array<String>::class.java).toMutableList()
//        }
//        val serversCache = mutableListOf<ServersCache>()
//
//        for (guid in configs) {
//            val config = MmkvManager.decodeServerConfig(guid) ?: continue
////            if (subscriptionId.isNotEmpty() && subscriptionId != config.subscriptionId) {
////                continue
////            }
//
//            serversCache.add(ServersCache(guid, config))
////            if (keywordFilter.isEmpty() || config.remarks.contains(keywordFilter)) {
////            }
//        }
//        emit(serversCache)
//    }.flowOn(Dispatchers.IO)


    private val stateConfigs = MutableStateFlow(emptyList<ServersCache>())

    override val configs: StateFlow<List<ServersCache>>
        get() = stateConfigs

    override suspend fun getAll(filter: String?): List<ServersCache> {
        val json = mainStorage?.decodeString(MmkvManager.KEY_ANG_CONFIGS)
        val configs: MutableList<String> = if (json.isNullOrBlank()) {
            mutableListOf()
        } else {
            Gson().fromJson(json, Array<String>::class.java).toMutableList()
        }
        var serversCache = mutableListOf<ServersCache>()

        for (guid in configs) {
            val config = MmkvManager.decodeServerConfig(guid) ?: continue
//            if (subscriptionId.isNotEmpty() && subscriptionId != config.subscriptionId) {
//                continue
//            }
            serversCache.add(ServersCache(guid, config))
        }
        if (!filter.isNullOrEmpty()) {
            serversCache =
                serversCache.filter { it.config.remarks.contains(filter) }.toMutableList()
        }
        return serversCache
    }

    override suspend fun remove(index: Int) {
        stateConfigs.value = emptyList()
    }

    override suspend fun remove(config: ServersCache): Boolean {

        getAll().firstOrNull {
            config.guid == it.guid
        }?.let { server ->
            MmkvManager.removeServer(server.guid)
            stateConfigs.update {
                val updated = it.toMutableList()
                updated.remove(server)
                updated
            }
            return true
        }
        return false

    }

    override suspend fun update(index: Int, newConfig: ServersCache) {
        TODO("Not yet implemented")
    }

    override suspend fun update(config: ServersCache, newConfig: ServersCache) {
        TODO("Not yet implemented")
    }
}