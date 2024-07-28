package com.v2ray.ang.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.v2ray.ang.dto.SubscriptionItem

class ConfigsViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val activity: MainActivity,
    private val subs:  List<Pair<String, SubscriptionItem>>,
) : FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int = subs.size + 1

    override fun createFragment(position: Int): Fragment {

        return ConfigListFragment(
            adapter = if (position == 0)
                MainRecyclerAdapter(activity)
            else ConfigRecyclerAdapter(
                subs[position - 1].first,
            )
        )
    }
}
