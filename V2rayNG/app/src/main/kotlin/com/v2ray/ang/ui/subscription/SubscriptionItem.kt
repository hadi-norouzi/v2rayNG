package com.v2ray.ang.ui.subscription

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.dto.SubState
import com.v2ray.ang.dto.SubscriptionItem
import java.text.SimpleDateFormat
import java.time.Instant

@Composable
fun SubscriptionItem(
    item: SubscriptionItem,
    onEditTap: (SubscriptionItem) -> Unit,
    onReloadTap: (SubscriptionItem) -> Unit,
    onShareClicked: (SubscriptionItem) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEditTap(item)
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(item.remarks)
                Spacer(modifier = Modifier.height(4.dp))
                if (item.updatedAt == -1L) {

                    Text(item.url, maxLines = 1)
                } else {
                    Text("Updated: ${SimpleDateFormat("dd/MM/yyyy").format(item.updatedAt)}")
                }
            }
            Row {
                IconButton(onClick = { onShareClicked(item) }) {
                    Icon(Icons.Filled.Share, contentDescription = "share")
                }
                IconButton(onClick = { onReloadTap(item) }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "refresh")
                }

            }
        }
        if (item.state == SubState.Loading)
            LinearProgressIndicator(
                progress = 1f,
                modifier = Modifier.fillMaxWidth()
            )
    }
}

@Preview
@Composable
fun SubscriptionItemPreview() {
    SubscriptionItem(
        item = SubscriptionItem(remarks = "Sub", state = SubState.Loading),
        onEditTap = {},
        onReloadTap = {},
        onShareClicked = {},
    )
}