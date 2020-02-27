package thent.vietmobi.textscanner.activity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_main.view.*
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseActivity
import thent.vietmobi.textscanner.callback.OnHandlerEventListener
import thent.vietmobi.textscanner.callback.OnHandlerPermissionListener
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.databinding.ActivityMainBinding
import thent.vietmobi.textscanner.dialog.DialogMessage
import thent.vietmobi.textscanner.utils.NetWorkUtils
import thent.vietmobi.textscanner.utils.ToastUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity(), View.OnClickListener, OnHandlerPermissionListener,
    OnHandlerEventListener {
    private lateinit var binding: ActivityMainBinding
    private var REQUEST_IMAGE_CAPTURE = 9001
    private var imageFilePath: String? = null
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpDraw()
        listener()
        binding.adView.addView(initBanner())
    }

    private fun listener() {
        binding.btnSelect.setOnClickListener(this)
        binding.btnDocument.setOnClickListener(this)
        binding.imgCapture.setOnClickListener(this)
        binding.navView.llMore.setOnClickListener(this)
        binding.navView.llPrivacy.setOnClickListener(this)
        binding.navView.llRate.setOnClickListener(this)
        binding.navView.llShare.setOnClickListener(this)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        ToastUtils.toastShort(this, getString(R.string.double_tab))
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun setUpDraw() {
        setSupportActionBar(binding.toolbar)
        val toggle = object : ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ) {}
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.toolbar.setNavigationIcon(R.drawable.ic_menu_button)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(Constant.dayFormat, Locale.getDefault()).format(Date())
        val imageFileName = getString(R.string.format_timestamp, timeStamp)
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, Constant.jpgExtension, storageDir)
        imageFilePath = image.absolutePath
        return image
    }

    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            val photoURI = FileProvider.getUriForFile(
                this,
                getString(R.string.file_provider, applicationContext.packageName.toString()),
                photoFile!!
            )
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (NetWorkUtils.isNetworkConnected(this)) {
                val bundle = Bundle()
                bundle.putString(Constant.LINK, imageFilePath)
                openNewActivity(bundle, CropCameraActivity::class.java)
            } else {
                DialogMessage(
                    this, R.raw.error_remote, getString(R.string.warning),
                    getString(R.string.not_internet), true
                ).show()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llMore -> NetWorkUtils.eventMoreApp(this)
            R.id.llRate -> NetWorkUtils.intentToChPlay(this)
            R.id.llPrivacy -> openNewActivity(PolicyActivity::class.java)
            R.id.llShare -> NetWorkUtils.shareUrl(this, NetWorkUtils.getPackageAppInChPlay(this))
            R.id.btnSelect -> openNewActivity(SelectImageActivity::class.java)
            R.id.imgCapture -> askPermission(this)
            R.id.btnDocument -> handlerIntentDocument()
        }
    }

    private fun handlerIntentDocument() {
        openNewActivity(MyDocumentActivity::class.java)
        if (getInterstitialAd().isLoaded && isPermission()) getInterstitialAd().show()
    }

    override fun onEventPermission() {
        openCameraIntent()
    }

    override fun onEventDenied() {
    }

    override fun onEvent() {}
}
