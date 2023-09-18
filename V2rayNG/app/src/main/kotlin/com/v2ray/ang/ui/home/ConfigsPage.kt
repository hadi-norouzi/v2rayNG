package com.v2ray.ang.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.v2ray.ang.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigsPage() {

    val viewModel: ConfigsViewModel = viewModel()

    val running = viewModel.running.collectAsState()

//    LaunchedEffect(key1 = Unit) {
//        viewModel.startListenBroadcast()
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_filter_config)) },
                actions = {

                    AddDropDown()
                    MoreDropDown()
                },
            )
        },
        floatingActionButton = {
//            FloatingActionButton(onClick = {
//                if (running.value) {
//
//                } else {
//                    viewModel.startVpn()
//                }
//            }) {
//                Icon(
//                    if (!running.value) Icons.Filled.PlayArrow else Icons.Filled.Clear,
//                    contentDescription = "start"
//                )
//            }
            Column {
                ExtendedFloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(
                        if (!running.value) Icons.Filled.PlayArrow else Icons.Filled.Clear,
                        contentDescription = "start"
                    )
                    Text("200 ping")
                }
                Text("200 ping")
            }
        }
    ) {

        var tabIndex by remember { mutableIntStateOf(0) }
        val tabs = viewModel.subscriptions.collectAsState()
        val configs = viewModel.configs.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
        ) {
//            if (tabs.value.size > 1)
//            ScrollableTabRow(selectedTabIndex = tabIndex, modifier = Modifier.fillMaxWidth()) {
//                Tab(
//                    selected = tabIndex == 0,
//                    onClick = { tabIndex = 0 },
//                    modifier = Modifier.height(54.dp)
//                ) {
//                    Text("Ungrouped")
//                }
//                tabs.value.forEachIndexed { index, item ->
//                    Tab(
//                        modifier = Modifier.height(54.dp),
//                        selected = tabIndex - 1 == index,
//                        onClick = { tabIndex = index + 1 },
//                    ) {
//                        Text(item.second.remarks)
//                    }
//                }
//            }
            if (configs.value.isNotEmpty())
                ConfigList(
                    configs = configs.value.first()
                )
        }

    }
}

@Composable
fun MoreDropDown() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.title_service_restart)) },
                onClick = { Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.title_del_all_config)) },
                onClick = { Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.title_del_duplicate_config)) },
                onClick = { Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.title_del_invalid_config)) },
                onClick = { Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.title_ping_all_server)) },
                onClick = { Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.title_real_ping_all_server)) },
                onClick = { Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show() }
            )
        }
    }
}


@Composable
fun AddDropDown() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_qrcode)) },
                onClick = { Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_clipboard)) },
                onClick = { Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_manually_vmess)) },
                onClick = { Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_manually_vless)) },
                onClick = { Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_manually_ss)) },
                onClick = { Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_manually_socks)) },
                onClick = { Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_manually_trojan)) },
                onClick = { Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_custom)) },
                onClick = { Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show() }
            )
        }
    }
}