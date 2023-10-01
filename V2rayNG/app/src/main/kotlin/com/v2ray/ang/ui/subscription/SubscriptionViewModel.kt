package com.v2ray.ang.ui.subscription

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.data.SubscriptionDatasource
import com.v2ray.ang.domain.SubscriptionRepository
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.extension.toast
import com.v2ray.ang.util.AngConfigManager
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
) :
    ViewModel() {


    val subscriptions = repository.subscriptions



    fun updateSubscription(subId: String) {
        viewModelScope.launch {
            repository.getSubscriptionData(subId)
        }
    }
}