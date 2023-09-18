package com.v2ray.ang.ui.logcat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.v2ray.ang.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogcatPage() {

    val viewModel: LogcatViewModel = viewModel()
    val logs = viewModel.logs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_logcat)) },
                actions = {

                    IconButton(onClick = { viewModel.copyToClipboard() }) {
                        Icon(Icons.Outlined.Share, contentDescription = "")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "")
                    }
                },
            )
        }
    ) {


        Box(
            modifier = Modifier.padding(it)
        ) {
            Text(logs.value, modifier = Modifier.verticalScroll(rememberScrollState()))
        }
    }
}