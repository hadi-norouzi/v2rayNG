package com.v2ray.ang.ui.settings.perappproxy

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.hilt.navigation.compose.hiltViewModel
import com.v2ray.ang.R
import com.v2ray.ang.dto.AppInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerAppProxyPage() {


    val viewModel: PerAppProxyViewModel = hiltViewModel()

    val apps = viewModel.allApps.collectAsState()


    Scaffold(topBar = {
        TopAppBar(
            title = { Text("PerAppProxy") },
            actions = {

            },
        )
    }) {

        AppsList(
            modifier = Modifier.padding(it),
            apps = apps.value,
            onSelect = viewModel::updateAppSelection
        )
    }
}

@Composable
fun AppsList(
    modifier: Modifier = Modifier,
    apps: List<AppInfo>,
    onSelect: (AppInfo) -> Unit,
) {
    Box(modifier = modifier) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.title_pref_per_app_proxy))
                Spacer(modifier = Modifier.width(4.dp))
                Switch(checked = false, onCheckedChange = {})
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.switch_bypass_apps_mode))
                Spacer(modifier = Modifier.width(4.dp))
                Switch(checked = false, onCheckedChange = {})
            }
            LazyColumn(
                modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(apps) {
                    AppInfoRow(app = it, onClick = { onSelect(it) })
                }
            }
        }
    }
}

@Preview
@Composable
fun AppListPreview() {
    AppsList(apps = listOf(), onSelect = {})
}

@Composable
fun AppInfoRow(app: AppInfo, onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Image(
            bitmap = app.appIcon.toBitmap().asImageBitmap(),
            contentDescription = "",
            modifier = Modifier.size(40.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = app.appName)
            Text(text = app.packageName)
        }

        Checkbox(checked = app.isSelected == 1, onCheckedChange = {})
    }
}

@Preview
@Composable
fun AppInfoRowPreview() {
    AppInfoRow(
        app = AppInfo("name", "com.", R.drawable.ic_info_24dp.toDrawable(), false, 1),
        onClick = {}
    )
}