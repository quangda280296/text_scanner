package thent.vietmobi.textscanner.dialog

import android.os.AsyncTask
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.Utils
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.activity.MyDocumentActivity
import thent.vietmobi.textscanner.base.BaseActivity
import thent.vietmobi.textscanner.base.BaseDialog
import thent.vietmobi.textscanner.callback.OnHandlerEventListener
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.databinding.DialogConvertBinding
import thent.vietmobi.textscanner.utils.FileUtils
import thent.vietmobi.textscanner.utils.KeyboardUtils
import thent.vietmobi.textscanner.utils.ToastUtils
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class DialogConvert(private val strContent: String, val context: BaseActivity) : BaseDialog(),
    View.OnClickListener, RadioGroup.OnCheckedChangeListener, OnHandlerEventListener {
    private var isPDF = true
    private var strType = String()

    private var binding: DialogConvertBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.dialog_convert, null, false
    )

    init {
        dialogPlus = DialogPlus.newDialog(context)
            .setGravity(Gravity.CENTER)
            .setCancelable(false)
            .setContentBackgroundResource(R.color.transparent)
            .setContentHolder(ViewHolder(binding.root))
            .create()
        initText()
        handlerAddTextChange()
        isPDF = binding.rdPDF.isChecked
        listener()
    }

    private fun initText() {
        binding.edNameFile.setText(
            SimpleDateFormat(Constant.dayFormat, Locale.getDefault()).format(Date())
        )
        binding.edPath.setText(
            context.getString(
                R.string.format_path, Constant.IMAGE_FOLDER,
                binding.edNameFile.text.toString(), Constant.pdfExtension
            )
        )
        binding.txtEndPoint.text = Constant.pdfExtension
        setUpText(
            binding.edPath, binding.edPath.text.toString(),
            context.getString(R.string.format_path__)
        )
        binding.edNameFile.setSelectAllOnFocus(true)
        strType = if (isPDF) Constant.pdfExtension else Constant.txtExtension
    }

    private fun setUpText(editText: EditText, textResource: String, txtLabel: String) {
        val spannable = SpannableStringBuilder(textResource)
        spannable.setSpan(
            ForegroundColorSpan(context.resources.getColor(android.R.color.darker_gray)),
            textResource.indexOf(txtLabel), textResource.indexOf(txtLabel) + txtLabel.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        editText.text = spannable
    }

    private fun handlerAddTextChange() {
        binding.edNameFile.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count != 0) {
                    handlerPath()
                    setUpText(
                        binding.edPath, binding.edPath.text.toString(),
                        context.getString(R.string.format_path__)
                    )
                } else {
                    binding.edPath.text = null
                    binding.edPath.hint =
                        context.getString(R.string.format_path_, Constant.IMAGE_FOLDER)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

        })
    }

    private fun handlerPath() {
        strType = if (isPDF) Constant.pdfExtension else Constant.txtExtension
        binding.txtEndPoint.text = strType
        if (binding.edNameFile.text.toString().isNotEmpty()) {
            binding.edPath.setText(
                context.getString(
                    R.string.format_path, Constant.IMAGE_FOLDER,
                    binding.edNameFile.text.toString(), strType
                )
            )
        } else {
            binding.edPath.text = null
            binding.edPath.hint =
                context.getString(R.string.format_path_, Constant.IMAGE_FOLDER)
        }
    }

    private fun listener() {
        binding.btnCancel.setOnClickListener(this)
        binding.btnConvert.setOnClickListener(this)
        binding.llName.setOnClickListener(this)
        binding.txtEndPoint.setOnClickListener(this)
        binding.rdGroup.setOnCheckedChangeListener(this)
    }

    private fun validateName() {
        when {
            binding.edNameFile.text.toString().isEmpty() -> {
                ToastUtils.toastShort(context, context.getString(R.string.null_name_file))
            }
            binding.edNameFile.text.toString().length > 20 -> {
                ToastUtils.toastShort(context, context.getString(R.string.number_characters))
            }
            else -> HandlerSaveFileWithAsyncTask(context, object : OnHandlerEventListener {
                override fun onEvent() {
                    handlerEqualName()
                }
            }).execute()
        }
    }

    private fun handlerEqualName() {
        val listFDF = FileUtils.getAllFilePDF(context, File(Constant.IMAGE_FOLDER))
        if (listFDF.size != 0) {
            if (checkNameIsExisted()) return
            savePDF()
        } else savePDF()
    }

    private fun checkNameIsExisted(): Boolean {
        val listFDF = FileUtils.getAllFilePDF(context, File(Constant.IMAGE_FOLDER))
        for (i in 0 until listFDF.size) {
            if (listFDF[i].name?.replace(strType, "").equals(binding.edNameFile.text.toString())) {
                Utils.runOnUiThread {
                    binding.edNameFile.text = null
                    ToastUtils.toastShort(context, context.getString(R.string.equal_name))
                }
                return true
            }
        }
        return false
    }

    private fun savePDF() {
        val document = Document()
        val mFilePath = context.getString(
            R.string.convert_pdf_folder, Constant.IMAGE_FOLDER,
            binding.edNameFile.text, strType
        )
        if (isPDF) {
            PdfWriter.getInstance(document, FileOutputStream(mFilePath))
            document.open()
            document.add(Paragraph(strContent, Font(Font.FontFamily.UNDEFINED, 20F, Font.NORMAL)))
            document.close()
        } else {
            val writer = FileWriter(File(mFilePath))
            writer.append(strContent)
            writer.flush()
            writer.close()
        }
        KeyboardUtils.hideSoftKeyboard(context)
        Utils.runOnUiThread {
            handlerIntentDocument()
            if (context.getInterstitialAd().isLoaded) {
                context.getInterstitialAd().show()
            }
        }
        try {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handlerIntentDocument() {
        dismiss()
        ToastUtils.toastShort(context, context.getString(R.string.save_success))
        context.openNewActivity(MyDocumentActivity::class.java)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCancel -> dismiss()
            R.id.btnConvert -> validateName()
        }
    }

    class HandlerSaveFileWithAsyncTask(
        val context: BaseActivity, private val onHandlerEventListener: OnHandlerEventListener
    ) : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String?): String {
            onHandlerEventListener.onEvent()
            return null.toString()
        }

        override fun onPreExecute() {
            super.onPreExecute()
            context.showProgress()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            context.dismissProgress()
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (checkedId == R.id.rdPDF) {
            isPDF = true
            handlerPath()
        } else {
            isPDF = false
            handlerPath()
        }
    }

    override fun onEvent() {
    }
}