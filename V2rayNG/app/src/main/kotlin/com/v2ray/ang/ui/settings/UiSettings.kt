package com.v2ray.ang.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.v2ray.ang.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiSettings() {

    val dynamicTheme = remember { mutableStateOf(false) }

    val showLanguageDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("UI Settings") })
        }
    ) {

        Column(
            modifier = Modifier.padding(it)
        ) {

            if (showLanguageDialog.value) {
                LanguageDialog(
                    onDismissRequest = {
                        showLanguageDialog.value = false
                    }
                )
            }

            SettingItem(
                title = "Dynamic Theme",
            ) {
                Switch(
                    checked = dynamicTheme.value, onCheckedChange = { dynamicTheme.value = it },
                )
            }

            SettingItem(
                title = stringResource(id = R.string.title_pref_start_scan_immediate),
                subtitle = stringResource(id = R.string.summary_pref_start_scan_immediate),
            ) {
                Switch(
                    checked = dynamicTheme.value, onCheckedChange = { dynamicTheme.value = it },
                )
            }
            SettingItem(
                title = stringResource(id = R.string.title_language),
                subtitle = "auto",
                onClick = {
                    showLanguageDialog.value = true
                }
            )
        }

    }

}

@Composable
fun LanguageDialog(
    onDismissRequest: () -> Unit,
) {
    val languages = listOf("auto", "English", "Persian")
    val selected = remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {

                languages.forEach {
                    Row {
                        RadioButton(selected = selected.value == it, onClick = { /*TODO*/ })
                        Text(
                            it,
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}