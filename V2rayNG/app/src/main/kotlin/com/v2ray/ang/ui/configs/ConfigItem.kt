package com.v2ray.ang.ui.configs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.dto.EConfigType
import com.v2ray.ang.dto.ServerConfig

@Composable
fun ConfigItem(
    item: ServerConfig,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) BorderStroke(width = 1.dp, color = Color.Red) else null
    ) {
        Row {
            Box(
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .width(8.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(item.remarks)
                    Text(item.configType.name)
                }
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Row {
                        IconButton(onClick = onShare) {
                            Icon(Icons.Outlined.Share, contentDescription = "")
                        }
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Outlined.Edit, contentDescription = "")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Outlined.Delete, contentDescription = "")
                        }
                    }
                    Text("100ms")
                }
            }
        }
    }
}

@Preview
@Composable
fun ConfigItemPreview() {
    ConfigItem(
        item = ServerConfig(configType = EConfigType.VLESS),
        isSelected = false,
        onSelect = { /*TODO*/ },
        onDelete = { /*TODO*/ },
        onEdit = { /*TODO*/ },
        onShare = {},
    )
}