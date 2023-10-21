package com.v2ray.ang.ui.configs

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tencent.mmkv.MMKV
import com.v2ray.ang.AngApplication
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.domain.ConfigRepository
import com.v2ray.ang.dto.ServerConfig
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.extension.toast
import com.v2ray.ang.service.V2RayServiceManager
import com.v2ray.ang.util.AngConfigManager
import com.v2ray.ang.util.MessageUtil
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.SpeedtestUtil
import com.v2ray.ang.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ConfigsViewModel @Inject constructor(
    application: Application,
    private val configRepository: ConfigRepository,
) :
    AndroidViewModel(application) {

    private val mainStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_MAIN, MMKV.MULTI_PROCESS_MODE) }
    private val settingsStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_SETTING, MMKV.MULTI_PROCESS_MODE) }


    private val _currentPing: MutableStateFlow<Int> = MutableStateFlow(value = -1)

    val currentPing = _currentPing.asStateFlow()

    private val _subscriptions: MutableStateFlow<List<Pair<String, SubscriptionItem>>> =
        MutableStateFlow(value = listOf())

    val subscriptions = _subscriptions.asStateFlow()

    val configs = configRepository.configs


    private val _isRunning: MutableStateFlow<ConnectionState> = MutableStateFlow(value = ConnectionState.Disconnected)
    val running = _isRunning.asStateFlow()


    val selectedConfig = configRepository.selectedConfig


    fun onSelect(config: ServerConfig) = viewModelScope.launch {
        configRepository.selectConfig(config)
    }

    init {

//        getSubs()
//        getConfigs()

        startListenBroadcast()
    }


    fun testAllConfigs() {
        viewModelScope.launch(Dispatchers.IO) {
            configRepository.testPingAll()
        }
    }

    private fun getSubs() {
        val subs = MmkvManager.decodeSubscriptions()
        _subscriptions.update { subs }
    }

    fun addConfig(text: String) = viewModelScope.launch {
        configRepository.addConfig(text)
    }

    fun deleteConfig(config: ServerConfig) = viewModelScope.launch {
        configRepository.deleteConfig(config)
    }

    private fun startListenBroadcast() {

        getApplication<AngApplication>().registerReceiver(
            mMsgReceiver,
            IntentFilter(AppConfig.BROADCAST_ACTION_ACTIVITY)
        )
        println("broadcast registered ${mMsgReceiver.isOrderedBroadcast}")
        MessageUtil.sendMsg2Service(getApplication(), AppConfig.MSG_REGISTER_CLIENT, "")
    }

    fun testCurrentServerRealPing() {
        MessageUtil.sendMsg2Service(getApplication(), AppConfig.MSG_MEASURE_DELAY, "")
    }

    fun restartV2Ray() {
        if (_isRunning.value == ConnectionState.Connected) {
            Utils.stopVService(getApplication())
        }
        Observable.timer(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                startV2Ray()
            }
    }

    fun startVpn() {
        if (_isRunning.value == ConnectionState.Connected) {
            Utils.stopVService(getApplication())
            _isRunning.value = ConnectionState.Disconnected
        } else if ((settingsStorage?.decodeString(AppConfig.PREF_MODE) ?: "VPN") == "VPN") {
            val intent = VpnService.prepare(getApplication())
            if (intent == null) {
                startV2Ray()
            }
        } else {
            startV2Ray()
        }
    }

    private fun startV2Ray() {
        if (mainStorage?.decodeString(MmkvManager.KEY_SELECTED_SERVER).isNullOrEmpty()) {
            return
        }
//        showCircle()
//        toast(R.string.toast_services_start)
        V2RayServiceManager.startV2Ray(getApplication() as AngApplication)
        _isRunning.value = ConnectionState.Connecting
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
            println("receiver onReceive called")
            when (intent?.getIntExtra("key", 0)) {
                AppConfig.MSG_STATE_RUNNING -> {
                    _isRunning.value = ConnectionState.Connected
                }

                AppConfig.MSG_STATE_NOT_RUNNING -> {
                    _isRunning.value = ConnectionState.Disconnected
                }

                AppConfig.MSG_STATE_START_SUCCESS -> {
                    getApplication<AngApplication>().toast(R.string.toast_services_success)
                    _isRunning.value = ConnectionState.Connected
                }

                AppConfig.MSG_STATE_START_FAILURE -> {
                    getApplication<AngApplication>().toast(R.string.toast_services_failure)
                    _isRunning.value = ConnectionState.Disconnected
                }

                AppConfig.MSG_STATE_STOP_SUCCESS -> {
                    _isRunning.value = ConnectionState.Disconnected
                }

                AppConfig.MSG_MEASURE_DELAY_SUCCESS -> {
                    _currentPing.value = intent.getStringExtra("content")?.toInt() ?: -1
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

sealed class ConnectionState {
    object Disconnected : ConnectionState()

    object Connected : ConnectionState()

    object Connecting : ConnectionState()
}