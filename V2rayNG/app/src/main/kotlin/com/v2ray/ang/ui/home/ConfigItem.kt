package com.v2ray.ang.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2ray.ang.dto.ServerConfig

@Composable
fun ConfigItem(
    item: ServerConfig
) {
    Card(


        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(item.remarks)
                Text("${item.outboundBean?.getServerAddress()} : ${item.outboundBean?.getServerPort()}")
            }
            Row {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.Share, contentDescription = "")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.Edit, contentDescription = "")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.Delete, contentDescription = "")
                }
            }
        }
    }
}