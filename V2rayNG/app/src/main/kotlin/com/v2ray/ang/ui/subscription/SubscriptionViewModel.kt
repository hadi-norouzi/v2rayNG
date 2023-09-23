package com.v2ray.ang.ui.subscription

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.extension.toast
import com.v2ray.ang.util.AngConfigManager
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class SubscriptionViewModel : ViewModel() {


    private val _subscriptions: MutableStateFlow<List<Pair<String, SubscriptionItem>>> =
        MutableStateFlow(value = listOf())

    val subscriptions = _subscriptions.asStateFlow()

    private val _subscriptionUpdate: MutableStateFlow<String?> = MutableStateFlow(value = null)
    val subscriptionUpdate = _subscriptionUpdate.asStateFlow()

    init {
        getSubs()
    }

    private fun getSubs() {
        val subs = MmkvManager.decodeSubscriptions()
        _subscriptions.value = subs
    }

    fun reloadSubs() {
        getSubs()
    }

    private fun setLoadingState(item: SubscriptionItem) {
        val sub = _subscriptions.value.find { it.second == item } ?: return



    }


    fun updateSubscription(subId: String) {
        try {
            val sub = MmkvManager.decodeSubscriptions().firstOrNull { subId == it.first } ?: return
            if (TextUtils.isEmpty(sub.first) || TextUtils.isEmpty(sub.second.remarks) || TextUtils.isEmpty(sub.second.url)) {
                return
            }
            val url = Utils.idnToASCII(sub.second.url)
            if (!Utils.isValidUrl(url)) {
                return
            }
            Log.d(AppConfig.ANG_PACKAGE, url)
            viewModelScope.launch(Dispatchers.IO) {
                val configText = try {
                    Utils.getUrlContentWithCustomUserAgent(url)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@launch
                }
                println(configText)
                launch(Dispatchers.Main) {
                    importConfigs(configText, sub.first)
                    _subscriptionUpdate.emit("Subscription ${sub.second.remarks} Updated")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun importConfigs(server: String?, subid: String = "") {
        var count = AngConfigManager.importBatchConfig(server, subid, true)
        if (count <= 0) {
            count = AngConfigManager.importBatchConfig(Utils.decode(server!!), subid, true)
        }
    }
}