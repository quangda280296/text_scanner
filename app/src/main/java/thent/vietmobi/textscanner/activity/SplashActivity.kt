package thent.vietmobi.textscanner.activity

import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseActivity
import thent.vietmobi.textscanner.databinding.ActivitySplashBinding
import thent.vietmobi.textscanner.utils.FileUtils

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        FileUtils.createFolder()
        fetchDataAds()
        handlerIntentAds()
    }

    private fun handlerIntentAds() {
        Handler().postDelayed({
            handlerIntent()
            if (getInterstitialAd().isLoaded)
                getInterstitialAd().show()
            binding.lottie.pauseAnimation()
        }, 4000)
    }

    private fun handlerIntent() {
        openNewActivity(MainActivity::class.java)
        finish()
    }
}
