package com.v2ray.ang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.AppConfig
import com.v2ray.ang.AppConfig.ANG_PACKAGE
import com.v2ray.ang.util.LogUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class V2Log(
    val tag: String,
    val content: String?,
)

fun V2Log.concat(): String = "$tag : $content"


fun String.extractLog(): V2Log {
    val tag = substringBefore(": ")
    val content = substringAfter(": ")
    return V2Log(tag, content)
}

data class LogState(
    val isLoading: Boolean = false,
    val logs: List<V2Log> = emptyList(),
    val filterText: String = "",
    val isRefreshing: Boolean = false,
)

class LogcatViewModel : ViewModel() {

    private val _allLogs = MutableStateFlow<List<V2Log>>(emptyList())
    private val _filterText = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)

    val state: StateFlow<LogState> = combine(
        _allLogs,
        _filterText,
        _isLoading,
        _isRefreshing
    ) { logs, filter, loading, refreshing ->
        val filtered = if (filter.isBlank()) {
            logs
        } else {
            logs.filter {
                it.tag.contains(filter, ignoreCase = true) ||
                        (it.content?.contains(filter, ignoreCase = true) == true)
            }
        }
        LogState(
            isLoading = loading,
            logs = filtered,
            filterText = filter,
            isRefreshing = refreshing
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LogState()
    )

    init {
        loadLogcat()
    }

    fun loadLogcat() = viewModelScope.launch {
        _isLoading.value = true
        _isRefreshing.value = true
        try {
            val lst = LinkedHashSet<String>()
            lst.apply {
                add("logcat")
                add("-d")
                add("-v")
                add("time")
                add("-s")
                add("GoLog,${ANG_PACKAGE},AndroidRuntime,System.err")
            }

            val process = Runtime.getRuntime().exec(lst.toTypedArray())
            val allText = process.inputStream.bufferedReader().use { it.readLines() }.reversed()

            _allLogs.value = allText.map { t -> t.extractLog() }
        } catch (e: IOException) {
            LogUtil.e(AppConfig.TAG, "Failed to get logcat", e)
            _allLogs.value = emptyList()
        } finally {
            _isLoading.value = false
            _isRefreshing.value = false
        }
    }

    fun clearLogcat() = viewModelScope.launch {
        try {
            val lst = LinkedHashSet<String>()
            lst.add("logcat")
            lst.add("-c")
            val process = Runtime.getRuntime().exec(lst.toTypedArray())
            process.waitFor()

            _allLogs.value = emptyList()
        } catch (e: IOException) {
            LogUtil.e(AppConfig.TAG, "Failed to clear logcat", e)
        }
    }

    fun updateFilter(filter: String) {
        _filterText.value = filter
    }
}
