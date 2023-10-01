package com.v2ray.ang.ui.configs.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.v2ray.ang.dto.EConfigType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditConfigPage(navController: NavController) {

    val viewModel: EditConfigViewModel = hiltViewModel()

    val config = viewModel.config.collectAsState()

    val name = remember {
        mutableStateOf(config.value?.remarks.orEmpty())
    }
    val address = remember {
        mutableStateOf(config.value?.getProxyOutbound()?.getServerAddress().orEmpty())
    }
    val port = remember {
        mutableStateOf(config.value?.getProxyOutbound()?.getServerPort() ?: 443)
    }
    val id = remember {
        mutableStateOf(config.value?.getProxyOutbound()?.getPassword().orEmpty())
    }
    val alterId = remember {
        mutableStateOf(config.value?.getProxyOutbound()?.settings?.vnext?.get(0)?.users?.get(0)?.alterId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Config") },
                actions = {

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Done, contentDescription = "")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "")
                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(24.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("name") })


            OutlinedTextField(
                value = address.value,
                onValueChange = { address.value = it },
                label = { Text("Address") },
            )
            OutlinedTextField(
                value = port.value.toString(), onValueChange = { port.value = it.toInt() },
                label = {
                    Text("Port")

                },
            )
            OutlinedTextField(
                value = alterId.value.toString(),
                onValueChange = { alterId.value = it.toInt() },
                label = {
                    Text("id")
                },
            )

            if (config.value?.configType == EConfigType.SOCKS) {

            }
        }
    }
}