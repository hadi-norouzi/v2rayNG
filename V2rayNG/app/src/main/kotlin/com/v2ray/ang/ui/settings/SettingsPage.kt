package com.v2ray.ang.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
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

//            SettingItem(
//                icon = R.drawable.ic_wallet,
//                onClick = {
//
//                    navController.navigate("/wallets")
//                },
//            ) {
//                Column {
//                    Text("Wallets")
//                    Text("Wallet 1", style = MaterialTheme.typography.bodySmall)
//                }
//            }
//            SettingItem(
//                icon = R.drawable.qr_code, text = "Scan QR code",
//                onClick = {
//
//                },
//            )
            SettingItem(
                text = stringResource(id = R.string.title_user_asset_setting),
                onClick = {

                },
            )
            Divider()
            SettingItem(
                text = stringResource(id = R.string.title_pref_promotion),
                onClick = {

                },
            )
            SettingItem(
                text = stringResource(id = R.string.title_logcat),
                onClick = {

                },
            )
            SettingItem(
                text = stringResource(id = R.string.title_pref_feedback),
                onClick = {

                },
            )
        }
    }
}


@Composable
fun SettingItem(
    @DrawableRes icon: Int? = null,
    text: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {


    Row(
        modifier = Modifier
            .let {

                if (onClick != null)
                    return@let it.clickable(onClick = onClick)
                it
            }
            .fillMaxWidth()
            .padding(12.dp)
            .height(40.dp),
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
        if (text != null) Text(text, modifier = Modifier.weight(1f))
        if (content != null) {
            content()
        }

    }

}
