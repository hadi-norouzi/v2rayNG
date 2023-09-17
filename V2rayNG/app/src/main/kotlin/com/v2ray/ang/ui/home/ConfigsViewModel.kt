package com.v2ray.ang.ui.home

import androidx.lifecycle.ViewModel
import com.v2ray.ang.dto.ServerConfig
import com.v2ray.ang.dto.ServersCache
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.util.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConfigsViewModel : ViewModel() {


    private val _subscriptions: MutableStateFlow<List<Pair<String, SubscriptionItem>>> =
        MutableStateFlow(value = listOf())

    val subscriptions = _subscriptions.asStateFlow()


    private val _configs: MutableStateFlow<MutableList<MutableList<ServerConfig>>> =
        MutableStateFlow(value = mutableListOf())

    val configs = _configs.asStateFlow()


    init {

        getSubs()
        getConfigs()
    }

    private fun getSubs() {
        val subs = MmkvManager.decodeSubscriptions()
        _subscriptions.value = subs
    }

    private fun getConfigs() {
        val serverList = MmkvManager.decodeServerList()
        val list: MutableList<MutableList<ServerConfig>> = MutableList(serverList.size) { mutableListOf() }
        for ((index, guid) in serverList.withIndex()) {
            val config = MmkvManager.decodeServerConfig(guid) ?: continue
            if (config.subscriptionId.isEmpty()) {
                list[0].add(config)
            } else {


            }
        }
        _configs.update { list }
        print("configs ${_configs.value}")
    }
}