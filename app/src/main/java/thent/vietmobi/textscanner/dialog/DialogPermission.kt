package thent.vietmobi.textscanner.dialog

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseDialog
import thent.vietmobi.textscanner.callback.OnHandlerEventListener
import thent.vietmobi.textscanner.databinding.DialogPermissionBinding
import thent.vietmobi.textscanner.utils.NetWorkUtils

class DialogPermission(activity: Activity, onHandlerEventListener: OnHandlerEventListener) : BaseDialog() {
    private var viewBinding: DialogPermissionBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.dialog_permission, null, false
    )

    init {
        dialogPlus = DialogPlus.newDialog(activity)
            .setGravity(Gravity.CENTER)
            .setCancelable(false)
            .setContentBackgroundResource(R.color.transparent)
            .setContentHolder(ViewHolder(viewBinding.root))
            .create()
        viewBinding.title.setTextColor(Color.RED)
        viewBinding.btnClose.setOnClickListener {
            dismiss()
            onHandlerEventListener.onEvent()
        }
        viewBinding.btnAccept.setOnClickListener {
            NetWorkUtils.intentPermissionSettingDevice(activity)
            dismiss()
        }
    }
}