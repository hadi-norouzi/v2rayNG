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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.v2ray.ang.R
import com.v2ray.ang.ui.home.ConfigsPage
import com.v2ray.ang.ui.settings.SettingsPage
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
    MaterialTheme {

        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Scaffold(
            bottomBar = {
                NavigationBar {
                    icons.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {

                                navHostController.navigate(item.route) {

                                    navHostController.graph.startDestinationRoute?.let { screen_route ->
                                        popUpTo(screen_route) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = {
                                Text(item.title)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = "",
                                )
                            },
                        )

                    }
                }
            }
        ) {

            Box(modifier = Modifier.padding(it)) {
                NavHost(navController = navHostController, startDestination = "home") {

                    composable("home") {
                        ConfigsPage()
                    }
                    composable("subscriptions") {
                        SubscriptionPage(
                            navHostController
                        )
                    }
                    composable("settings") {
                        SettingsPage()
                    }
                    composable("subscription_edit") {
                        EditSubPage()
                    }
                }
            }
        }
    }
}