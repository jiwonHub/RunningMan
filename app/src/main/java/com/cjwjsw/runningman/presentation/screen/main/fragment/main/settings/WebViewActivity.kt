package com.cjwjsw.runningman.presentation.screen.main.fragment.main.settings

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView = binding.webView

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true

        webView.webViewClient = WebViewClient()

        webView.loadUrl("https://github.com/jiwonHub/RunningMan/blob/dev/privacy%20policy.md")
    }
}