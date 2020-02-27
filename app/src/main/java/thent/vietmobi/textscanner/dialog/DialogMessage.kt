package thent.vietmobi.textscanner.dialog

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseDialog
import thent.vietmobi.textscanner.databinding.DialogMessageBinding

class DialogMessage(
    activity: Activity, raw: Int, title: String, message: String, colorError: Boolean
) : BaseDialog() {
    private var viewBinding: DialogMessageBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.dialog_message, null, false
    )

    init {
        dialogPlus = DialogPlus.newDialog(activity)
            .setGravity(Gravity.CENTER)
            .setCancelable(false)
            .setContentBackgroundResource(R.color.transparent)
            .setContentHolder(ViewHolder(viewBinding.root))
            .create()
        if (colorError) {
            viewBinding.title.setTextColor(Color.RED)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewBinding.title.setTextColor(activity.getColor(R.color.colorAccent))
            } else {
                viewBinding.title.setTextColor(Color.CYAN)
            }
        }
        viewBinding.title.text = title
        viewBinding.message.text = message
        viewBinding.animationView.setAnimation(raw)
        viewBinding.btnClose.setOnClickListener { dismiss() }
    }
}