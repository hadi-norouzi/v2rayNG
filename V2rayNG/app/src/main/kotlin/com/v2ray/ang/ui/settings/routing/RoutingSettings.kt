package com.v2ray.ang.ui.settings.routing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.v2ray.ang.R
import com.v2ray.ang.ui.settings.SettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutingSettings(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Vpn settings") })
        }
    ) {

        Column(
            modifier = Modifier.padding(it)
        ) {


            SettingItem(
                title = stringResource(id = R.string.title_pref_routing_domain_strategy),
                subtitle = "ip",
            )
            SettingItem(
                title = stringResource(id = R.string.routing_settings_default_rules),
                subtitle = "custom",
                onClick = {

                    navController.navigate("settings/routing/custom")
                }
            )
            SettingItem(
                title = stringResource(id = R.string.routing_settings_default_rules),
                subtitle = "global",
            )
        }

    }
}