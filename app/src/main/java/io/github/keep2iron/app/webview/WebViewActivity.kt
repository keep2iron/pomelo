package io.github.keep2iron.app.webview

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.app.R
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomelo.pager.adapter.RecyclerViewHolder
import io.github.keep2iron.pomelo.pager.load.ListBinder

class WebViewActivity : AppCompatActivity() {

  var index = 0
  var add = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list)

    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    val linearLayoutManager = LinearLayoutManager(this)

    findViewById<View>(R.id.btnScroll).setOnClickListener {
      if (add && index == 3) add = false
      if (!add && index == 0) add = true

      if (add) index++ else index--
      Toast.makeText(this, "index is $index ", Toast.LENGTH_SHORT).show()

      linearLayoutManager.scrollToPositionWithOffset(index, 0)
    }

    ListBinder(recyclerView, loadMoreAble = null, refreshable = null)
      .setLayoutManager(linearLayoutManager)
      .addSubAdapter(HeaderAdapter(Color.BLACK))
      .addSubAdapter(HeaderAdapter(Color.BLUE))
      .addSubAdapter(WebViewAdapter())
      .addSubAdapter(HeaderAdapter(Color.GREEN))
      .addSubAdapter(HeaderAdapter(Color.RED))
      .bind()
  }

}

private class WebViewAdapter : AbstractSubAdapter(viewType = 0) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
    val holder = super.onCreateViewHolder(parent, viewType)
    val webView = holder.findViewById<WebView>(R.id.webView)
    //        webView.addJavascriptInterface(WebViewActivity.BeeJsObj(this), "beeJsObj")

    //声明WebSettings子类
    val webSettings = webView.settings

    //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
    webSettings.javaScriptEnabled = false

    //设置自适应屏幕，两者合用
    webSettings.useWideViewPort = true //将图片调整到适合webview的大小
    webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小

    //缩放操作
    webSettings.setSupportZoom(false) //支持缩放，默认为true。是下面那个的前提。
    webSettings.builtInZoomControls = false //设置内置的缩放控件。若为false，则该WebView不可缩放
    webSettings.displayZoomControls = false //隐藏原生的缩放控件
    webSettings.domStorageEnabled = true

    //其他细节操作
    //        webSettings.cacheMode = WebSettings.LOAD_DEFAULT //关闭webview中缓存
    webSettings.loadsImagesAutomatically = true //支持自动加载图片
    webSettings.defaultTextEncodingName = "utf-8" //设置编码格式

    //Android 5.0及以上版本使用WebView不能存储第三方Cookies解决方案
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
    //            webSettings.mixedContentMode = 0
    //        }
    webView.webViewClient = WebViewClient()
    webView.webChromeClient = WebChromeClient()
    return holder
  }

  override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int = R.layout.item_web_view

  override fun render(holder: RecyclerViewHolder, position: Int) {
    val webView = holder.findViewById<WebView>(R.id.webView)
    webView.loadUrl("https://flutter.dev/")
  }
}

private class HeaderAdapter(val color: Int) : AbstractSubAdapter(viewType = 1) {

  override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int = R.layout.item_header

  override fun render(holder: RecyclerViewHolder, position: Int) {

    val textView = holder.findViewById<View>(R.id.textView)
    textView.setBackgroundColor(color)
  }

}

private class OriginRecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
    return RecyclerViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
    )
  }

  val colors = arrayListOf(Color.BLACK, Color.RED, Color.GREEN, Color.YELLOW)

  override fun getItemCount(): Int = 4

  override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
    val textView = holder.findViewById<View>(R.id.textView)
    textView.setBackgroundColor(colors[position])
  }
}