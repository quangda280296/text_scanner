package thent.vietmobi.textscanner.activity

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.databinding.DataBindingUtil
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseActivity
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.databinding.ActivityCropCameraBinding
import thent.vietmobi.textscanner.utils.ScannerUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

class CropCameraActivity : BaseActivity(), View.OnClickListener,
    CropImageView.OnCropImageCompleteListener {
    private var listRotate = ArrayList<String>()
    private lateinit var binding: ActivityCropCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop_camera)
        binding.cropImageView.setImageUriAsync(Uri.fromFile(File(intentBundle?.getString(Constant.LINK)!!)))
        listener()
    }

    private fun listener() {
        binding.btnCancel.setOnClickListener(this)
        binding.btnDone.setOnClickListener(this)
        binding.cropImageView.setOnCropImageCompleteListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCancel -> finish()
            R.id.btnDone -> handlerCropImage()
        }
    }

    private fun handlerScan(uriImage: Uri, rotate: Float, key: String) {
        val observable =
            Observable.just(ScannerUtils.inspect(uriImage, this@CropCameraActivity, rotate))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
        observable.subscribe(object : Observer<String> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: String) {
                handlerRotate(t, key, uriImage)
            }

            override fun onError(e: Throwable) {
                e.stackTrace
            }
        })
    }

    private fun handlerRotate(result: String?, key: String, uriImage: Uri) {
        if (key == Constant.ROTATE_0) {
            listRotate.clear()
        }
        listRotate.add(result!!)
        when (key) {
            Constant.ROTATE_0 -> {
                showProgress()
                handlerScan(uriImage, 90F, Constant.ROTATE_90)
            }
            Constant.ROTATE_90 -> {
                handlerScan(uriImage, 180F, Constant.ROTATE_180)
            }
            Constant.ROTATE_180 -> {
                handlerScan(uriImage, 270F, Constant.ROTATE_270)
            }
            else -> handlerContent()
        }
    }

    private fun handlerContent() {
        listRotate.sortWith(Comparator { one, other -> other.length.compareTo(one.length) })
        dismissProgress()
        if (listRotate[0] != null.toString()) {
            val bundle = Bundle()
            bundle.putString(Constant.LINK, listRotate[0])
            openNewActivity(bundle, ScanTextActivity::class.java)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat(Constant.dayFormat, Locale.getDefault()).format(Date())
        val imageFileName = getString(R.string.format_timestamp, timeStamp)
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, Constant.jpgExtension, storageDir)
    }

    private fun handlerCropImage() {
        showProgress()
        binding.cropImageView.saveCroppedImageAsync(Uri.fromFile(createImageFile()))
    }

    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        handlerScan(result!!.uri, 0F, Constant.ROTATE_0)
    }
}
