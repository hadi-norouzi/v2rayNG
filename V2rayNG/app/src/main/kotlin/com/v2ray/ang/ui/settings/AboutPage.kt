package com.v2ray.ang.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.util.SpeedtestUtil

@Composable
fun AboutPage() {
    Scaffold {
        Column(
            modifier = Modifier.padding(it)
        ) {

            Text("v${BuildConfig.VERSION_NAME} (${SpeedtestUtil.getLibVersion()})")
        }
    }
}