package thent.vietmobi.textscanner.dialog

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseDialog
import thent.vietmobi.textscanner.databinding.DialogInfoBinding
import thent.vietmobi.textscanner.model.ItemPDF

class DialogInformation(activity: Activity, itemPDF: ItemPDF) : BaseDialog() {
    private var viewBinding: DialogInfoBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.dialog_info, null, false
    )

    init {
        dialogPlus = DialogPlus.newDialog(activity)
            .setGravity(Gravity.CENTER)
            .setCancelable(false)
            .setContentBackgroundResource(R.color.transparent)
            .setContentHolder(ViewHolder(viewBinding.root))
            .create()

        viewBinding.txtNameFile.text = itemPDF.name
        viewBinding.txtCreateAt.text = itemPDF.createAt
        viewBinding.txtData.text = itemPDF.dataKB
        viewBinding.txtPath.text = itemPDF.path
        viewBinding.btnClose.setOnClickListener { dismiss() }
    }
}