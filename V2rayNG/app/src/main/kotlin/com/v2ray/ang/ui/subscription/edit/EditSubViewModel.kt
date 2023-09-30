package com.v2ray.ang.ui.subscription.edit

import android.text.TextUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.data.SubscriptionDatasource
import com.v2ray.ang.domain.SubscriptionRepository
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class EditSubViewModel @Inject constructor(
    state: SavedStateHandle,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val subStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_SUB, MMKV.MULTI_PROCESS_MODE) }


    private val subId: String? = state["subId"]


    private val _subItem: MutableStateFlow<SubscriptionItem?> = MutableStateFlow(value = null)
    val subItem = _subItem.asStateFlow()


    private val _state: MutableStateFlow<SubscriptionState> = MutableStateFlow(value = SubscriptionState.Initial)
    val state = _state.asStateFlow()


    init {
        getSubscriptionItem()
    }


    private fun getSubscriptionItem() {
        val subs = MmkvManager.decodeSubscriptions()
        val sub = subs.firstOrNull { it.first == subId }
        sub?.let {
            _subItem.value = it.second
        }
    }

    fun removeSubscription() {

    }

    fun saveServer(
        name: String,
        url: String,
    ) {
        val subItem: SubscriptionItem
        val json = subStorage?.decodeString(subId)
        var subId = subId
        if (!json.isNullOrBlank()) {
            subItem = Gson().fromJson(json, SubscriptionItem::class.java)
        } else {
            subId = Utils.getUuid()
            subItem = SubscriptionItem()
        }

        subItem.remarks = name
        subItem.url = url
        subItem.enabled = true
        subItem.autoUpdate = true

        if (TextUtils.isEmpty(subItem.remarks)) {
            return
        }
        viewModelScope.launch {
            try {
                if (subId == null) {
                    subscriptionRepository.addSubscription(subItem)
                    _state.value = SubscriptionState.Added(subItem)
                } else {
                    subscriptionRepository.updateSubscription(subItem)
                    _state.value = SubscriptionState.Updated(subItem)
                }
            } catch (e: Exception) {
                _state.value = SubscriptionState.Failed
            }
        }
    }

}