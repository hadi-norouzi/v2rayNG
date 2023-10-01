package com.v2ray.ang.ui.subscription.edit

import android.text.TextUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.domain.SubscriptionRepository
import com.v2ray.ang.dto.SubscriptionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class EditSubViewModel @Inject constructor(
    state: SavedStateHandle,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val subId: String? = state["subId"]


    private val _subItem: MutableStateFlow<SubscriptionItem?> = MutableStateFlow(value = null)
    val subItem = _subItem.asStateFlow()


    private val _state: MutableStateFlow<SubscriptionState> = MutableStateFlow(value = SubscriptionState.Initial)
    val state = _state.asStateFlow()


    init {
        getSubscriptionItem()
    }


    private fun getSubscriptionItem() {
        if (subId == null) return
        viewModelScope.launch {
            try {
                val sub = subscriptionRepository.getSubscriptionById(subId)
                _subItem.value = sub
            } catch (e: Exception) {
                _state.value = SubscriptionState.Failed
            }
        }
    }

    fun delete(item: SubscriptionItem) = viewModelScope.launch {
        subscriptionRepository.removeSubscription(item)
        _state.value = SubscriptionState.Deleted
    }

    fun submit(item: SubscriptionItem) = viewModelScope.launch {
//        if (subId == null) {
//            addServer(item.remarks, item.url)
//        } else {
//            updateServer(_subItem.value!!)
//        }

        subscriptionRepository.upsertSubscription(item)
        if (item.id == "") {
            _state.value = SubscriptionState.Added(item)
        } else {
            _state.value = SubscriptionState.Updated(item)
        }

    }

    private fun updateServer(item: SubscriptionItem) =
        viewModelScope.launch {
            try {
                subscriptionRepository.upsertSubscription(item)
                _state.value = SubscriptionState.Updated(item)
            } catch (e: Exception) {
                _state.value = SubscriptionState.Failed
            }
        }


    private fun addServer(name: String, url: String) {
        val subItem = SubscriptionItem().apply {
            remarks = name
            this.url = url
            enabled = true
            autoUpdate = true

        }


        if (TextUtils.isEmpty(subItem.remarks)) {
            return
        }
        viewModelScope.launch {
            try {

                subscriptionRepository.upsertSubscription(subItem)
                _state.value = SubscriptionState.Added(subItem)

            } catch (e: Exception) {
                _state.value = SubscriptionState.Failed
            }
        }
    }

}