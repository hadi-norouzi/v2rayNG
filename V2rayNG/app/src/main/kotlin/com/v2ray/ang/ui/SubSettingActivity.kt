package com.v2ray.ang.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivitySubSettingBinding
import com.v2ray.ang.databinding.ItemQrcodeBinding
import com.v2ray.ang.databinding.LayoutProgressBinding
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.extension.toast
import com.v2ray.ang.util.AngConfigManager
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.QRCodeDecoder
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SubSettingActivity : BaseActivity(), SubSettingActions {
    private lateinit var binding: ActivitySubSettingBinding

    private var oldHashCode: Int? = null

    var subscriptions: List<Pair<String, SubscriptionItem>> = listOf()
        set(value) {
            oldHashCode = field.hashCode()
            field = value
        }
    private val adapter by lazy { SubSettingRecyclerAdapter(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubSettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        title = getString(R.string.title_sub_setting)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        onBackPressedDispatcher.addCallback(owner = this) {
            val intent = Intent().apply {
                putExtra(
                    MainActivity.SUB_UPDATE,
                    if (oldHashCode != subscriptions.hashCode()) 1 else -1
                )
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        subscriptions = MmkvManager.decodeSubscriptions()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_sub_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.add_config -> {
            startActivity(Intent(this, SubEditActivity::class.java))
            true
        }

        R.id.sub_update -> {
            val dialog = AlertDialog.Builder(this)
                .setView(LayoutProgressBinding.inflate(layoutInflater).root)
                .setCancelable(false)
                .show()

            lifecycleScope.launch(Dispatchers.IO) {
                val count = AngConfigManager.updateConfigViaSubAll()
                delay(500L)
                launch(Dispatchers.Main) {
                    if (count > 0) {
                        toast(R.string.toast_success)
                    } else {
                        toast(R.string.toast_failure)
                    }
                    dialog.dismiss()
                }
            }

            true
        }

        else -> super.onOptionsItemSelected(item)

    }

    private val share_method: Array<out String> by lazy {
        resources.getStringArray(R.array.share_sub_method)
    }

    override fun onShareClick(url: String) {
        AlertDialog.Builder(this)
            .setItems(share_method.asList().toTypedArray()) { _, i ->
                try {
                    when (i) {
                        0 -> {
                            val ivBinding =
                                ItemQrcodeBinding.inflate(LayoutInflater.from(this))
                            ivBinding.ivQcode.setImageBitmap(
                                QRCodeDecoder.createQRCode(url)
                            )
                            AlertDialog.Builder(this).setView(ivBinding.root).show()
                        }

                        1 -> {
                            Utils.setClipboard(this, url)
                        }

                        else -> toast("else")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.show()
    }

    override fun onEditClick(subId: String) {
        startActivity(
            Intent(this, SubEditActivity::class.java)
                .putExtra("subId", subId)
        )
    }

    private val subStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_SUB, MMKV.MULTI_PROCESS_MODE) }


    override fun onItemClick(subId: String, item: SubscriptionItem) {
        item.enabled = !item.enabled
        subStorage?.encode(subId, Gson().toJson(item))
    }
}
