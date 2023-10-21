package com.v2ray.ang.ui.configs

import android.content.Intent
import android.net.VpnService
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.v2ray.ang.R
import com.v2ray.ang.dto.EConfigType
import com.v2ray.ang.ui.ServerActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigsPage(navController: NavController, viewModel: ConfigsViewModel = hiltViewModel()) {

    val running = viewModel.running.collectAsState()


    val clipboardManager = LocalClipboardManager.current

    val context = LocalContext.current

    val launch = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) viewModel.startVpn()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_filter_config)) },
                actions = {
                    AddDropDown(
                        onImportFromClipboard = {
                            clipboardManager.getText()?.text?.let {
                                viewModel.addConfig(it)
                            }
                        },
                        onImportFromQr = {
                            navController.navigate("scan_qr")
                        },
                        onManuallyClicked = {
                            context.startActivity(
                                Intent()
                                    .putExtra("createConfigType", EConfigType.VLESS.value)
//                                        .putExtra("subscriptionId", mainViewModel.subscriptionId)
                                    .setClass(context, ServerActivity::class.java)
                            )

                        }
                    )
                    MoreDropDown(
                        onRestartService = viewModel::restartV2Ray,
                        onPingAll = viewModel::testAllConfigs
                    )
                },
            )
        },
        floatingActionButton = {
            Column {
                ExtendedFloatingActionButton(
                    onClick = {
                        val intent = VpnService.prepare(context)
                        if (intent == null) {
                            viewModel.startVpn()
                        } else {
                            launch.launch(intent)
                        }
                    },
                ) {
                    when (running.value) {
                        ConnectionState.Connected, ConnectionState.Disconnected -> {
                            Icon(
                                if (running.value == ConnectionState.Disconnected) Icons.Filled.PlayArrow else Icons.Filled.Clear,
                                contentDescription = "start",
                            )
                        }
                        ConnectionState.Connecting -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 1.dp
                            )
                        }
                    }


                }
            }
        }
    ) {

        var tabIndex by remember { mutableIntStateOf(0) }
        val tabs = viewModel.subscriptions.collectAsState()
        val configs = viewModel.configs.collectAsState(initial = listOf())
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
            val selected = viewModel.selectedConfig.collectAsState(initial = null)
            println(configs.value)
            if (configs.value.isNotEmpty())

                ConfigList(
                    configs = configs.value,
                    selectedConfig = selected.value,
                    onSelect = viewModel::onSelect,
                    onEditClicked = { config ->
                        navController.navigate("configs/edit/${config.id}")
                    },
                    onDeleteClicked = { config ->
                        viewModel.deleteConfig(config)
                    },
                )
        }

    }
}

@Composable
fun MoreDropDown(
    onRestartService: () -> Unit,
    onPingAll: () -> Unit,
) {
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
                onClick = onRestartService
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
                onClick = onPingAll,
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.title_real_ping_all_server)) },
                onClick = { Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show() }
            )
        }
    }
}


@Composable
fun AddDropDown(
    onImportFromClipboard: () -> Unit,
    onImportFromQr: () -> Unit,
    onManuallyClicked: () -> Unit,
) {
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
                onClick = onImportFromQr,
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_clipboard)) },
                onClick = onImportFromClipboard,
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.menu_item_import_config_manually_vmess)) },
                onClick = { onManuallyClicked() }
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