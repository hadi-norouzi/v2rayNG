package com.v2ray.ang.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ItemRecyclerMainBinding
import com.v2ray.ang.helper.ItemTouchHelperAdapter
import com.v2ray.ang.ui.MainRecyclerAdapter.MainViewHolder
import com.v2ray.ang.util.MmkvManager

class ConfigRecyclerAdapter(
    private val subId: String,
) : RecyclerView.Adapter<MainRecyclerAdapter.BaseViewHolder>(),
    ItemTouchHelperAdapter {

    private val configs by lazy {
        MmkvManager.getAllConfigs(subId)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MainRecyclerAdapter.BaseViewHolder {
        return MainViewHolder(
            ItemRecyclerMainBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = configs.size

    override fun onBindViewHolder(holder: MainRecyclerAdapter.BaseViewHolder, position: Int) {
        if (holder is MainViewHolder) {
            val guid = configs[position].guid
            val config = configs[position].config


            val outbound = config.getProxyOutbound()
            val aff = MmkvManager.decodeServerAffiliationInfo(guid)

            holder.itemMainBinding.tvName.text = config.remarks
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            holder.itemMainBinding.tvTestResult.text = aff?.getTestDelayString() ?: ""
            if ((aff?.testDelayMillis ?: 0L) < 0L) {
                holder.itemMainBinding.tvTestResult.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, R.color.colorPingRed
                    )
                )
            } else {
                holder.itemMainBinding.tvTestResult.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, R.color.colorPing
                    )
                )
            }
//            if (guid == mainStorage?.decodeString(MmkvManager.KEY_SELECTED_SERVER)) {
//                holder.itemMainBinding.layoutIndicator.setBackgroundResource(R.color.colorAccent)
//            } else {
//                holder.itemMainBinding.layoutIndicator.setBackgroundResource(0)
//            }
//            holder.itemMainBinding.tvSubscription.text = ""
//            val json = subStorage?.decodeString(config.subscriptionId)
//            if (!json.isNullOrBlank()) {
//                val sub = Gson().fromJson(json, SubscriptionItem::class.java)
//                holder.itemMainBinding.tvSubscription.text = sub.remarks
//            }

//            var shareOptions = share_method.asList()
//            when (config.configType) {
//                EConfigType.CUSTOM -> {
//                    holder.itemMainBinding.tvType.text =
//                        mActivity.getString(R.string.server_customize_config)
//                    shareOptions = shareOptions.takeLast(1)
//                }
//
//                EConfigType.VLESS -> {
//                    holder.itemMainBinding.tvType.text = config.configType.name
//                }
//
//                else -> {
//                    holder.itemMainBinding.tvType.text = config.configType.name.lowercase()
//                }
//            }

            val strState = try {
                "${outbound?.getServerAddress()?.dropLast(3)}*** : ${outbound?.getServerPort()}"
            } catch (e: Exception) {
                ""
            }

            holder.itemMainBinding.tvStatistics.text = strState

//            holder.itemMainBinding.layoutShare.setOnClickListener {
//                AlertDialog.Builder(mActivity).setItems(shareOptions.toTypedArray()) { _, i ->
//                    try {
//                        when (i) {
//                            0 -> {
//                                if (config.configType == EConfigType.CUSTOM) {
//                                    shareFullContent(guid)
//                                } else {
//                                    val ivBinding =
//                                        ItemQrcodeBinding.inflate(LayoutInflater.from(mActivity))
//                                    ivBinding.ivQcode.setImageBitmap(
//                                        AngConfigManager.share2QRCode(
//                                            guid
//                                        )
//                                    )
//                                    AlertDialog.Builder(mActivity).setView(ivBinding.root).show()
//                                }
//                            }
//
//                            1 -> {
//                                if (AngConfigManager.share2Clipboard(mActivity, guid) == 0) {
//                                    mActivity.toast(R.string.toast_success)
//                                } else {
//                                    mActivity.toast(R.string.toast_failure)
//                                }
//                            }
//
//                            2 -> shareFullContent(guid)
//                            else -> mActivity.toast("else")
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }.show()
//            }

//            holder.itemMainBinding.layoutEdit.setOnClickListener {
//                val intent = Intent().putExtra("guid", guid)
//                    .putExtra("isRunning", isRunning)
//                if (config.configType == EConfigType.CUSTOM) {
//                    mActivity.startActivity(
//                        intent.setClass(
//                            mActivity,
//                            ServerCustomConfigActivity::class.java
//                        )
//                    )
//                } else {
//                    mActivity.startActivity(intent.setClass(mActivity, ServerActivity::class.java))
//                }
//            }
//            holder.itemMainBinding.layoutRemove.setOnClickListener {
//                if (guid != mainStorage?.decodeString(MmkvManager.KEY_SELECTED_SERVER)) {
//                    if (settingsStorage?.decodeBool(AppConfig.PREF_CONFIRM_REMOVE) == true) {
//                        AlertDialog.Builder(mActivity).setMessage(R.string.del_config_comfirm)
//                            .setPositiveButton(android.R.string.ok) { _, _ ->
//                                removeServer(guid, position)
//                            }
//                            .setNegativeButton(android.R.string.no) { _, _ ->
//                                //do noting
//                            }
//                            .show()
//                    } else {
//                        removeServer(guid, position)
//                    }
//                } else {
//                    application.toast(R.string.toast_action_not_allowed)
//                }
//            }

//            holder.itemMainBinding.infoContainer.setOnClickListener {
//                val selected = mainStorage?.decodeString(MmkvManager.KEY_SELECTED_SERVER)
//                if (guid != selected) {
//                    mainStorage?.encode(MmkvManager.KEY_SELECTED_SERVER, guid)
//                    if (!TextUtils.isEmpty(selected)) {
//                        notifyItemChanged(mActivity.mainViewModel.getPosition(selected ?: ""))
//                    }
//                    notifyItemChanged(mActivity.mainViewModel.getPosition(guid))
//                    if (isRunning) {
//                        Utils.stopVService(mActivity)
//                        Observable.timer(500, TimeUnit.MILLISECONDS)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe {
//                                V2RayServiceManager.startV2Ray(mActivity)
//                            }
//                    }
//                }
//            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onItemMoveCompleted() {
        TODO("Not yet implemented")
    }

    override fun onItemDismiss(position: Int) {
        TODO("Not yet implemented")
    }
}