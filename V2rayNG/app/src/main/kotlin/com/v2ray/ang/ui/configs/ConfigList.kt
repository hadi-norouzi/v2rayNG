package com.v2ray.ang.ui.configs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.v2ray.ang.dto.ServerConfig
import com.v2ray.ang.dto.shareableString
import com.v2ray.ang.util.AngConfigManager

@Composable
fun ConfigList(
    configs: List<ServerConfig>,
    selectedConfig: ServerConfig?,
    onSelect: (ServerConfig) -> Unit,
    onEditClicked: (ServerConfig) -> Unit,
    onDeleteClicked: (ServerConfig) -> Unit,
) {

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp),
        userScrollEnabled = true
    ) {

        items(configs) {
            ConfigItem(
                item = it,
                isSelected = it == selectedConfig,
                onSelect = { onSelect(it) },
                onDelete = { onDeleteClicked(it) },
                onEdit = { onEditClicked(it) },
                onShare = {
                    val config = it.shareableString()
                    clipboardManager.setText(AnnotatedString(config))
                },
            )
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }


    }
}

