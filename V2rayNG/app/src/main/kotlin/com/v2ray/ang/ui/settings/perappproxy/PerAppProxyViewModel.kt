package com.v2ray.ang.ui.settings.perappproxy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.data.AppsDatasource
import com.v2ray.ang.dto.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerAppProxyViewModel @Inject constructor(
    val datasource: AppsDatasource
): ViewModel() {


    private val apps: MutableStateFlow<List<AppInfo>> = MutableStateFlow(value = listOf())

    val allApps = apps.asStateFlow()

    init {
        viewModelScope.launch {
            getAllApps()
        }
    }

    suspend fun getAllApps () {
        datasource.supportedApps().collect {
            println("supported Apps ${it.size}")

            apps.value = it
        }
    }
}