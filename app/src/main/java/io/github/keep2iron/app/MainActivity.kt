package io.github.keep2iron.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomelo.utilities.FindService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val apiService by FindService(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tvText).setOnClickListener {
            it.alpha = 0f
            it.alpha = 1f
//            it.post {
//                 = View.GONE
//            }
//            it.postDelayed( {
//                it.visibility = View.VISIBLE
//            },50L)
        }

        apiService.indexHome(0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(AndroidSubscriber {
                onSuccess = { resp ->
                    Log.d("keep2iron", "onSuccessful .......... ${resp.value}")
                }
            })
    }
}
