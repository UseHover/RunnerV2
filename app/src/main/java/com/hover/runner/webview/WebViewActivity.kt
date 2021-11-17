package com.hover.runner.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hover.runner.databinding.WebviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WebViewActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = WebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.Main) {
            delay(2000)
            binding.fakeLoading.visibility = View.GONE
        }

        setupTitle(binding)
        loadWeb(binding)

    }

    private fun setupTitle(binding: WebviewBinding) {
        val title = intent.extras?.getString(TITLE)
        val titleView = binding.webviewTitle
        titleView.text = title
        titleView.setOnClickListener { finish() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWeb(binding: WebviewBinding) {
        val url = intent.extras!!.getString(URL)
        val webView = binding.webView1
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url!!)
    }

    companion object {
        const val TITLE = "title"
        const val URL = "url"
    }
}