package thent.vietmobi.textscanner.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnTapListener
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseActivity
import thent.vietmobi.textscanner.callback.OnHandlerEventListener
import thent.vietmobi.textscanner.callback.OnHandlerMessageListener
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.databinding.ActivityPdfViewerBinding
import thent.vietmobi.textscanner.dialog.DialogDelete
import thent.vietmobi.textscanner.dialog.DialogPassword
import thent.vietmobi.textscanner.utils.FileUtils
import thent.vietmobi.textscanner.utils.KeyboardUtils
import thent.vietmobi.textscanner.utils.ToastUtils
import java.io.File

class PdfViewerActivity : BaseActivity(), View.OnClickListener,
    OnPageChangeListener, OnErrorListener, OnLoadCompleteListener, OnHandlerMessageListener,
    OnTapListener {
    private lateinit var binding: ActivityPdfViewerBinding
    private var pageNumber = 0
    private lateinit var dialogPassword: DialogPassword
    private var strPage = String()
    private var doubleBackToExitPressedOnce = false
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_viewer)
        binding.title.text = intentBundle?.getString(Constant.TITLE)
        dialogPassword = DialogPassword(this, this)
        listener()
        handlerPassword()
        binding.adView.addView(initBanner())
    }

    private fun handlerPassword() {
        if (intentBundle!!.getBoolean(Constant.PASSWORD, true)) dialogPassword.show()
        else displayFromUriWithOutPassWord()
    }

    private fun listener() {
        binding.btnBack.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)
        binding.btnShare.setOnClickListener(this)
    }

    private fun displayFromUriWithOutPassWord() {
        binding.progress.visibility = View.VISIBLE
        binding.pdfView.fromUri(Uri.fromFile(File(intentBundle!!.getString(Constant.LINK, ""))))
            .defaultPage(pageNumber)
            .enableSwipe(true)
            .swipeHorizontal(true)
            .onPageChange(this)
            .onLoad(this)
            .spacing(10)
            .autoSpacing(true)
            .pageSnap(true)
            .pageFling(true)
            .nightMode(false)
            .onTap(this)
            .load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
        strPage = getString(R.string.format_page_, page + 1, pageCount)
        doubleBackToExitPressedOnce = true
        toast = ToastUtils.initToast(this, strPage)
        toast?.show()
    }

    override fun loadComplete(nbPages: Int) {
        if (intentBundle!!.getBoolean(Constant.PASSWORD, true)) {
            dialogPassword.dismiss()
            KeyboardUtils.hideSoftKeyboard(this)
        }
        binding.progress.visibility = View.GONE
    }

    override fun onMessage(message: String) {
        binding.progress.visibility = View.VISIBLE
        binding.pdfView.fromUri(Uri.fromFile(File(intentBundle!!.getString(Constant.LINK, ""))))
            .defaultPage(pageNumber)
            .swipeHorizontal(true)
            .enableSwipe(true)
            .onPageChange(this)
            .onLoad(this)
            .spacing(10)
            .pageSnap(true)
            .pageFling(true)
            .autoSpacing(true)
            .password(message)
            .onTap(this)
            .onError(this)
            .load()
    }

    override fun onCancel() {
        finish()
    }

    override fun onError(t: Throwable?) {
        dialogPassword.clearText()
        binding.progress.visibility = View.GONE
        KeyboardUtils.hideSoftKeyboard(this)
        ToastUtils.toastShort(this, getString(R.string.wrong_pass))
    }

    private fun handlerDeleteFile() {
        DialogDelete(
            this, getString(R.string.wait_delete_file),
            object : OnHandlerEventListener {
                override fun onEvent() {
                    File(intentBundle!!.getString(Constant.LINK, "")).delete()
                    val bundle = Bundle()
                    bundle.putBoolean(Constant.BOOLEAN, true)
                    handlerAfterDelete()
                }
            }).show()
    }

    private fun handlerAfterDelete() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBack -> finish()
            R.id.btnDelete -> handlerDeleteFile()
            R.id.btnShare -> FileUtils.shareFile(intentBundle!!.getString(Constant.LINK, ""), this)
        }
    }

    override fun onTap(e: MotionEvent?): Boolean {
        if (doubleBackToExitPressedOnce) {
            toast?.cancel()
            doubleBackToExitPressedOnce = false
            return true
        }
        doubleBackToExitPressedOnce = true
        toast = ToastUtils.initToast(this, strPage)
        toast?.show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 3000)
        return true
    }
}
