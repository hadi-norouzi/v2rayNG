package com.v2ray.ang.ui

import android.graphics.Color
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ItemQrcodeBinding
import com.v2ray.ang.databinding.ItemRecyclerFooterBinding
import com.v2ray.ang.databinding.ItemRecyclerMainBinding
import com.v2ray.ang.dto.EConfigType
import com.v2ray.ang.dto.ServerConfig
import com.v2ray.ang.dto.ServersCache
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.extension.toast
import com.v2ray.ang.helper.ItemTouchHelperAdapter
import com.v2ray.ang.helper.ItemTouchHelperViewHolder
import com.v2ray.ang.service.V2RayServiceManager
import com.v2ray.ang.util.AngConfigManager
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MainRecyclerAdapter(
    val activity: MainActivity,
    private val configs: MutableList<ServerConfig>,
    private val onItemEditClicked: (Int, ServerConfig) -> Unit,
    private val onItemDeleteClicked: (Int, ServerConfig) -> Unit,
    private val onShareClicked: (Int, ServerConfig) -> Unit,
    private val onItemTap: (Int) -> Unit,
) : RecyclerView.Adapter<MainRecyclerAdapter.BaseViewHolder>(), ItemTouchHelperAdapter {
    companion object {
        private const val VIEW_TYPE_ITEM = 1
        private const val VIEW_TYPE_FOOTER = 2
    }

        private var mActivity: MainActivity = activity
    private val mainStorage by lazy {
        MMKV.mmkvWithID(
            MmkvManager.ID_MAIN,
            MMKV.MULTI_PROCESS_MODE
        )
    }
    private val subStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_SUB, MMKV.MULTI_PROCESS_MODE) }
    private val settingsStorage by lazy {
        MMKV.mmkvWithID(
            MmkvManager.ID_SETTING,
            MMKV.MULTI_PROCESS_MODE
        )
    }

    var isRunning = false

    override fun getItemCount() = configs.size + 1

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is MainViewHolder) {
            val guid = mActivity.mainViewModel.serversCache[position].guid
            val config = configs[position]
//            //filter
//            if (mActivity.mainViewModel.subscriptionId.isNotEmpty()
//                && mActivity.mainViewModel.subscriptionId != config.subscriptionId
//            ) {
//                holder.itemMainBinding.cardView.visibility = View.GONE
//            } else {
//                holder.itemMainBinding.cardView.visibility = View.VISIBLE
//            }

            val outbound = config.getProxyOutbound()
            val aff = MmkvManager.decodeServerAffiliationInfo(guid)

            holder.itemMainBinding.tvName.text = config.remarks
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            holder.itemMainBinding.tvTestResult.text = aff?.getTestDelayString() ?: ""
            if ((aff?.testDelayMillis ?: 0L) < 0L) {
                holder.itemMainBinding.tvTestResult.setTextColor(
                    ContextCompat.getColor(
                        mActivity,
                        R.color.colorPingRed
                    )
                )
            } else {
                holder.itemMainBinding.tvTestResult.setTextColor(
                    ContextCompat.getColor(
                        mActivity,
                        R.color.colorPing
                    )
                )
            }
            if (guid == mainStorage?.decodeString(MmkvManager.KEY_SELECTED_SERVER)) {
                holder.itemMainBinding.layoutIndicator.setBackgroundResource(R.color.colorSelected)
            } else {
                holder.itemMainBinding.layoutIndicator.setBackgroundResource(R.color.colorUnselected)
            }
            holder.itemMainBinding.tvSubscription.text = ""
            val json = subStorage?.decodeString(config.subscriptionId)
            if (!json.isNullOrBlank()) {
                val sub = Gson().fromJson(json, SubscriptionItem::class.java)
                holder.itemMainBinding.tvSubscription.text = sub.remarks
            }

            when (config.configType) {
                EConfigType.CUSTOM -> {
                    holder.itemMainBinding.tvType.text =
                        mActivity.getString(R.string.server_customize_config)
                }

                EConfigType.VLESS -> {
                    holder.itemMainBinding.tvType.text = config.configType.name
                }

                else -> {
                    holder.itemMainBinding.tvType.text = config.configType.name.lowercase()
                }
            }
            holder.itemMainBinding.tvStatistics.text =
                "${outbound?.getServerAddress()} : ${outbound?.getServerPort()}"

            holder.itemMainBinding.layoutShare.setOnClickListener {
                onShareClicked(position, config)
            }

            holder.itemMainBinding.layoutEdit.setOnClickListener {
                onItemEditClicked(position, config)
            }
            holder.itemMainBinding.layoutRemove.setOnClickListener {
                onItemDeleteClicked(position, config)
            }

            holder.itemMainBinding.infoContainer.setOnClickListener {

                onItemTap(position)
            }
        }
        if (holder is FooterViewHolder) {
            holder.itemFooterBinding.layoutEdit.visibility = View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM ->
                MainViewHolder(
                    ItemRecyclerMainBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            else ->
                FooterViewHolder(
                    ItemRecyclerFooterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mActivity.mainViewModel.serversCache.size) {
            VIEW_TYPE_FOOTER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }

    class MainViewHolder(val itemMainBinding: ItemRecyclerMainBinding) :
        BaseViewHolder(itemMainBinding.root), ItemTouchHelperViewHolder

    class FooterViewHolder(val itemFooterBinding: ItemRecyclerFooterBinding) :
        BaseViewHolder(itemFooterBinding.root)

    override fun onItemDismiss(position: Int) {
        val guid = mActivity.mainViewModel.serversCache.getOrNull(position)?.guid ?: return
        if (guid != mainStorage?.decodeString(MmkvManager.KEY_SELECTED_SERVER)) {
//            mActivity.alert(R.string.del_config_comfirm) {
//                positiveButton(android.R.string.ok) {
            mActivity.mainViewModel.removeServer(guid)
            notifyItemRemoved(position)
//                }
//                show()
//            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        mActivity.mainViewModel.swapServer(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        // position is changed, since position is used by click callbacks, need to update range
        if (toPosition > fromPosition)
            notifyItemRangeChanged(fromPosition, toPosition - fromPosition + 1)
        else
            notifyItemRangeChanged(toPosition, fromPosition - toPosition + 1)
        return true
    }

    override fun onItemMoveCompleted() {
        // do nothing
    }
}
