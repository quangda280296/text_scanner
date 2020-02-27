package thent.vietmobi.textscanner.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_policy.*
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.base.BaseActivity
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.databinding.ActivityPolicyBinding

class PolicyActivity : BaseActivity() {
    private lateinit var binding: ActivityPolicyBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_policy)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.loadUrl(Constant.LINK_PRIVACY_POLiCY)
        binding.btnBack.setOnClickListener { finish() }
    }
}
