package com.v2ray.ang.ui.configs.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class EditConfigViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val configId = savedStateHandle
}