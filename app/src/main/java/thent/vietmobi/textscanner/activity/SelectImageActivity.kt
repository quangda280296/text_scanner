package thent.vietmobi.textscanner.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.adapter.MediaPickerAdapter
import thent.vietmobi.textscanner.base.BaseActivity
import thent.vietmobi.textscanner.callback.OnHandlerPermissionListener
import thent.vietmobi.textscanner.callback.OnItemClickListener
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.databinding.ActivitySelectImageBinding
import thent.vietmobi.textscanner.dialog.DialogMessage
import thent.vietmobi.textscanner.model.ItemMediaModel
import thent.vietmobi.textscanner.model.ItemPDF
import thent.vietmobi.textscanner.utils.*
import thent.vietmobi.textscanner.widget.MyPullToRefresh
import kotlin.collections.ArrayList

class SelectImageActivity : BaseActivity(), OnItemClickListener, OnHandlerPermissionListener {
    private lateinit var binding: ActivitySelectImageBinding
    private lateinit var adapter: MediaPickerAdapter
    private var list = ArrayList<ItemMediaModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_image)
        adapter = MediaPickerAdapter(this, list, this)
        handlerRefresh()
        setUpAdapter()
        binding.btnBack.setOnClickListener { finish() }
        binding.adView.addView(initBanner())
        askPermission(this@SelectImageActivity)
    }

    private fun setUpAdapter() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.addItemDecoration(
            GridUtils(3, Utils.dip2px(this, 10f), true)
        )
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
    }

    private fun handlerRefresh() {
        binding.swRefresh.setOnRefreshBegin(
            binding.recyclerView, MyPullToRefresh.PullToRefreshHeader(this), object :
                MyPullToRefresh.OnRefreshBegin {
                override fun refresh() {
                    GetAllImageWithAsyncTask().execute()
                }
            })
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetAllImageWithAsyncTask : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String?): String {
            list.clear()
            list.addAll(AlbumUtils.getAllShownImagesPath(this@SelectImageActivity))
            return null.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            binding.progress.visibility = View.GONE
            adapter.notifyDataSetChanged()
            if (list.size == 0) binding.tvNoResult.visibility = View.VISIBLE
            else binding.tvNoResult.visibility = View.GONE
            binding.swRefresh.refreshComplete()
        }
    }


    override fun onItemClickClicked(itemPDF: ItemPDF) {
    }

    override fun onItemClickWithPosition(position: Int) {
        if (NetWorkUtils.isNetworkConnected(this)) {
            val bundle = Bundle()
            bundle.putString(Constant.LINK, list[position].uri)
            openNewActivity(bundle, CropImageActivity::class.java)
        } else {
            DialogMessage(
                this, R.raw.error_remote, getString(R.string.warning),
                getString(R.string.not_internet), true
            ).show()
        }
    }

    override fun onEventPermission() {
        GetAllImageWithAsyncTask().execute()
    }

    override fun onEventDenied() {
        finish()
    }
}
