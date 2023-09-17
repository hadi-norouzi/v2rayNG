package com.v2ray.ang.ui.subscription

import androidx.lifecycle.ViewModel
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.util.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SubscriptionViewModel : ViewModel() {


    private val _subscriptions: MutableStateFlow<List<Pair<String, SubscriptionItem>>> = MutableStateFlow(value = listOf())

    val subscriptions = _subscriptions.asStateFlow()

    init {
        getSubs()
    }

    private fun getSubs() {
        val subs = MmkvManager.decodeSubscriptions()
        _subscriptions.value = subs
    }


}