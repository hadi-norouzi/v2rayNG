package com.v2ray.ang.data

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SubscriptionDatasourceImpl @Inject constructor() : SubscriptionDatasource {


    private val subStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_SUB, MMKV.MULTI_PROCESS_MODE) }


    override val subscriptions: Flow<List<SubscriptionItem>>
        get() = _subs


    private val _subs: MutableStateFlow<List<SubscriptionItem>> = MutableStateFlow(value = listOf())


    init {
        getAll()
    }

    private fun getAll() {
        _subs.value = getAllSubs()
    }

    private fun getAllSubs(): List<SubscriptionItem> {
        val subs = MmkvManager.decodeSubscriptions()
        return subs.map { it.second.copy(id = it.first) }
    }

    override suspend fun removeSubscription(item: SubscriptionItem) {
        val allSubs = getAllSubs().toMutableList()
        allSubs.removeAll { it.id == item.id }
        _subs.update { allSubs }
        subStorage.remove(item.id)
    }

    override suspend fun upsertSubscription(item: SubscriptionItem) {
        val allSubs = getAllSubs().toMutableList()
        val index = allSubs.indexOfFirst { it.id == item.id }
        if (index == -1) {
            val newId = Utils.getUuid()
            val newItem = item.copy(id = newId)
            allSubs.add(newItem)
            subStorage?.encode(newId, Gson().toJson(item))
        } else {
            allSubs[index] = item
            subStorage?.encode(item.id, Gson().toJson(item))
        }
        _subs.update { allSubs }
    }

    override suspend fun getSubscriptionById(id: String): SubscriptionItem {
        val allSubs = getAllSubs().toMutableList()
        return allSubs.find { it.id == id } ?: throw Exception("Not found")
    }
}