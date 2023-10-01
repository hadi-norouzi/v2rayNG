package com.v2ray.ang.data

import android.text.TextUtils
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.R
import com.v2ray.ang.dto.EConfigType
import com.v2ray.ang.dto.ServerConfig
import com.v2ray.ang.dto.V2rayConfig
import com.v2ray.ang.dto.VmessQRCode
import com.v2ray.ang.extension.idnHost
import com.v2ray.ang.util.AngConfigManager
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.net.URI
import javax.inject.Inject

class ConfigsDatasourceImpl @Inject constructor() : ConfigsDatasource {


    private var storage: MMKV = MMKV.mmkvWithID(MmkvManager.ID_MAIN, MMKV.MULTI_PROCESS_MODE)
    private var serverStorage: MMKV = MMKV.mmkvWithID(MmkvManager.ID_SERVER_CONFIG, MMKV.MULTI_PROCESS_MODE)
    private val mainStorage: MMKV = MMKV.mmkvWithID(MmkvManager.ID_MAIN, MMKV.MULTI_PROCESS_MODE)


    private val _configs: MutableStateFlow<List<ServerConfig>> = MutableStateFlow(value = getAllConfigs())
    override val configs: Flow<List<ServerConfig>>
        get() = _configs

    private val _selectedConfig: MutableStateFlow<ServerConfig?> = MutableStateFlow(value = null)
    override val selectedConfig: Flow<ServerConfig?>
        get() = _selectedConfig

    init {
        getSelectedConfig()
    }

    override suspend fun removeConfig(config: ServerConfig) {
        val configs = getAllConfigs().toMutableList()
        configs.remove(config)
        _configs.update { configs }
    }

    private fun getSelectedConfig() {
        val configs = getAllConfigs()
        val selectedConfig = mainStorage.decodeString(MmkvManager.KEY_SELECTED_SERVER)
        val config = configs.find { it.id == selectedConfig }
        _selectedConfig.value = config
    }

    override suspend fun updateConfig(vararg configs: ServerConfig) {
        val allConfigs = getAllConfigs().toMutableList()

        configs.forEach { config ->
            val index = allConfigs.indexOfFirst { it == config }
            if (index != -1) {
                allConfigs[index] = config
            }
        }
        _configs.update { allConfigs }
        updateDatabase(configs.asList())
    }

    override suspend fun addConfig(vararg config: ServerConfig) {
        val configs = getAllConfigs().toMutableList()
        configs.addAll(config)
        _configs.update { configs }
    }

