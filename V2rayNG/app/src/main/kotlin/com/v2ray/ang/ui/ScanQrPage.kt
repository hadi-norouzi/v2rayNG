package com.v2ray.ang.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ScanQrPage() {

    val scanQr = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) {

    }

    val context = LocalContext.current
    Button(onClick = {

        val intent = Intent(context, ScannerActivity::class.java)
        scanQr.launch(intent)
    }) {
        Text("Qr scan")
    }
}