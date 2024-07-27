package com.v2ray.ang.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.v2ray.ang.R
import com.v2ray.ang.databinding.FragmentConfigListBinding


class ConfigListFragment(
    val adapter: RecyclerView.Adapter<MainRecyclerAdapter.BaseViewHolder>
) : Fragment(R.layout.fragment_config_list) {

    private lateinit var binding: FragmentConfigListBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = FragmentConfigListBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = binding.root
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }
}