    override suspend fun addConfig(url: String, allowInsecure: Boolean): Int {
        var config: ServerConfig? = null

        if (url.startsWith(EConfigType.VMESS.protocolScheme)) {
            config = ServerConfig.create(EConfigType.VMESS)
            val streamSetting = config.outboundBean?.streamSettings ?: return -1


            if (!AngConfigManager.tryParseNewVmess(url, config, allowInsecure)) {
                if (url.indexOf("?") > 0) {
                    if (!AngConfigManager.tryResolveVmess4Kitsunebi(url, config)) {
                        return R.string.toast_incorrect_protocol
                    }
                } else {
                    var result = url.replace(EConfigType.VMESS.protocolScheme, "")
                    result = Utils.decode(result)
                    if (TextUtils.isEmpty(result)) {
                        return R.string.toast_decoding_failed
                    }
                    val vmessQRCode = Gson().fromJson(result, VmessQRCode::class.java)
                    // Although VmessQRCode fields are non null, looks like Gson may still create null fields
                    if (TextUtils.isEmpty(vmessQRCode.add)
                        || TextUtils.isEmpty(vmessQRCode.port)
                        || TextUtils.isEmpty(vmessQRCode.id)
                        || TextUtils.isEmpty(vmessQRCode.net)
                    ) {
                        return R.string.toast_incorrect_protocol
                    }

                    config.remarks = vmessQRCode.ps
                    config.outboundBean?.settings?.vnext?.get(0)?.let { vnext ->
                        vnext.address = vmessQRCode.add
                        vnext.port = Utils.parseInt(vmessQRCode.port)
                        vnext.users[0].id = vmessQRCode.id
                        vnext.users[0].security =
                            if (TextUtils.isEmpty(vmessQRCode.scy)) V2rayConfig.DEFAULT_SECURITY else vmessQRCode.scy
                        vnext.users[0].alterId = Utils.parseInt(vmessQRCode.aid)
                    }
                    val sni = streamSetting.populateTransportSettings(
                        vmessQRCode.net,
                        vmessQRCode.type,
                        vmessQRCode.host,
                        vmessQRCode.path,
                        vmessQRCode.path,
                        vmessQRCode.host,
                        vmessQRCode.path,
                        vmessQRCode.type,
                        vmessQRCode.path
                    )

                    val fingerprint = vmessQRCode.fp ?: streamSetting.tlsSettings?.fingerprint
                    streamSetting.populateTlsSettings(
                        vmessQRCode.tls, allowInsecure,
                        if (TextUtils.isEmpty(vmessQRCode.sni)) sni else vmessQRCode.sni,
                        fingerprint, vmessQRCode.alpn, null, null, null
                    )
                }
            }
        } else if (url.startsWith(EConfigType.SHADOWSOCKS.protocolScheme)) {
            config = ServerConfig.create(EConfigType.SHADOWSOCKS)
            if (!AngConfigManager.tryResolveResolveSip002(url, config)) {
                var result = url.replace(EConfigType.SHADOWSOCKS.protocolScheme, "")
                val indexSplit = result.indexOf("#")
                if (indexSplit > 0) {
                    try {
                        config.remarks =
                            Utils.urlDecode(result.substring(indexSplit + 1, result.length))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    result = result.substring(0, indexSplit)
                }

                //part decode
                val indexS = result.indexOf("@")
                result = if (indexS > 0) {
                    Utils.decode(result.substring(0, indexS)) + result.substring(
                        indexS,
                        result.length
                    )
                } else {
                    Utils.decode(result)
                }

                val legacyPattern = "^(.+?):(.*)@(.+?):(\\d+?)/?$".toRegex()
                val match = legacyPattern.matchEntire(result)
                    ?: return R.string.toast_incorrect_protocol

                config.outboundBean?.settings?.servers?.get(0)?.let { server ->
                    server.address = match.groupValues[3].removeSurrounding("[", "]")
                    server.port = match.groupValues[4].toInt()
                    server.password = match.groupValues[2]
                    server.method = match.groupValues[1].lowercase()
                }
            }
        } else if (url.startsWith(EConfigType.SOCKS.protocolScheme)) {
            var result = url.replace(EConfigType.SOCKS.protocolScheme, "")
            val indexSplit = result.indexOf("#")
            config = ServerConfig.create(EConfigType.SOCKS)
            if (indexSplit > 0) {
                try {
                    config.remarks =
                        Utils.urlDecode(result.substring(indexSplit + 1, result.length))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                result = result.substring(0, indexSplit)
            }

            //part decode
            val indexS = result.indexOf("@")
            if (indexS > 0) {
                result = Utils.decode(result.substring(0, indexS)) + result.substring(
                    indexS,
                    result.length
                )
            } else {
                result = Utils.decode(result)
            }

            val legacyPattern = "^(.*):(.*)@(.+?):(\\d+?)$".toRegex()
            val match =
                legacyPattern.matchEntire(result) ?: return R.string.toast_incorrect_protocol

            config.outboundBean?.settings?.servers?.get(0)?.let { server ->
                server.address = match.groupValues[3].removeSurrounding("[", "]")
                server.port = match.groupValues[4].toInt()
                val socksUsersBean =
                    V2rayConfig.OutboundBean.OutSettingsBean.ServersBean.SocksUsersBean()
                socksUsersBean.user = match.groupValues[1].lowercase()
                socksUsersBean.pass = match.groupValues[2]
                server.users = listOf(socksUsersBean)
            }
        } else if (url.startsWith(EConfigType.TROJAN.protocolScheme)) {
            val uri = URI(Utils.fixIllegalUrl(url))
            config = ServerConfig.create(EConfigType.TROJAN)
            config.remarks = Utils.urlDecode(uri.fragment ?: "")

            var flow = ""
            var fingerprint = config.outboundBean?.streamSettings?.tlsSettings?.fingerprint
            if (uri.rawQuery != null) {
                val queryParam = uri.rawQuery.split("&")
                    .associate { it.split("=").let { (k, v) -> k to Utils.urlDecode(v) } }

                val sni = config.outboundBean?.streamSettings?.populateTransportSettings(
                    queryParam["type"] ?: "tcp",
                    queryParam["headerType"],
                    queryParam["host"],
                    queryParam["path"],
                    queryParam["seed"],
                    queryParam["quicSecurity"],
                    queryParam["key"],
                    queryParam["mode"],
                    queryParam["serviceName"]
                )
                fingerprint = queryParam["fp"] ?: ""
                config.outboundBean?.streamSettings?.populateTlsSettings(
                    queryParam["security"] ?: V2rayConfig.TLS,
                    allowInsecure, queryParam["sni"] ?: sni!!, fingerprint, queryParam["alpn"],
                    null, null, null
                )
                flow = queryParam["flow"] ?: ""
            } else {
                config.outboundBean?.streamSettings?.populateTlsSettings(
                    V2rayConfig.TLS, allowInsecure, "",
                    fingerprint, null, null, null, null
                )
            }

            config.outboundBean?.settings?.servers?.get(0)?.let { server ->
                server.address = uri.idnHost
                server.port = uri.port
                server.password = uri.userInfo
                server.flow = flow
            }
        } else if (url.startsWith(EConfigType.VLESS.protocolScheme)) {
            val uri = URI(Utils.fixIllegalUrl(url))
            val queryParam = uri.rawQuery.split("&")
                .associate { it.split("=").let { (k, v) -> k to Utils.urlDecode(v) } }
            config = ServerConfig.create(EConfigType.VLESS)
            val streamSetting = config.outboundBean?.streamSettings ?: return -1
            var fingerprint = streamSetting.tlsSettings?.fingerprint

            config.remarks = Utils.urlDecode(uri.fragment ?: "")
            config.outboundBean?.settings?.vnext?.get(0)?.let { vnext ->
                vnext.address = uri.idnHost
                vnext.port = uri.port
                vnext.users[0].id = uri.userInfo
                vnext.users[0].encryption = queryParam["encryption"] ?: "none"
                vnext.users[0].flow = queryParam["flow"] ?: ""
            }

            val sni = streamSetting.populateTransportSettings(
                queryParam["type"] ?: "tcp",
                queryParam["headerType"],
                queryParam["host"],
                queryParam["path"],
                queryParam["seed"],
                queryParam["quicSecurity"],
                queryParam["key"],
                queryParam["mode"],
                queryParam["serviceName"]
            )
            fingerprint = queryParam["fp"] ?: ""
            val pbk = queryParam["pbk"] ?: ""
            val sid = queryParam["sid"] ?: ""
            val spx = Utils.urlDecode(queryParam["spx"] ?: "")
            streamSetting.populateTlsSettings(
                queryParam["security"] ?: "", allowInsecure,
                queryParam["sni"] ?: sni, fingerprint, queryParam["alpn"], pbk, sid, spx
            )
        }
        if (config == null) {
            return R.string.toast_incorrect_protocol
        }
//        config.subscriptionId = subid
//        val guid = MmkvManager.encodeServerConfig("", config)
//        if (
//            config.getProxyOutbound()
//                ?.getServerAddress() == removedSelectedServer.getProxyOutbound()
//                ?.getServerAddress() &&
//            config.getProxyOutbound()
//                ?.getServerPort() == removedSelectedServer.getProxyOutbound()?.getServerPort()
//        ) {
//            AngConfigManager.mainStorage?.encode(MmkvManager.KEY_SELECTED_SERVER, guid)
//        }
        return -1
    }

    override fun getAllConfigs(): List<ServerConfig> {
        val json = storage.decodeString(MmkvManager.KEY_ANG_CONFIGS)
        val list = if (json.isNullOrBlank()) {
            mutableListOf()
        } else {
            Gson().fromJson(json, Array<String>::class.java).toMutableList()
        }

        return list.mapNotNull { decodeServerConfig(it) }
    }

    override fun selectConfig(item: ServerConfig) {
        mainStorage.encode(MmkvManager.KEY_SELECTED_SERVER, item.id)
        val configs = getAllConfigs()
        val index = configs.indexOfFirst { it.id == item.id  }
        if (index == -1) return
        val selected = configs[index]
        _selectedConfig.value = selected
    }

    fun decodeServerConfig(guid: String): ServerConfig? {
        if (guid.isBlank()) {
            return null
        }
        val json = serverStorage.decodeString(guid)
        if (json.isNullOrBlank()) {
            return null
        }
        var item = Gson().fromJson(json, ServerConfig::class.java)
        item = item.copy(id = guid)
        return item
    }

    private fun updateDatabase(items: List<ServerConfig>) {
        for (config in items) {
            MmkvManager.encodeServerConfig(config.id!!, config)
        }
    }
}