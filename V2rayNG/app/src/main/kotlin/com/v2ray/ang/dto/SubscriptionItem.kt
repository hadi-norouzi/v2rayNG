package com.v2ray.ang.dto

data class SubscriptionItem(
    val id: String = "",
    var remarks: String = "",
    var url: String = "",
    var enabled: Boolean = true,
    val addedTime: Long = System.currentTimeMillis(),
    var updatedAt: Long = -1,
    var autoUpdate: Boolean = false,
    val updateInterval: Int? = null,
    val state: SubState = SubState.Init,
)


enum class SubState {
    Init,
    Loading,
    Success,
    Failed,
}