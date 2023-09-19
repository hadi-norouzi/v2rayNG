package com.v2ray.ang.ui.home

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import com.v2ray.ang.AngApplication
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.dto.ServerConfig
import com.v2ray.ang.dto.ServersCache
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.extension.toast
import com.v2ray.ang.service.V2RayServiceManager
import com.v2ray.ang.util.MessageUtil
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.SpeedtestUtil
import com.v2ray.ang.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConfigsViewModel @Inject constructor (application: Application) : AndroidViewModel(application) {

    private val mainStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_MAIN, MMKV.MULTI_PROCESS_MODE) }
    private val settingsStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_SETTING, MMKV.MULTI_PROCESS_MODE) }


    private val _subscriptions: MutableStateFlow<List<Pair<String, SubscriptionItem>>> =
        MutableStateFlow(value = listOf())

    val subscriptions = _subscriptions.asStateFlow()


    private val _configs: MutableStateFlow<MutableList<ServerConfig>> =
        MutableStateFlow(value = mutableListOf())

    val configs = _configs.asStateFlow()


    private val _isRunning = MutableStateFlow(value = false)
    val running = _isRunning.asStateFlow()

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
        val list = mutableListOf<ServerConfig>()
//        val list: MutableList<MutableList<ServerConfig>> = MutableList(serverList.size) { mutableListOf() }
//        for ((index, guid) in serverList.withIndex()) {
//            val config = MmkvManager.decodeServerConfig(guid) ?: continue
//            if (config.subscriptionId.isEmpty()) {
//                list[0].add(config)
//            } else {
//
//
//
//            }
//        }
        for (guid in serverList) {
            val config = MmkvManager.decodeServerConfig(guid) ?: continue
            list.add(config)
        }
        _configs.update { list }
        print("configs ${_configs.value}")
    }

    fun startListenBroadcast() {
        _isRunning.value = false
        getApplication<AngApplication>().registerReceiver(mMsgReceiver, IntentFilter(AppConfig.BROADCAST_ACTION_ACTIVITY))
        MessageUtil.sendMsg2Service(getApplication(), AppConfig.MSG_REGISTER_CLIENT, "")
    }

    fun startVpn() {
        if (_isRunning.value) {
            Utils.stopVService(getApplication())
        } else if (settingsStorage?.decodeString(AppConfig.PREF_MODE) ?: "VPN" == "VPN") {
            val intent = VpnService.prepare(getApplication())
            if (intent == null) {
                startV2Ray()
            } else {
//                requestVpnPermission.launch(intent)
            }
        } else {
            startV2Ray()
        }
    }

    fun startV2Ray() {
        if (mainStorage?.decodeString(MmkvManager.KEY_SELECTED_SERVER).isNullOrEmpty()) {
            return
        }
//        showCircle()
//        toast(R.string.toast_services_start)
        V2RayServiceManager.startV2Ray(getApplication())
        _isRunning.value = true
//        hideCircle()
    }

    override fun onCleared() {
        getApplication<AngApplication>().unregisterReceiver(mMsgReceiver)
//        tcpingTestScope.coroutineContext[Job]?.cancelChildren()
        SpeedtestUtil.closeAllTcpSockets()
        Log.i(AppConfig.ANG_PACKAGE, "Main ViewModel is cleared")
        super.onCleared()
    }

    private val mMsgReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.getIntExtra("key", 0)) {
                AppConfig.MSG_STATE_RUNNING -> {
                    _isRunning.value = true
                }
                AppConfig.MSG_STATE_NOT_RUNNING -> {
                    _isRunning.value = false
                }
                AppConfig.MSG_STATE_START_SUCCESS -> {
                    getApplication<AngApplication>().toast(R.string.toast_services_success)
                    _isRunning.value = true
                }
                AppConfig.MSG_STATE_START_FAILURE -> {
                    getApplication<AngApplication>().toast(R.string.toast_services_failure)
                    _isRunning.value = false
                }
                AppConfig.MSG_STATE_STOP_SUCCESS -> {
                    _isRunning.value = false
                }
                AppConfig.MSG_MEASURE_DELAY_SUCCESS -> {
//                    updateTestResultAction.value = intent.getStringExtra("content")
                }
                AppConfig.MSG_MEASURE_CONFIG_SUCCESS -> {
                    val resultPair = intent.getSerializableExtra("content") as Pair<String, Long>
                    MmkvManager.encodeServerTestDelayMillis(resultPair.first, resultPair.second)
//                    updateListAction.value = getPosition(resultPair.first)
                }
            }
        }
    }

}