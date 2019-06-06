package io.github.keep2iron.app

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.github.keep2iron.pomelo.NetworkManager
import okhttp3.Protocol
import retrofit2.HttpException
import java.util.*
import java.util.concurrent.TimeUnit

class MyApplication : Application() {

    companion object {

        lateinit var networkManager: NetworkManager

    }

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
        MyApplication.networkManager = NetworkManager.build("http://10.0.2.2:8080/") {
            initOkHttp { builder ->
                builder.protocols(Collections.singletonList(Protocol.HTTP_1_1))         //解决 https://www.cnblogs.com/myhalo/p/6811472.html
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .addNetworkInterceptor(StethoInterceptor())
            }
            networkTagName = "keep2iron"
            doOnGlobalError = { throwable ->
                //                val exp = throwable as HttpException
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@MyApplication, throwable.message, Toast.LENGTH_LONG).show()
                }
            }
            setHeaderParameterInterceptor { url, headerParams ->
                headerParams["test-get-params"] = ""
            }
            setGetParameterInterceptor { url, getParams ->
                getParams["test-get-params"] = ""
            }
            setResponseProcessListener {
                Log.d("keep2iron", "setResponseProcessListener : $it")
                it
            }
        }
    }

}