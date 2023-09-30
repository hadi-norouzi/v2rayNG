package com.v2ray.ang.ui.configs.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditConfigPage() {

    val viewModel: EditConfigViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Config") })
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {

            Text("Name")
            TextField(value = "", onValueChange = {})

            Text("Address")
            TextField(value = "", onValueChange = {})

            Text("Port")
            TextField(value = "", onValueChange = {})


            Text("id")
            TextField(value = "", onValueChange = {})
        }
    }
}