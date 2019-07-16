package io.github.keep2iron.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomelo.utilities.FindService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit

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

//        val disposable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
//            .subscribe {
//                apiService.indexHome("test")
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(AndroidSubscriber<BaseResponse<String>> {
//                        onSuccess = { resp ->
//                            Log.d("keep2iron", "onSuccessful .......... ${resp.value}")
//                        }
//                        onError = {
//
//                        }
//                    })
//            }
    }
}
