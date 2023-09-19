package com.v2ray.ang.ui.subscription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.v2ray.ang.R
import com.v2ray.ang.dto.SubscriptionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubPage(navController: NavController) {

    val viewModel: EditSubViewModel = hiltViewModel()

    val subItem = viewModel.subItem.collectAsState()

    var remark by remember { mutableStateOf(subItem.value?.remarks ?: "") }

    var url by remember { mutableStateOf(subItem.value?.url ?: "") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    IconButton(onClick = {
                        val success = viewModel.saveServer(remark, url)
                        if (success) navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = "save")
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
            modifier = Modifier.padding(it)
        ) {

            Text(stringResource(id = R.string.sub_setting_remarks))
            TextField(value = remark, onValueChange = { remark = it })
            Text(stringResource(id = R.string.sub_setting_url))
            TextField(value = url, onValueChange = { url = it })
        }
    }
}