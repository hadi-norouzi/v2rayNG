package com.v2ray.ang.dto

/***
@property updateInterval is In Minute
 */
data class SubscriptionItem(
    var remarks: String = "",
    var url: String = "",
    var enabled: Boolean = true,
    val addedTime: Long = System.currentTimeMillis(),
    val autoUpdate: Boolean = false,
    val updateInterval: Int? = null,
)
