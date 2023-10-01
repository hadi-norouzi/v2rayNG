package com.v2ray.ang.ui.configs.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.v2ray.ang.data.ConfigsDatasource
import com.v2ray.ang.dto.ServerConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditConfigViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val configsDatasource: ConfigsDatasource
): ViewModel() {

    private val configId: String? = savedStateHandle["configId"]

    private val _config: MutableStateFlow<ServerConfig?> = MutableStateFlow(value = null)

    val config = _config.asStateFlow()

    init {
        getConfig()
    }
    private fun getConfig() {
        val configs = configsDatasource.getAllConfigs()
        _config.value = configs.find { configId == it.id }
    }

}