package thent.vietmobi.textscanner.dialog

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.SizeUtils
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseDialog
import thent.vietmobi.textscanner.databinding.DialogErrorBinding

class DialogError(activity: Activity) : BaseDialog() {
    private var viewBinding: DialogErrorBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.dialog_error, null, false
    )

    init {
        dialogPlus = DialogPlus.newDialog(activity)
            .setGravity(Gravity.CENTER)
            .setCancelable(false)
            .setContentBackgroundResource(R.color.transparent)
            .setContentHolder(ViewHolder(viewBinding.root))
            .create()
        viewBinding.btnOk.setOnClickListener { activity.finish() }
    }
}