package thent.vietmobi.textscanner.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.adapter.MyDocumentPDFAdapter
import thent.vietmobi.textscanner.base.BaseActivity
import thent.vietmobi.textscanner.callback.OnHandlerEventListener
import thent.vietmobi.textscanner.callback.OnHandlerPermissionListener
import thent.vietmobi.textscanner.callback.OnItemClickWithSelectListener
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.databinding.ActivityMyDocumentBinding
import thent.vietmobi.textscanner.databinding.ItemFileBinding
import thent.vietmobi.textscanner.dialog.*
import thent.vietmobi.textscanner.model.ItemPDF
import thent.vietmobi.textscanner.utils.FileUtils
import thent.vietmobi.textscanner.utils.TimeUtils
import thent.vietmobi.textscanner.widget.MyPullToRefresh
import java.io.File
import java.text.SimpleDateFormat

class MyDocumentActivity : BaseActivity(), View.OnClickListener, OnHandlerEventListener,
    OnItemClickWithSelectListener, OnHandlerPermissionListener {

    private var isSortDate = true
    private var list = ArrayList<ItemPDF>()
    private lateinit var adapter: MyDocumentPDFAdapter
    private lateinit var binding: ActivityMyDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_document)
        adapter = MyDocumentPDFAdapter(list, this, this)
        listener()
        setUpAdapter()
        askPermission(this@MyDocumentActivity)
        handlerRefresh()
        binding.adView.addView(initBanner())
    }

    private fun handlerSelectAll() {
        adapter.setAllSelected(true)
        handlerTitleSelected()
    }

    private fun handlerBack() {
        if (adapter.showOptions) handlerResetWithShowOption(false)
        else finish()
    }

    private fun handlerSortName() {
        list.sortWith(Comparator { one, other -> one.name!!.compareTo(other.name!!) })
        adapter.notifyDataSetChanged()
    }

    private fun handlerResetWithShowOption(isShowOptions: Boolean) {
        adapter.setOption(isShowOptions)
        adapter.notifyDataSetChanged()
        handlerChangeWhenShowOption(adapter.showOptions)
    }

    private fun setUpAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
    }

    private fun listener() {
        binding.btnBack.setOnClickListener(this)
        binding.btnSort.setOnClickListener(this)
        binding.btnShare.setOnClickListener(this)
        binding.btnSelectAll.setOnClickListener(this)
    }

    private fun handlerShare() {
        if (adapter.handlerGetListSelected().size == 0) {
            DialogMessage(
                this, R.raw.file, getString(R.string.warning),
                getString(R.string.null_file), true
            ).show()
        } else FileUtils.shareListFile(adapter.handlerGetListSelected(), this)
    }

    private fun handlerDeleteFile(itemPDF: ItemPDF) {
        DialogDelete(this, getString(R.string.wait_delete_file),
            object : OnHandlerEventListener {
                override fun onEvent() {
                    File(itemPDF.path!!).delete()
                    GetFileWithAsyncTask().execute()
                }
            }).show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun handlerSortDate() {
        list.sortWith(Comparator { one, other ->
            SimpleDateFormat(TimeUtils.DATE_FORMAT_TIME).parse(other.createAt!!)!!.compareTo(
                SimpleDateFormat(TimeUtils.DATE_FORMAT_TIME).parse(one.createAt!!)!!
            )
        })
        adapter.notifyDataSetChanged()
    }

    private fun handlerRefresh() {
        binding.swRefresh.setOnRefreshBegin(
            binding.recyclerView, MyPullToRefresh.PullToRefreshHeader(this), object :
                MyPullToRefresh.OnRefreshBegin {
                override fun refresh() {
                    GetFileWithAsyncTask().execute()
                }
            })
    }

    private fun handlerPopUp() {
        val popup = PopupMenu(this, binding.btnSort)
        popup.inflate(R.menu.sort_menu)
        popup.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.sort_name -> {
                    isSortDate = false
                    handlerSortName()
                }
                R.id.sort_date -> {
                    isSortDate = true
                    handlerSortDate()
                }
            }
            true
        }
        popup.show()
    }

    private fun handlerChangeWhenShowOption(isShowOptions: Boolean) {
        if (isShowOptions) {
            binding.btnShare.visibility = View.VISIBLE
            binding.btnSelectAll.visibility = View.VISIBLE
            Glide.with(this).load(R.drawable.ic_close_button).into(binding.btnBack)
            Glide.with(this).load(R.drawable.ic_delete_button).into(binding.btnSort)
        } else {
            adapter.setAllSelected(false)
            handlerTitleSelected()
            binding.btnShare.visibility = View.GONE
            binding.btnSelectAll.visibility = View.GONE
            Glide.with(this).load(R.drawable.ic_back_button).into(binding.btnBack)
            Glide.with(this).load(R.drawable.ic_sort).into(binding.btnSort)
        }
    }

    private fun handlerDeleteWithSort() {
        if (adapter.showOptions) {
            if (adapter.handlerGetListSelected().size == 0) {
                DialogMessage(
                    this, R.raw.file, getString(R.string.warning),
                    getString(R.string.null_file), true
                ).show()
            } else {
                DialogDelete(
                    this, getString(R.string.want_delete_selected_file),
                    object : OnHandlerEventListener {
                        override fun onEvent() {
                            for (i in 0 until adapter.handlerGetListSelected().size) {
                                File(adapter.handlerGetListSelected()[i].path!!).delete()
                            }
                            adapter.setOption(false)
                            GetFileWithAsyncTask().execute()
                            handlerChangeWhenShowOption(adapter.showOptions)
                        }
                    }).show()
            }
        } else {
            handlerPopUp()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetFileWithAsyncTask : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String?): String {
            list.clear()
            list.addAll(
                FileUtils.getAllFilePDF(this@MyDocumentActivity, File(Constant.IMAGE_FOLDER))
            )
            return null.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            binding.progress.visibility = View.GONE
            if (isSortDate) handlerSortDate() else handlerSortName()
            handlerTitleSelected()
            adapter.notifyDataSetChanged()
            if (list.size == 0) {
                binding.cv.visibility = View.GONE
                binding.tvNoResult.visibility = View.VISIBLE
            } else {
                binding.cv.visibility = View.VISIBLE
                binding.tvNoResult.visibility = View.GONE
            }
            binding.swRefresh.refreshComplete()
        }
    }

    override fun onEvent() {
        GetFileWithAsyncTask().execute()
    }

    override fun onItemClicked(itemPDF: ItemPDF) {
        if (itemPDF.isPDF) {
            val bundle = Bundle()
            bundle.putString(Constant.TITLE, itemPDF.name)
            bundle.putString(Constant.LINK, itemPDF.path)
            bundle.putBoolean(Constant.PASSWORD, itemPDF.isPassword)
            openNewActivityForResult(bundle, PdfViewerActivity::class.java)
        } else FileUtils.openPDFFile(itemPDF.path, itemPDF.isPDF, this)
    }

    override fun onItemSelected(position: Int) {
        list[position].selected = !list[position].selected
        handlerTitleSelected()
        adapter.notifyItemChanged(position)
    }

    override fun onItemLongClicked(itemFile: ItemFileBinding) {
        itemFile.imgCheckBox.visibility = View.VISIBLE
        itemFile.imgSelected.visibility = View.GONE
        handlerResetWithShowOption(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BASE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            GetFileWithAsyncTask().execute()
        }
    }

    override fun onBackPressed() {
        if (adapter.showOptions) {
            handlerResetWithShowOption(false)
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBack -> handlerBack()
            R.id.btnSort -> handlerDeleteWithSort()
            R.id.btnShare -> handlerShare()
            R.id.btnSelectAll -> handlerSelectAll()
        }
    }

    private fun handlerTitleSelected() {
        if (adapter.handlerGetListSelected().size == 0) {
            binding.title.text = getString(R.string.my_doc)
        } else {
            binding.title.text =
                getString(R.string.count_selected, adapter.handlerGetListSelected().size)
        }
    }

    override fun onItemImageClicked(itemPDF: ItemPDF, imageView: ImageView) {
        val popup = PopupMenu(this, imageView)
        popup.inflate(R.menu.pdf_menu)
        popup.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.nav_share -> {
                    FileUtils.shareFile(itemPDF.path!!, this)
                }
                R.id.nav_delete -> {
                    handlerDeleteFile(itemPDF)
                }
                R.id.nav_detail -> {
                    DialogInformation(this, itemPDF).show()
                }
                else -> DialogRename(this, itemPDF, adapter, this).show()
            }
            true
        }
        popup.show()
    }

    override fun onEventPermission() {
        GetFileWithAsyncTask().execute()
    }

    override fun onEventDenied() {
        finish()
    }
}
