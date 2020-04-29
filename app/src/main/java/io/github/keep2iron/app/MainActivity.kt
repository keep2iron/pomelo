package io.github.keep2iron.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.keep2iron.app.complex.PageStateActivity
import io.github.keep2iron.app.webview.WebViewActivity
import io.github.keep2iron.pineapple.ImageLoaderManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ImageLoaderManager.init(application)

        findViewById<View>(R.id.tvComplexPageState).setOnClickListener {
            startActivity(Intent(this, PageStateActivity::class.java))
        }

        findViewById<View>(R.id.tvListPageState).setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
        }

      findViewById<View>(R.id.tvWebViewList).setOnClickListener {
        startActivity(Intent(this, WebViewActivity::class.java))
      }
    }

}
