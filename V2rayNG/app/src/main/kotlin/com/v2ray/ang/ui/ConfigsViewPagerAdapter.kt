package com.v2ray.ang.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.v2ray.ang.util.MmkvManager

class ConfigsViewPagerAdapter(
    val fragmentManager: FragmentManager,
    val lifecycle: Lifecycle,
    val activity: MainActivity,
) : FragmentStateAdapter(fragmentManager, lifecycle) {


    private val subs by lazy { MmkvManager.decodeSubscriptions() }

    override fun getItemCount(): Int = subs.size

    override fun createFragment(position: Int): Fragment {

        return ConfigListFragment(
            adapter = if (position == 0) MainRecyclerAdapter(activity) else MainRecyclerAdapter(activity)
        )
    }
}
