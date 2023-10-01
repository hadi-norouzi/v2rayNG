package com.v2ray.ang.ui.subscription.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.v2ray.ang.R
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.util.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubPage(navController: NavController) {

    val viewModel: EditSubViewModel = hiltViewModel()

    val subItem = viewModel.subItem.collectAsState()

    val state = viewModel.state.collectAsState()

    var remark by remember { mutableStateOf(subItem.value?.remarks ?: "") }

    var url by remember { mutableStateOf(subItem.value?.url ?: "") }

    val context = LocalContext.current


    LaunchedEffect(key1 = state.value) {
        when (state.value) {
            is SubscriptionState.Added -> navController.popBackStack()
            SubscriptionState.Failed -> TODO()
            SubscriptionState.Initial -> Unit
            is SubscriptionState.Updated -> navController.popBackStack()
            SubscriptionState.Deleted -> {
                context.showToast("Subscription Deleted")
                navController.popBackStack()
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription") },
                actions = {
                    if (subItem.value != null)
                        IconButton(onClick = {
                            viewModel.delete(subItem.value!!)
                        }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "delete")
                        }
                    IconButton(onClick = {
//                        viewModel.submit(subItem.value?.copy())
                    }) {
                        Icon(Icons.Outlined.Done, contentDescription = "save")
                    }

                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                    }
                },
            )
        }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(1f),
                value = remark,
                onValueChange = { remark = it },
                label = {
                    Text(stringResource(id = R.string.sub_setting_remarks))

                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(1f),
                value = url,
                onValueChange = { url = it },
                label = {
                    Text(stringResource(id = R.string.sub_setting_url))
                }
            )
        }
    }
}