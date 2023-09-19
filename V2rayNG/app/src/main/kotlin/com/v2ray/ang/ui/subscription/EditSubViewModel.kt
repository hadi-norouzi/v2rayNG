package com.v2ray.ang.ui.subscription

import android.text.TextUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.R
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.extension.toast
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditSubViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {

    private val subStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_SUB, MMKV.MULTI_PROCESS_MODE) }


    private val subId: String? = state["subId"]


    private val _subItem: MutableStateFlow<SubscriptionItem?> = MutableStateFlow(value = null)
    val subItem = _subItem.asStateFlow()


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


    fun saveServer(
        name: String,
        url: String,

        ): Boolean {
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
        subItem.enabled = false
        subItem.autoUpdate = false

        if (TextUtils.isEmpty(subItem.remarks)) {
//            toast(R.string.sub_setting_remarks)
            return false
        }
//        if (TextUtils.isEmpty(subItem.url)) {
//            toast(R.string.sub_setting_url)
//            return false
//        }

        subStorage?.encode(subId, Gson().toJson(subItem))
//        toast(R.string.toast_success)
//        finish()
        return true
    }

}