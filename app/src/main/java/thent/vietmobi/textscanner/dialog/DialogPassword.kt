package thent.vietmobi.textscanner.dialog

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseDialog
import thent.vietmobi.textscanner.callback.OnHandlerMessageListener
import thent.vietmobi.textscanner.databinding.DialogPasswordBinding
import thent.vietmobi.textscanner.utils.ToastUtils

class DialogPassword(
    var activity: Activity,
    var onHandlerMessage: OnHandlerMessageListener
) : BaseDialog() {
    private var binding: DialogPasswordBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.dialog_password, null, false
    )

    init {
        dialogPlus = DialogPlus.newDialog(activity)
            .setGravity(Gravity.CENTER)
            .setCancelable(false)
            .setContentBackgroundResource(R.color.transparent)
            .setContentHolder(ViewHolder(binding.root))
            .create()
        binding.btnCancel.setOnClickListener { onHandlerMessage.onCancel() }
        binding.btnRename.setOnClickListener { handlerInputText() }
    }

    private fun handlerInputText() {
        if (binding.edNameFile.text.toString().isEmpty()) {
            ToastUtils.toastShort(activity, activity.getString(R.string.input_password_))
        } else {
            onHandlerMessage.onMessage(binding.edNameFile.text.toString())
        }
    }

    fun clearText() {
        binding.edNameFile.text = null
    }
}