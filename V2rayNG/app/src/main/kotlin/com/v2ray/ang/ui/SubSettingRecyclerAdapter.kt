package com.v2ray.ang.ui

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ItemRecyclerSubSettingBinding
import com.v2ray.ang.dto.SubscriptionItem

interface SubSettingActions {
    fun onShareClick(url: String)

    fun onEditClick(subId: String)

    fun onItemClick(subId: String, item: SubscriptionItem)
}

class SubSettingRecyclerAdapter(
    val activity: SubSettingActivity,
    private val actions: SubSettingActions?,
) :
    RecyclerView.Adapter<SubSettingRecyclerAdapter.SubscriptionViewHolder>() {

    private var mActivity: SubSettingActivity = activity



    override fun getItemCount() = mActivity.subscriptions.size

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val subId = mActivity.subscriptions[position].first
        val subItem = mActivity.subscriptions[position].second
        holder.binding.tvName.text = subItem.remarks
        holder.binding.tvUrl.text = subItem.url
        if (subItem.enabled) {
            holder.binding.chkEnable.setBackgroundResource(R.color.colorAccent)
        } else {
            holder.binding.chkEnable.setBackgroundResource(0)
        }
        holder.itemView.setBackgroundColor(Color.TRANSPARENT)

        holder.binding.apply {
            layoutEdit.setOnClickListener {
                actions?.onEditClick(subId)
            }
            infoContainer.setOnClickListener {
                actions?.onItemClick(subId, subItem)
                notifyItemChanged(position)
            }

            if (TextUtils.isEmpty(subItem.url)) {
                holder.binding.layoutShare.visibility = View.INVISIBLE
            } else {
                layoutShare.setOnClickListener {
                    actions?.onShareClick(subItem.url)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        return SubscriptionViewHolder(
            ItemRecyclerSubSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class SubscriptionViewHolder(val binding: ItemRecyclerSubSettingBinding) :
        RecyclerView.ViewHolder(binding.root)
}
