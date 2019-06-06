package io.github.keep2iron.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomelo.NetworkManager
import io.github.keep2iron.pomelo.interceptor.HttpLogger
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.concurrent.TimeUnit

//import io.github.keep2iron.network.NetworkManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = MyApplication.networkManager.getService(ApiService::class.java)
        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .subscribe {
                service.indexHome()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(AndroidSubscriber<BaseResponse<String>>(
                        onSuccess = { resp ->
                            Log.d("keep2iron","onSuccessful .......... ${resp}")
                        },
                        onError = { throwable: Throwable ->
                            Log.d("keep2iron","onError .......... ${throwable}")
                        }
                    ))
            }
    }
}
