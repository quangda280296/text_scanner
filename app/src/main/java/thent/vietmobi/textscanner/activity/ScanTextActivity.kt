package thent.vietmobi.textscanner.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseActivity
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.databinding.ActivityScanTextBinding
import thent.vietmobi.textscanner.dialog.DialogConvert
import thent.vietmobi.textscanner.dialog.DialogError
import thent.vietmobi.textscanner.utils.KeyboardUtils
import thent.vietmobi.textscanner.utils.ToastUtils


class ScanTextActivity : BaseActivity(), View.OnClickListener {
    private var isEdit = false
    private lateinit var binding: ActivityScanTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan_text)
        handlerDataIsEmpty()
        listener()
        handlerTextChange()
        binding.adView.addView(initBanner())
    }

    private fun handlerDataIsEmpty() {
        if (intentBundle?.getString(Constant.LINK)!!.isNotEmpty()) {
            binding.edContent.setText(intentBundle?.getString(Constant.LINK))
        } else {
            DialogError(this).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handlerTextChange() {
        binding.edContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerScanTextChange()
            }
        })

        binding.edContent.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                KeyboardUtils.hideSoftKeyboard(this)
            }
            false
        }
    }

    private fun handlerScanTextChange() {
        if (!isEdit) {
            isEdit = true
            binding.edContent.isFocusable = true
            binding.btnBack.setImageResource(R.drawable.ic_close)
            binding.btnCopy.visibility = View.GONE
            binding.btnSave.setImageResource(R.drawable.ic_done_icon)
        }
    }

    private fun listener() {
        binding.btnBack.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnCopy.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBack -> handlerHasEditBtnClose()
            R.id.btnCopy -> handlerValidateCopy()
            R.id.btnSave -> handlerConvert()
        }
    }

    private fun handlerConvert() {
        DialogConvert(binding.edContent.text.toString(), this).show()
        KeyboardUtils.hideSoftKeyboard(this)
    }

    private fun handlerValidateCopy() {
        KeyboardUtils.hideSoftKeyboard(this)
        if (binding.edContent.text.isEmpty()) {
            ToastUtils.toastShort(this, getString(R.string.null_content))
        } else {
            KeyboardUtils.handlerCopy(this, binding.edContent.text.toString())
            ToastUtils.toastShort(this, getString(R.string.copy_success))
        }
    }

    private fun handlerHasEditBtnClose() {
        if (isEdit) {
            binding.btnBack.setImageResource(R.drawable.ic_back_button)
            binding.btnCopy.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE
            binding.btnSave.setImageResource(R.drawable.ic_save_icon)
            binding.edContent.setText(intentBundle?.getString(Constant.LINK))
            isEdit = false
            KeyboardUtils.hideSoftKeyboard(this)
        } else finish()
        isEdit = false
    }
}
