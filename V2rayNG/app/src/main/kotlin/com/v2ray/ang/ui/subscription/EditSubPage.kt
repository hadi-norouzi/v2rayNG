package com.v2ray.ang.ui.subscription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.v2ray.ang.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubPage() {

    var remark by remember { mutableStateOf("") }

    var url by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("") })
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