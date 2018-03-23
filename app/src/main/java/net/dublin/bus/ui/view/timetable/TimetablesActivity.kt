package net.dublin.bus.ui.view.timetable

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_timetables.*
import kotlinx.android.synthetic.main.content_timetables.*
import net.dublin.bus.R
import net.dublin.bus.common.Constants
import net.dublin.bus.ui.utilities.Utility
import net.dublin.bus.ui.utilities.snackBarErrorMessage
import net.dublin.bus.ui.utilities.snackBarNoConnection

class TimetablesActivity : AppCompatActivity() {
    private var number: String? = null
    private var code: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetables)
        initExtra()
        setupToolbar()
        setupWebView()
        loadUrl()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.timetables_title, number)
    }

    private fun initExtra() {
        number = intent.getStringExtra(EXTRA_ROUTE_NUMBER)
        code = intent.getStringExtra(EXTRA_ROUTE_CODE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        web_view.clearCache(true)
        web_view.settings.javaScriptEnabled = true
        web_view.settings.setSupportZoom(true)
        web_view.settings.builtInZoomControls = true
        web_view.settings.displayZoomControls = false

        web_view.settings.loadWithOverviewMode = true
        web_view.settings.useWideViewPort = true

        web_view.settings.setAppCacheEnabled(false)
        web_view.settings.defaultFontSize = 12
        web_view.webViewClient = WebClient()
    }

    private fun loadUrl() {
        web_view.loadUrl(Constants.API_URL_TIMETABLE.format(code))
    }

    private inner class WebClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            showProgress()
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            onError()
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            injectJavascript(JS_CLEAN_HEADER, view!!)
            injectJavascript(JS_CLEAN_BOTTOM_HEADER, view)
            injectJavascript(JS_CLEAN_MAP_1, view)
            injectJavascript(JS_CLEAN_MAP_2, view)
            hideProgress()
        }

        private fun injectJavascript(script: String, @NonNull webView: WebView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript(script, null)
            } else {
                webView.loadUrl("javascript:$script;")
            }
        }
    }

    fun isNetworkAvailable(): Boolean {
        return Utility.isNetworkAvailable(this)
    }

    private fun onError() {
        if (isNetworkAvailable()) {
            showSnackBarError()
        } else {
            showSnackBarNoConnection()
        }
    }

    private fun showSnackBarNoConnection() {
        snackBarNoConnection(container, { loadUrl() }).show()
    }

    private fun showSnackBarError() {
        snackBarErrorMessage(container, { loadUrl() }).show()
    }

    fun showProgress() {
        time_progress_bar_view?.visibility = View.VISIBLE
    }

    fun hideProgress() {
        time_progress_bar_view?.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val JS_CLEAN_HEADER = "document.getElementsByClassName(\"btn-back\")[0].style.display = \"none\";"
        private const val JS_CLEAN_BOTTOM_HEADER = "document.getElementsByClassName(\"btn-back\")[1].style.display = \"none\";"
        private const val JS_CLEAN_MAP_1 = "document.getElementsByClassName(\"view_on_map\")[0].style.display = \"none\";"
        private const val JS_CLEAN_MAP_2 = "document.getElementsByClassName(\"view_on_map\")[1].style.display = \"none\";"
        private const val EXTRA_ROUTE_NUMBER = "route_number"
        private const val EXTRA_ROUTE_CODE = "route_code_number"

        fun navigate(context: Context, routeNumber: String, routeCode: String?) {
            val intent = Intent(context, TimetablesActivity::class.java)
            intent.putExtra(EXTRA_ROUTE_NUMBER, routeNumber)
            intent.putExtra(EXTRA_ROUTE_CODE, routeCode)
            if (!TextUtils.isEmpty(routeCode)) {
                context.startActivity(intent)
            }
        }
    }
}
