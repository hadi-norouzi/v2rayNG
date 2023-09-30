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

    override suspend fun addSubscription(item: SubscriptionItem) {
        val allSubs = getAllSubs().toMutableList()

        val newItem = item.copy(id = Utils.getUuid())

        subStorage?.encode(newItem.id, Gson().toJson(item))

        allSubs.add(newItem)
        _subs.update { allSubs }
    }

    override suspend fun removeSubscription(item: SubscriptionItem) {
        val allSubs = getAllSubs().toMutableList()
        allSubs.removeAll { it.id == item.id }
        _subs.update { allSubs }
        MmkvManager.removeSubscription(item.id)
    }

    override suspend fun updateSubscription(item: SubscriptionItem) {
        val allSubs = getAllSubs().toMutableList()
        val index = allSubs.indexOfFirst { it.id == item.id }
        if (index == -1) return
        allSubs[index] = item
        _subs.update { allSubs }
    }
}