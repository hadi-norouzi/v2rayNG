package com.v2ray.ang.ui.settings.routing

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

sealed class RoutingTab(val title: String, val screen: @Composable () -> Unit) {
    object Proxy : RoutingTab("Proxy", { Addresses() })
    object Direct : RoutingTab("Direct", { Addresses() })

    object Block : RoutingTab("Blocked", { Addresses() })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RoutingRulesPage() {

    val tabs = listOf(
        RoutingTab.Proxy,
        RoutingTab.Direct,
        RoutingTab.Block
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Custom rules") })
        }
    ) {

        Column(modifier = Modifier.padding(it)) {
            RoutingTabs(
                tabs = tabs, pagerState = pagerState,
            )
            HorizontalPager(state = pagerState) {index ->

                tabs[index].screen()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoutingTabs(tabs: List<RoutingTab>, pagerState: PagerState) {

    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
    ) {

        tabs.forEachIndexed { index, item ->
            LeadingIconTab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = { Text(item.title) },
                icon = { /*TODO*/ },
            )
        }
    }
}