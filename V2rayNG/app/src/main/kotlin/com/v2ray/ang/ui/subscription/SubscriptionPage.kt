package com.v2ray.ang.ui.subscription

import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.v2ray.ang.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPage(navController: NavController) {

    val viewModel: SubscriptionViewModel = hiltViewModel()

    val items = viewModel.subscriptions.collectAsState()

    val update = viewModel.subscriptionUpdate.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(key1 = update.value) {

        Toast.makeText(context, update.value ?: "", Toast.LENGTH_SHORT).show()
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_sub_setting)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("subscription/add")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "")
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            val context = LocalContext.current
            if (items.value.isEmpty()) {

                Text(
                    "Empty subscriptions",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items.value) { sub ->
                        SubscriptionItem(
                            item = sub.second,
                            onEditTap = {
                                navController.navigate("subscription/edit/${sub.first}")
                            },
                            onReloadTap = {
                                viewModel.updateSubscription(sub.first)
                            },
                            onShareClicked = {
                                if (TextUtils.isEmpty(sub.second.url)) {
                                    // TODO: disable share button
                                }
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, sub.second.url)
                                    putExtra(Intent.EXTRA_SUBJECT, sub.second.remarks)
                                    type = "text/plain"
                                }

                                // TODO: show qrcode or copy to clipboard dialog
                                val shareIntent = Intent.createChooser(sendIntent, "Share With")
                                context.startActivity(shareIntent)
                            }
                        )
                    }
                }
            }

        }

    }
}