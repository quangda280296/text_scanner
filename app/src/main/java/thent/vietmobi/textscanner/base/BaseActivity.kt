package thent.vietmobi.textscanner.base

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.callback.OnHandlerEventListener
import thent.vietmobi.textscanner.callback.OnHandlerPermissionListener
import thent.vietmobi.textscanner.dialog.DialogPermission
import thent.vietmobi.textscanner.model.AdMobModel
import thent.vietmobi.textscanner.network.manager.RestApiManager
import thent.vietmobi.textscanner.utils.ShareUtils


@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = ProgressDialog(this@BaseActivity)
        progressDialog.setMessage(getString(R.string.wait_me_a_min))
        if (interstitialAd == null) initInterstitialAd()
    }

    fun showProgress() {
        progressDialog.show()
    }

    fun dismissProgress() {
        progressDialog.dismiss()
    }

    private fun getAds(): AdMobModel? {
        return ShareUtils[this@BaseActivity, AdMobModel::class.java.name, AdMobModel::class.java]
    }

    fun initBanner(): AdView {
        val mAdView = AdView(this)
        mAdView.adSize = AdSize.BANNER
        mAdView.adUnitId = if (getAds()?.admob?.banner != null) getAds()?.admob?.banner else ""
        mAdView.loadAd(AdRequest.Builder().build())
        return mAdView
    }

    private fun initInterstitialAd() {
        interstitialAd = InterstitialAd(this@BaseActivity)
        interstitialAd?.adUnitId =
            if (getAds()?.admob?.popup != null) getAds()?.admob?.popup else ""
        interstitialAd?.loadAd(AdRequest.Builder().build())
        interstitialAd?.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                interstitialAd?.loadAd(AdRequest.Builder().build())
            }
        }
    }

    fun fetchDataAds() {
        val a = AdMobModel(
            AdMobModel.AdmobBean(
                "ca-app-pub-3940256099942544/6300978111",
                "ca-app-pub-3940256099942544/1033173712"
            )
        )
        val observableAdsData =
            RestApiManager.instance.getAdsManager().getAds()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
        observableAdsData.subscribe(object : Observer<AdMobModel> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AdMobModel) {
                ShareUtils.put(applicationContext, AdMobModel::class.java.name, t)
            }

            override fun onError(e: Throwable) {
                e.stackTrace
            }
        })
    }

    fun getInterstitialAd(): InterstitialAd {
        return interstitialAd!!
    }

    fun openNewActivity(c: Class<*>?) {
        try {
            val intent = Intent(this, c)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openNewActivityAndClearStack(c: Class<*>?) {
        try {
            val intent = Intent(this, c)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openNewActivityAndClearStack(bundle: Bundle?, c: Class<*>?) {
        try {
            val intent = Intent(this, c)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(BUNDLE_KEY, bundle)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openNewActivity(bundle: Bundle?, c: Class<*>?) {
        try {
            val intent = Intent(this, c)
            intent.putExtra(BUNDLE_KEY, bundle)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openNewActivity(intent: Intent, bundle: Bundle?) {
        try {
            intent.putExtra(BUNDLE_KEY, bundle)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openNewActivityForResult(bundle: Bundle?, c: Class<*>?) {
        try {
            val intent = Intent(this, c)
            intent.putExtra(BUNDLE_KEY, bundle)
            startActivityForResult(intent, BASE_RESULT_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openNewActivityForResult(
        bundle: Bundle?,
        c: Class<*>?,
        request_code: Int
    ) {
        try {
            val intent = Intent(this, c)
            intent.putExtra(BUNDLE_KEY, bundle)
            startActivityForResult(intent, request_code)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun askPermission(onHandlerListener: OnHandlerPermissionListener) {
        if (AndPermission.hasPermissions(
                this,
                Permission.CAMERA,
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            onHandlerListener.onEventPermission()
        } else {
            AndPermission.with(this)
                .runtime()
                .permission(
                    Permission.CAMERA,
                    Permission.READ_EXTERNAL_STORAGE,
                    Permission.WRITE_EXTERNAL_STORAGE
                )
                .onGranted {
                    onHandlerListener.onEventPermission()
                }
                .onDenied {
                    DialogPermission(this, object : OnHandlerEventListener {
                        override fun onEvent() {
                            onHandlerListener.onEventDenied()
                        }
                    }).show()
                }
                .start()
        }
    }

    fun isPermission(): Boolean {
        return AndPermission.hasPermissions(
            this, Permission.CAMERA,
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE
        )
    }

    val intentBundle: Bundle?
        get() = try {
            intent.getBundleExtra(BUNDLE_KEY)
        } catch (e: Exception) {
            null
        }

    companion object {
        const val BUNDLE_KEY = "com.thent.key.BUNDLE_KEY"
        const val BASE_RESULT_CODE = 9001
    }
}