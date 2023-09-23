package com.v2ray.ang.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.v2ray.ang.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpnSettings(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Vpn settings") })
        }
    ) {

        Column(
            modifier = Modifier.padding(it)
        ) {


            SettingItem(
                title = stringResource(id = R.string.title_pref_per_app_proxy),
                subtitle = stringResource(id = R.string.summary_pref_per_app_proxy),
                onClick = {
                    navController.navigate("settings/vpn/per_app_proxy")
                }
            ) {
                Switch(checked = false, onCheckedChange = {})
            }
            SettingItem(
                title = stringResource(id = R.string.title_pref_local_dns_enabled),
                subtitle = stringResource(id = R.string.summary_pref_local_dns_enabled),
            ) {
                Switch(checked = false, onCheckedChange = {})
            }
            SettingItem(
                title = stringResource(id = R.string.title_pref_fake_dns_enabled),
                subtitle = stringResource(id = R.string.summary_pref_fake_dns_enabled),
            ) {
                Switch(checked = false, onCheckedChange = {})
            }
            SettingItem(
                title = stringResource(id = R.string.title_pref_local_dns_port),
                subtitle = "5050",
            )
            SettingItem(
                title = stringResource(id = R.string.title_pref_vpn_dns),
                subtitle = "1.1.1.1",
            )
        }

    }
}