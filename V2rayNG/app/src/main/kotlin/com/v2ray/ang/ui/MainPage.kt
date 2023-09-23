package com.v2ray.ang.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.v2ray.ang.R
import com.v2ray.ang.ui.home.ConfigsPage
import com.v2ray.ang.ui.home.EditConfigPage
import com.v2ray.ang.ui.logcat.LogcatPage
import com.v2ray.ang.ui.settings.AboutPage
import com.v2ray.ang.ui.settings.AdvancedSettings
import com.v2ray.ang.ui.settings.routing.RoutingSettings
import com.v2ray.ang.ui.settings.SettingsPage
import com.v2ray.ang.ui.settings.UiSettings
import com.v2ray.ang.ui.settings.VpnSettings
import com.v2ray.ang.ui.settings.perappproxy.PerAppProxyPage
import com.v2ray.ang.ui.settings.routing.RoutingRulesPage
import com.v2ray.ang.ui.subscription.EditSubPage
import com.v2ray.ang.ui.subscription.SubscriptionPage

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
)

@Composable
fun MainPage() {

    val navHostController: NavHostController = rememberNavController()


    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination
    Scaffold(
        bottomBar = {
            BottomBar(navHostController, currentRoute)
        }
    ) {

        Box(modifier = Modifier.padding(it)) {
            NavHost(navController = navHostController, startDestination = "home") {

                composable("scan_qr") {
                    ScanQrPage()
                }

                navigation(route = "home", startDestination = "configs") {
                    composable("configs") {
                        ConfigsPage(navHostController)
                    }
                    composable("configs/edit") {
                        EditConfigPage()
                    }
                }

                navigation(route = "subscription_route", startDestination = "subscriptions") {
                    composable("subscriptions") {
                        SubscriptionPage(
                            navHostController
                        )
                    }
                    composable(
                        "subscription/edit/{subId}",
                        arguments = listOf(navArgument("subId") { type = NavType.StringType })
                    ) {
                        EditSubPage(navHostController)
                    }
                    composable("subscription/add") {
                        EditSubPage(navHostController)
                    }
                }

                navigation(startDestination = "settings", route = "setting") {
                    composable("settings") {
                        SettingsPage(
                            navHostController
                        )
                    }
                    composable("logcat") {
                        LogcatPage()
                    }
                    composable("settings/ui") {
                        UiSettings()
                    }
                    composable("settings/vpn") {
                        VpnSettings(
                            navHostController
                        )
                    }
                    composable("settings/vpn/per_app_proxy") {
                        PerAppProxyPage()
                    }
                    composable("settings/routing") {
                        RoutingSettings(
                            navHostController
                        )
                    }
                    composable("settings/routing/custom") {
                        RoutingRulesPage()
                    }
                    composable("settings/advanced") {
                        AdvancedSettings()
                    }
                    composable("settings/about") {
                        AboutPage()
                    }
                }
            }
        }
    }

}

@Composable
fun BottomBar(navController: NavHostController, navDestination: NavDestination?) {
    val icons = listOf(
        BottomNavigationItem(
            title = stringResource(id = R.string.home_item),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "home"
        ),
        BottomNavigationItem(
            title = stringResource(id = R.string.subscriptions_item),
            selectedIcon = Icons.Filled.AccountBox,
            unselectedIcon = Icons.Outlined.AccountBox,
            route = "subscriptions"
        ),
        BottomNavigationItem(
            title = stringResource(id = R.string.settings_item),
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = "settings"
        )
    )
    NavigationBar {
        icons.forEach { item ->
            NavigationBarItem(
                selected = navDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                label = {
                    Text(item.title)
                },
                icon = {
                    Icon(
                        imageVector = if (navDestination?.hierarchy?.any { it.route == item.route } == true) item.selectedIcon else item.unselectedIcon,
                        contentDescription = "",
                    )
                },
            )

        }
    }

}