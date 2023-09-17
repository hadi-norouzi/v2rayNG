package com.v2ray.ang.ui.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.v2ray.ang.dto.ServerConfig

@Composable
fun ConfigList(configs: List<ServerConfig>) {

    LazyColumn {

        items(configs) {
            Text(it.remarks)
        }
    }
}