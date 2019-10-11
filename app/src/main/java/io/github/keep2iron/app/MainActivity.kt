package io.github.keep2iron.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomelo.utilities.FindService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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

    }

}
