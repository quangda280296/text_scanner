package thent.vietmobi.textscanner.base

import com.orhanobut.dialogplus.DialogPlus


abstract class BaseDialog {
    var dialogPlus: DialogPlus? = null

    open fun show() {
        if (dialogPlus != null) {
            dialogPlus!!.show()
        }
    }

    open fun dismiss() {
        if (dialogPlus != null) {
            dialogPlus!!.dismiss()
        }
    }
}