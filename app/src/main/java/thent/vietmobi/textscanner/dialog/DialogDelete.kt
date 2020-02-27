package thent.vietmobi.textscanner.dialog

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseDialog
import thent.vietmobi.textscanner.callback.OnHandlerEventListener
import thent.vietmobi.textscanner.databinding.DialogDeleteBinding

class DialogDelete(
    activity: Activity, message: String,
    onHandlerEventListener: OnHandlerEventListener
) : BaseDialog() {
    private var viewBinding: DialogDeleteBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.dialog_delete, null, false
    )

    init {
        dialogPlus = DialogPlus.newDialog(activity)
            .setGravity(Gravity.CENTER)
            .setCancelable(false)
            .setContentBackgroundResource(R.color.transparent)
            .setContentHolder(ViewHolder(viewBinding.root))
            .create()
        viewBinding.message.text = message
        viewBinding.btnClose.setOnClickListener { dismiss() }
        viewBinding.btnDelete.setOnClickListener {
            dismiss()
            onHandlerEventListener.onEvent()
        }
    }
}