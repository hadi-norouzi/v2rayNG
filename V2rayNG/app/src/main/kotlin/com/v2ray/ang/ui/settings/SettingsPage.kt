package com.v2ray.ang.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.R
import com.v2ray.ang.util.SpeedtestUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings")
                },
            )
        }
    ) {

        Column(modifier = Modifier.padding(it)) {
            SettingItem(
                title = stringResource(id = R.string.title_user_asset_setting),
                onClick = {

                },
            )

            Divider()

            SettingItem(
                title = "Ui Settings",
                onClick = {
                    navController.navigate("settings/ui")
                }
            )
            SettingItem(
                title = "VPN Settings",
                onClick = {
                    navController.navigate("settings/vpn")
                }
            )
            SettingItem(
                title = "Routing Settings",
                onClick = {
                    navController.navigate("settings/routing")
                }
            )
            Divider()

            SettingItem(
                title = stringResource(id = R.string.title_pref_promotion),
                onClick = {

                },
            )
            SettingItem(
                title = stringResource(id = R.string.title_logcat),
                onClick = {
                    navController.navigate("logcat")
                },
            )
            SettingItem(
                title = stringResource(id = R.string.title_pref_feedback),
                onClick = {

                },
            )
            SettingItem(
                title = "About",
                onClick = {

                    navController.navigate("settings/about")
                },
            )
        }
    }
}


@Composable
fun SettingItem(
    @DrawableRes icon: Int? = null,
    title: String? = null,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
) {


    Row(
        modifier = Modifier
            .let {

                if (onClick != null)
                    return@let it.clickable(onClick = onClick)
                it
            }
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null)
            Row {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "icon",
                    modifier = Modifier.size(26.dp),
                )
                Box(modifier = Modifier.width(12.dp))
            }
        if (title != null)
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(title)
                if (subtitle != null)
                    Text(subtitle)
            }
        if (content != null) {
            content()
        }

    }

}


