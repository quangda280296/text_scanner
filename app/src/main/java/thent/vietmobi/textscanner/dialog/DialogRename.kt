package thent.vietmobi.textscanner.dialog

import android.app.Activity
import android.content.ContentValues
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.adapter.MyDocumentPDFAdapter
import thent.vietmobi.textscanner.base.BaseDialog
import thent.vietmobi.textscanner.callback.OnHandlerEventListener
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.databinding.DialogRenameBinding
import thent.vietmobi.textscanner.model.ItemPDF
import thent.vietmobi.textscanner.utils.KeyboardUtils
import thent.vietmobi.textscanner.utils.ToastUtils
import java.io.File

class DialogRename(
    var activity: Activity, private var itemPDF: ItemPDF, private var adapter: MyDocumentPDFAdapter,
    private var onHandlerEventListener: OnHandlerEventListener
) : BaseDialog() {
    private var checkPDF = String()
    private var binding: DialogRenameBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.dialog_rename, null, false
    )

    init {
        dialogPlus = DialogPlus.newDialog(activity)
            .setGravity(Gravity.CENTER)
            .setCancelable(false)
            .setContentBackgroundResource(R.color.transparent)
            .setContentHolder(ViewHolder(binding.root))
            .create()
        binding.btnCancel.setOnClickListener {
            dismiss()
            KeyboardUtils.hideSoftKeyboard(activity)
        }
        checkPDF = if (itemPDF.isPDF) {
            Constant.pdfExtension
        } else {
            Constant.txtExtension
        }
        binding.edNameFile.setText(itemPDF.name!!.replace(checkPDF, ""))
        binding.btnRename.setOnClickListener { handlerChangeName() }
    }

    private fun handlerChangeName() {
        var newName: String = binding.edNameFile.text.toString()
        if (newName.trim { it <= ' ' }.isEmpty()) {
            binding.edNameFile.error = activity.getString(R.string.space_error)
            return
        }

        if (newName.length > 20) {
            ToastUtils.toastShort(activity, activity.getString(R.string.number_characters))
            return
        }

        val oldFile = File(itemPDF.path!!)
        newName = newName.trim { it <= ' ' }
        if (oldFile.isFile) {
            val newFile =
                File(oldFile.parentFile, activity.getString(R.string.end_point_, newName, checkPDF))
            if (!checkNameIsExisted(newName, adapter.getList())) return
            if (oldFile.renameTo(newFile)) {
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DATA, newFile.absolutePath)
                onHandlerEventListener.onEvent()
                ToastUtils.toastShort(
                    activity, String.format(activity.getString(R.string.rename_success), newName)
                )
                KeyboardUtils.hideSoftKeyboard(activity)
            } else ToastUtils.toastShort(activity, activity.getString(R.string.rename_fail))
            dismiss()
            return
        }
        dismiss()
    }

    private fun checkNameIsExisted(newName: String, arrayList: ArrayList<ItemPDF>): Boolean {
        for (i in 0 until arrayList.size) {
            if (arrayList[i].name.equals(
                    activity.getString(R.string.end_point_, newName, checkPDF)
                )
            ) {
                ToastUtils.toastShort(activity, activity.getString(R.string.exist_name))
                return false
            }
        }
        return true
    }
}