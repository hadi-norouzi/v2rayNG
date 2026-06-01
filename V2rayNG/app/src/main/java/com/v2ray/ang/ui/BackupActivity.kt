package com.v2ray.ang.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.tencent.mmkv.MMKV
import com.v2ray.ang.AppConfig
import com.v2ray.ang.AppConfig.WEBDAV_BACKUP_FILE_NAME
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityBackupBinding
import com.v2ray.ang.databinding.DialogWebdavBinding
import com.v2ray.ang.dto.entities.WebDavConfig
import com.v2ray.ang.extension.toastError
import com.v2ray.ang.extension.toastSuccess
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.handler.SettingsChangeManager
import com.v2ray.ang.handler.SettingsManager
import com.v2ray.ang.handler.WebDavManager
import com.v2ray.ang.util.LogUtil
import com.v2ray.ang.util.ZipUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class BackupActivity : HelperBaseActivity() {
    private val binding by lazy { ActivityBackupBinding.inflate(layoutInflater) }

    private val config_backup_options: Array<out String> by lazy {
        resources.getStringArray(R.array.config_backup_options)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(binding.root)

        //setContentViewWithToolbar(binding.root, showHomeAsUp = true, title = getString(R.string.title_configuration_backup_restore))

        /*
        binding.layoutBackup.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.title_configuration_backup)
                .setItems(config_backup_options) { dialog, which ->
                    when (which) {
                        0 -> backupViaLocal()
                        1 -> backupViaWebDav()
                    }
                }
                .show()
        }

        binding.layoutShare.setOnClickListener {
            val ret = backupConfigurationToCache()
            if (ret.first) {
                startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).setType("application/zip")
                            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .putExtra(
                                Intent.EXTRA_STREAM,
                                FileProvider.getUriForFile(
                                    this, BuildConfig.APPLICATION_ID + ".cache", File(ret.second)
                                )
                            ), getString(R.string.title_configuration_share)
                    )
                )
            } else {
                toastError(R.string.toast_failure)
            }
        }

        binding.layoutRestore.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.title_configuration_restore)
                .setItems(config_backup_options) { dialog, which ->
                    when (which) {
                        0 -> restoreViaLocal()
                        1 -> restoreViaWebDav()
                    }
                }
                .show()
        }

        binding.layoutWebdavConfigSetting.setOnClickListener {
            showWebDavSettingsDialog()
        }
        */

        setContent {
            V2RayNGTheme {
                BackupPage(
                    onBack = { finish() },
                    onBackupLocal = { backupViaLocal() },
                    onBackupWebDav = { backupViaWebDav() },
                    onShare = { shareConfiguration() },
                    onRestoreLocal = { restoreViaLocal() },
                    onRestoreWebDav = { restoreViaWebDav() },
                    onWebDavSettings = { showWebDavSettingsDialog() }
                )
            }
        }
    }

    private fun shareConfiguration() {
        val ret = backupConfigurationToCache()
        if (ret.first) {
            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).setType("application/zip")
                        .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        .putExtra(
                            Intent.EXTRA_STREAM,
                            FileProvider.getUriForFile(
                                this, BuildConfig.APPLICATION_ID + ".cache", File(ret.second)
                            )
                        ), getString(R.string.title_configuration_share)
                )
            )
        } else {
            toastError(R.string.toast_failure)
        }
    }

    /**
     * Backup configuration to cache directory
     * Returns Pair<success, zipFilePath>
     */
    private fun backupConfigurationToCache(): Pair<Boolean, String> {
        val dateFormatted = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val folderName = "${getString(R.string.app_name)}_${dateFormatted}"
        val backupDir = this.cacheDir.absolutePath + "/$folderName"
        val outputZipFilePath = "${this.cacheDir.absolutePath}/$folderName.zip"

        val count = MMKV.backupAllToDirectory(backupDir)
        if (count <= 0) {
            return Pair(false, "")
        }

        if (ZipUtil.zipFromFolder(backupDir, outputZipFilePath)) {
            return Pair(true, outputZipFilePath)
        } else {
            return Pair(false, "")
        }
    }

    private fun restoreConfiguration(zipFile: File): Boolean {
        val backupDir = this.cacheDir.absolutePath + "/${System.currentTimeMillis()}"

        if (!ZipUtil.unzipToFolder(zipFile, backupDir)) {
            return false
        }

        val count = MMKV.restoreAllFromDirectory(backupDir)
        SettingsChangeManager.makeSetupGroupTab()
        SettingsChangeManager.makeRestartService()

        SettingsManager.initApp(this)
        return count > 0
    }

    private fun showFileChooser() {
        launchFileChooser { uri ->
            if (uri == null) {
                return@launchFileChooser
            }
            try {
                val targetFile =
                    File(this.cacheDir.absolutePath, "${System.currentTimeMillis()}.zip")
                contentResolver.openInputStream(uri).use { input ->
                    targetFile.outputStream().use { fileOut ->
                        input?.copyTo(fileOut)
                    }
                }
                if (restoreConfiguration(targetFile)) {
                    toastSuccess(R.string.toast_success)
                } else {
                    toastError(R.string.toast_failure)
                }
            } catch (e: Exception) {
                LogUtil.e(AppConfig.TAG, "Error during file restore", e)
                toastError(R.string.toast_failure)
            }
        }
    }

    private fun backupViaLocal() {
        val dateFormatted = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val defaultFileName = "${getString(R.string.app_name)}_${dateFormatted}.zip"

        launchCreateDocument(defaultFileName) { uri ->
            if (uri != null) {
                try {
                    val ret = backupConfigurationToCache()
                    if (ret.first) {
                        // Copy the cached zip file to user-selected location
                        contentResolver.openOutputStream(uri)?.use { output ->
                            File(ret.second).inputStream().use { input ->
                                input.copyTo(output)
                            }
                        }
                        // Clean up cache file
                        File(ret.second).delete()
                        toastSuccess(R.string.toast_success)
                    } else {
                        toastError(R.string.toast_failure)
                    }
                } catch (e: Exception) {
                    LogUtil.e(AppConfig.TAG, "Failed to backup configuration", e)
                    toastError(R.string.toast_failure)
                }
            }
        }
    }

    private fun restoreViaLocal() {
        showFileChooser()
    }

    private fun backupViaWebDav() {
        val saved = MmkvManager.decodeWebDavConfig()
        if (saved == null || saved.baseUrl.isEmpty()) {
            toastError(R.string.title_webdav_config_setting_unknown)
            return
        }

        showLoading()

        lifecycleScope.launch(Dispatchers.IO) {
            var tempFile: File? = null
            try {
                val ret = backupConfigurationToCache()
                if (!ret.first) {
                    withContext(Dispatchers.Main) {
                        toastError(R.string.toast_failure)
                    }
                    return@launch
                }

                tempFile = File(ret.second)
                WebDavManager.init(saved)

                val ok = try {
                    WebDavManager.uploadFile(tempFile, WEBDAV_BACKUP_FILE_NAME)
                } catch (e: Exception) {
                    LogUtil.e(AppConfig.TAG, "WebDAV upload error", e)
                    false
                }

                withContext(Dispatchers.Main) {
                    if (ok) toastSuccess(R.string.toast_success) else toastError(R.string.toast_failure)
                }
            } catch (e: Exception) {
                LogUtil.e(AppConfig.TAG, "WebDAV backup error", e)
                withContext(Dispatchers.Main) {
                    toastError(R.string.toast_failure)
                }
            } finally {
                try {
                    tempFile?.delete()
                } catch (_: Exception) {
                }
                withContext(Dispatchers.Main) {
                    hideLoading()
                }
            }
        }
    }

    private fun restoreViaWebDav() {
        val saved = MmkvManager.decodeWebDavConfig()
        if (saved == null || saved.baseUrl.isEmpty()) {
            toastError(R.string.title_webdav_config_setting_unknown)
            return
        }

        showLoading()

        lifecycleScope.launch(Dispatchers.IO) {
            var target: File? = null
            try {
                target = File(cacheDir, "download_${System.currentTimeMillis()}.zip")
                WebDavManager.init(saved)
                val ok = WebDavManager.downloadFile(WEBDAV_BACKUP_FILE_NAME, target)
                if (!ok) {
                    withContext(Dispatchers.Main) {
                        toastError(R.string.toast_failure)
                    }
                    return@launch
                }

                val restored = restoreConfiguration(target)
                withContext(Dispatchers.Main) {
                    if (restored) {
                        toastSuccess(R.string.toast_success)
                    } else {
                        toastError(R.string.toast_failure)
                    }
                }
            } catch (e: Exception) {
                LogUtil.e(AppConfig.TAG, "WebDAV download error", e)
                withContext(Dispatchers.Main) { toastError(R.string.toast_failure) }
            } finally {
                try {
                    target?.delete()
                } catch (_: Exception) {
                }
                withContext(Dispatchers.Main) {
                    hideLoading()
                }
            }
        }
    }

    private fun showWebDavSettingsDialog() {
        val dialogBinding = DialogWebdavBinding.inflate(layoutInflater)

        MmkvManager.decodeWebDavConfig()?.let { cfg ->
            dialogBinding.etWebdavUrl.setText(cfg.baseUrl)
            dialogBinding.etWebdavUser.setText(cfg.username ?: "")
            dialogBinding.etWebdavPass.setText(cfg.password ?: "")
            dialogBinding.etWebdavRemotePath.setText(cfg.remoteBasePath ?: "/")
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.title_webdav_config_setting)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.menu_item_save_config) { _, _ ->
                val url = dialogBinding.etWebdavUrl.text.toString().trim()
                val user = dialogBinding.etWebdavUser.text.toString().trim().ifEmpty { null }
                val pass = dialogBinding.etWebdavPass.text.toString()
                val remotePath = dialogBinding.etWebdavRemotePath.text.toString().trim().ifEmpty { AppConfig.WEBDAV_BACKUP_DIR }
                val cfg = WebDavConfig(baseUrl = url, username = user, password = pass, remoteBasePath = remotePath)
                MmkvManager.encodeWebDavConfig(cfg)
                toastSuccess(R.string.toast_success)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupPage(
    onBack: () -> Unit,
    onBackupLocal: () -> Unit,
    onBackupWebDav: () -> Unit,
    onShare: () -> Unit,
    onRestoreLocal: () -> Unit,
    onRestoreWebDav: () -> Unit,
    onWebDavSettings: () -> Unit
) {
    var showBackupOptions by remember { mutableStateOf(false) }
    var showRestoreOptions by remember { mutableStateOf(false) }
    val backupOptions = stringArrayResource(R.array.config_backup_options)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_configuration_backup_restore)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            BackupItem(
                title = stringResource(R.string.title_configuration_backup),
                onClick = { showBackupOptions = true }
            )
            BackupItem(
                title = stringResource(R.string.title_configuration_share),
                onClick = onShare
            )
            BackupItem(
                title = stringResource(R.string.title_configuration_restore),
                onClick = { showRestoreOptions = true }
            )
            BackupItem(
                title = stringResource(R.string.title_webdav_config_setting),
                onClick = onWebDavSettings
            )
        }
    }

    if (showBackupOptions) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showBackupOptions = false },
            title = { Text(stringResource(R.string.title_configuration_backup)) },
            text = {
                Column {
                    backupOptions.forEachIndexed { index, option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showBackupOptions = false
                                    if (index == 0) onBackupLocal() else onBackupWebDav()
                                }
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showRestoreOptions) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showRestoreOptions = false },
            title = { Text(stringResource(R.string.title_configuration_restore)) },
            text = {
                Column {
                    backupOptions.forEachIndexed { index, option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showRestoreOptions = false
                                    if (index == 0) onRestoreLocal() else onRestoreWebDav()
                                }
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun BackupItem(title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
    HorizontalDivider()
}
