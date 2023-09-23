package com.v2ray.ang.ui.home

import com.v2ray.ang.dto.ServerConfig

data class ConfigGroup(
    val guid: String?,
    val configs: List<ServerConfig>
)
