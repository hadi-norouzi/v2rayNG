package com.v2ray.ang.datasource

import android.util.Log
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.dto.ServersCache
import com.v2ray.ang.util.MmkvManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ConfigDatasource {

    val configs: Flow<List<ServersCache>>

    fun getAll(filter: String? = null): Flow<List<ServersCache>>

    suspend fun remove(index: Int)

    suspend fun remove(config: ServersCache)

    suspend fun update(index: Int, newConfig: ServersCache)

    suspend fun update(config: ServersCache, newConfig: ServersCache)
}

class ConfigDatasourceImpl : ConfigDatasource {
    private val TAG = "ConfigDatasource"
    private val mainStorage by lazy {
        MMKV.mmkvWithID(
            MmkvManager.ID_MAIN,
            MMKV.MULTI_PROCESS_MODE
        )
    }
    override val configs: Flow<List<ServersCache>> = flow {
        val json = mainStorage?.decodeString(MmkvManager.KEY_ANG_CONFIGS)
        val configs: MutableList<String> = if (json.isNullOrBlank()) {
            mutableListOf()
        } else {
            Gson().fromJson(json, Array<String>::class.java).toMutableList()
        }
        val serversCache = mutableListOf<ServersCache>()

        for (guid in configs) {
            val config = MmkvManager.decodeServerConfig(guid) ?: continue
//            if (subscriptionId.isNotEmpty() && subscriptionId != config.subscriptionId) {
//                continue
//            }

            serversCache.add(ServersCache(guid, config))
//            if (keywordFilter.isEmpty() || config.remarks.contains(keywordFilter)) {
//            }
        }
        emit(serversCache)
    }

    override fun getAll(filter: String?): Flow<List<ServersCache>> {
        TODO("Not yet implemented")
    }


    //    override  fun getAll(filter: String?) :Flow<List<ServersCache>>=flow {
//        val json = mainStorage?.decodeString(MmkvManager.KEY_ANG_CONFIGS)
//        val configs: MutableList<String> = if (json.isNullOrBlank()) {
//            mutableListOf()
//        } else {
//            Gson().fromJson(json, Array<String>::class.java).toMutableList()
//        }
//
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
//        Log.d(TAG, "getAllConfigs: $serversCache")
//        serversCache.asFlow()
//    }
//    override fun getAll(filter: String?): Flow<List<ServersCache>> = flow{
//
//    }.flowOn(Dispatchers.IO)

    override suspend fun remove(index: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(config: ServersCache) {
        TODO("Not yet implemented")
    }

    override suspend fun update(index: Int, newConfig: ServersCache) {
        TODO("Not yet implemented")
    }

    override suspend fun update(config: ServersCache, newConfig: ServersCache) {
        TODO("Not yet implemented")
    }
}