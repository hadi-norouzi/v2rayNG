package com.v2ray.ang.ui.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.v2ray.ang.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPage(navController: NavController) {

    val viewModel: SubscriptionViewModel = viewModel()

    val items = viewModel.subscriptions.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_sub_setting)) },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("subscription/edit")
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "")
                    }
                },
            )
        },
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {

            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items.value) { sub ->
                    SubscriptionItem(item = sub.second)
                }
            }

        }

    }
}