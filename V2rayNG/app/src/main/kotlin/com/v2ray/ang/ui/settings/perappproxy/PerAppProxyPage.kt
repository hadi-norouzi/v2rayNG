package com.v2ray.ang.ui.settings.perappproxy

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.v2ray.ang.dto.AppInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerAppProxyPage() {


    val viewModel: PerAppProxyViewModel = hiltViewModel()

    val apps = viewModel.allApps.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PerAppProxy") })
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            LazyColumn {
                items(apps.value) {
                    AppInfoRow(app = it)
                }
            }
        }
    }
}

@Composable
fun AppInfoRow(app: AppInfo) {
    Row {
        Image(bitmap = app.appIcon.toBitmap().asImageBitmap(), contentDescription = "")

        Text(text = app.appName, modifier = Modifier.weight(1f))

        Checkbox(checked = app.isSelected == 1, onCheckedChange = {})
    }
}