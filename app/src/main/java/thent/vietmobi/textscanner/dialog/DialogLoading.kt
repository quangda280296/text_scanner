package thent.vietmobi.textscanner.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.blankj.utilcode.util.SizeUtils
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import thent.vietmobi.textscanner.R

class DialogLoading(context: Context) {
    private var dialogPlus: DialogPlus

    fun show() {
        dialogPlus.show()
    }

    fun dismiss() {
        dialogPlus.dismiss()
    }

    init {
        val rootView: View = LayoutInflater.from(context)
            .inflate(R.layout.layout_progress, null, false)
        val viewHolder = ViewHolder(rootView)
        dialogPlus = DialogPlus.newDialog(context)
            .setContentHolder(viewHolder)
            .setContentBackgroundResource(R.color.transparent)
            .setGravity(Gravity.CENTER)
            .setContentWidth(RelativeLayout.LayoutParams.WRAP_CONTENT)
            .setContentHeight(RelativeLayout.LayoutParams.WRAP_CONTENT)
            .setCancelable(false)
            .setMargin(
                SizeUtils.dp2px(30F),
                SizeUtils.dp2px(30F),
                SizeUtils.dp2px(30F),
                SizeUtils.dp2px(30F)
            ).setPadding(
                SizeUtils.dp2px(10F),
                SizeUtils.dp2px(0F),
                SizeUtils.dp2px(10F),
                SizeUtils.dp2px(20F)
            ).create()
    }
}