package com.v2ray.ang.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.v2ray.ang.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigsPage() {

    val viewModel: ConfigsViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_filter_config)) },
                actions = {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_24dp),
                        contentDescription = stringResource(
                            id = R.string.menu_item_add_config
                        ),
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_outline_filter_alt_24),
                        contentDescription = stringResource(
                            id = R.string.menu_item_add_config
                        ),
                    )
                    Icon(
                        painter = painterResource(id = com.google.android.material.R.drawable.material_ic_menu_arrow_up_black_24dp),
                        contentDescription = stringResource(
                            id = R.string.menu_item_add_config
                        ),
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "start")
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
            TabRow(selectedTabIndex = tabIndex) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    modifier = Modifier.height(60.dp)
                ) {
                    Text("Ungrouped")
                }
                tabs.value.forEachIndexed { index, item ->
                    Tab(
                        modifier = Modifier.height(54.dp),
                        selected = tabIndex - 1  == index,
                        onClick = { tabIndex = index + 1 },
                    ) {
                        Text(item.second.remarks)
                    }
                }
            }
            ConfigList(
                configs = configs.value[tabIndex]
            )
        }

    }
}