package com.v2ray.ang.data

import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.v2ray.ang.dto.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.ArrayList
import javax.inject.Inject

class AppsDatasourceImpl @Inject constructor(val packageManager: PackageManager) : AppsDatasource {
    override fun supportedApps(): Flow<List<AppInfo>> = flow {

        val packages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        val apps = ArrayList<AppInfo>()

        for (pkg in packages) {
            if (!pkg.hasInternetPermission && pkg.packageName != "android") continue

            val applicationInfo = pkg.applicationInfo

            val appName = applicationInfo.loadLabel(packageManager).toString()
            val appIcon = applicationInfo.loadIcon(packageManager)
            val isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) > 0

            val appInfo = AppInfo(appName, pkg.packageName, appIcon, isSystemApp, 0)
            apps.add(appInfo)
        }

        emit(apps)
    }

    override suspend fun enabledApps() {
        TODO("Not yet implemented")
    }


    private val PackageInfo.hasInternetPermission: Boolean
        get() {
            val permissions = requestedPermissions
            return permissions?.any { it == Manifest.permission.INTERNET } ?: false
        }
}