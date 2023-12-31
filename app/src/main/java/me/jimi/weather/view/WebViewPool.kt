package me.jimi.weather.view

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * webview 复用池
 */
@Suppress("unused")
class WebViewPool {
  private var currentSize = 0

  /**
   * 获取webview
   */
  fun getWebView(context: Context): WebView {
    synchronized(lock) {
      val webView: WebView
      if (available.size > 0) {
        webView = available[0]
        available.removeAt(0)
        currentSize++
        inUse.add(webView)
      } else {
        webView = WebView(context)
        initWebSeting(context, webView)
        inUse.add(webView)
        currentSize++
      }
      return webView
    }
  }

  /**
   * 回收webview ,不解绑
   *
   * @param webView 需要被回收的webview
   */
  fun removeWebView(webView: WebView) {
    webView.loadUrl("")
    webView.stopLoading()
    webView.webChromeClient = null
    webView.clearCache(true)
    webView.clearHistory()
    synchronized(lock) {
      inUse.remove(webView)
      if (available.size < maxSize) {
        available.add(webView)
      }
      currentSize--
    }
  }

  /**
   * 回收webview ,解绑
   *
   * @param webView 需要被回收的webview
   */
  fun removeWebView(viewGroup: ViewGroup, webView: WebView) {
    viewGroup.removeView(webView)
    webView.loadUrl("")
    webView.stopLoading()
    webView.webChromeClient = null
    webView.clearCache(true)
    webView.clearHistory()
    synchronized(lock) {
      inUse.remove(webView)
      if (available.size < maxSize) {
        available.add(webView)
      }
      currentSize--
    }
  }

  /**
   * 设置webview池个数
   *
   * @param size webview池个数
   */
  fun setMaxPoolSize(size: Int) {
    synchronized(lock) { maxSize = size }
  }

  companion object {
    private val available: MutableList<WebView> = ArrayList()
    private val inUse: MutableList<WebView> = ArrayList()
    private val lock = byteArrayOf()
    private var maxSize = 1
    private const val startTimes: Long = 0

    @Volatile
    private var _instance: WebViewPool? = null
      get() {
        if (field == null) {
          synchronized(WebViewPool::class.java) {
            if (field == null) {
              field = WebViewPool()
            }
          }
        }
        return field
      }

    val instance: WebViewPool
      get() = _instance!!

    /**
     * Webview 初始化
     * 最好放在application oncreate里
     */
    fun init(context: Context) {
      for (i in 0 until maxSize) {
        val webView = WebView(context)
        initWebSeting(context, webView)
        // webView.loadUrl(DEMO_URL);
        available.add(webView)
      }
    }

    private fun initWebSeting(context: Context, webView: WebView) {
      val params: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
      webView.layoutParams = params
      val webSettings = webView.settings
      // 设置自适应屏幕，两者合用
      webSettings.useWideViewPort = true // 将图片调整到适合webview的大小
      webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
      // 缩放操作
      webSettings.setSupportZoom(false) // 支持缩放，默认为true。是下面那个的前提。
      webSettings.builtInZoomControls = false // 设置内置的缩放控件。若为false，则该WebView不可缩放
      webSettings.displayZoomControls = true // 隐藏原生的缩放控件

      // 其他细节操作
      webSettings.cacheMode = WebSettings.LOAD_NO_CACHE// 关闭webview中缓存
      webSettings.allowFileAccess = true // 设置可以访问文件
      webSettings.javaScriptCanOpenWindowsAutomatically = true // 支持通过JS打开新窗口
      webSettings.loadsImagesAutomatically = true // 支持自动加载图片
     // webSettings.defaultTextEncodingName = "utf-8" // 设置编码格式
      webSettings.domStorageEnabled = true // 开启 DOM storage API 功能
      webSettings.databaseEnabled = true // 开启 database storage API 功能
      webSettings.javaScriptEnabled = true
      // 页面白屏问题
      webView.overScrollMode = View.OVER_SCROLL_NEVER
//            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
      webView.setBackgroundColor(Color.WHITE)
      webView.webViewClient = object :WebViewClient(){

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
          return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
          super.onPageFinished(view, url)
        }
      }
    }
  }
}