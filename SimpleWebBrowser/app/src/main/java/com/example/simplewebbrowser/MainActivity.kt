package com.example.simplewebbrowser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {


    private val addressBar :EditText by lazy {
        findViewById(R.id.addressBar)
    }

    private val webView :WebView by lazy {
        findViewById(R.id.webView)
    }

    private val homeBtn : ImageButton by lazy {
        findViewById(R.id.homeBtn)
    }

    private val forwardBtn : ImageButton by lazy {
        findViewById(R.id.forwardBtn)
    }
    private val backBtn : ImageButton by lazy {
        findViewById(R.id.backBtn)
    }

    private val refreshLayout : SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout);
    }

    private val progressBar : ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        bindView()
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun initView(){
        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    private fun bindView(){
        addressBar.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                val loadUrl = v.text.toString()
                if(URLUtil.isNetworkUrl(loadUrl)){
                    webView.loadUrl(loadUrl)
                }else{
                    webView.loadUrl("http://${loadUrl}")
                }

            }

            return@setOnEditorActionListener false
        }
        backBtn.setOnClickListener {
            webView.goBack()
        }
        forwardBtn.setOnClickListener {
            webView.goForward()
        }

        homeBtn.setOnClickListener{
            webView.loadUrl(DEFAULT_URL)
        }
        refreshLayout.setOnRefreshListener {
            webView.reload()

        }
    }

    override fun onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack()
        }else{
            super.onBackPressed()
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient(){
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.show()
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            refreshLayout.isRefreshing = false
            progressBar.hide()
            backBtn.isEnabled = webView.canGoBack()
            forwardBtn.isEnabled = webView.canGoForward()
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient : android.webkit.WebChromeClient(){
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progressBar.progress = newProgress
        }
    }

    companion object{
        private const val DEFAULT_URL = "http://www.google.com"
    }

}