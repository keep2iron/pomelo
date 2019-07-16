package io.github.keep2iron.app

import android.app.Application
import android.util.Log
import android.widget.Toast
import io.github.keep2iron.pomelo.NetworkManager
import io.github.keep2iron.pomelo.convert.CustomGsonConvertFactory
import io.github.keep2iron.pomelo.interceptor.NetworkErrorHandleInterceptor
import io.github.keep2iron.pomelo.interceptor.GetParamsInterceptor
import io.github.keep2iron.pomelo.interceptor.HeaderParamsInterceptor
import io.github.keep2iron.pomelo.interceptor.PostParamsInterceptor
import io.github.keep2iron.pomelo.log.NetworkLoggingInterceptor
import okhttp3.Protocol
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import com.orhanobut.logger.Logger


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodOffset(2)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("pomelo")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        NetworkManager.init("http://10.0.2.2:8080/") {
            initOkHttp {
                protocols(Collections.singletonList(Protocol.HTTP_1_1))         //解决 https://www.cnblogs.com/myhalo/p/6811472.html
                connectTimeout(15, TimeUnit.SECONDS)
                readTimeout(15, TimeUnit.SECONDS)
                addInterceptor(NetworkErrorHandleInterceptor { exception ->
//                    Toast.makeText(this@MyApplication, "${exception}", Toast.LENGTH_SHORT).show()
                })
                addInterceptor(HeaderParamsInterceptor { _, headerParams ->
                    headerParams["test-header-params"] = ""
                })
                addInterceptor(GetParamsInterceptor { url, getParams ->
                    getParams["test-get-params"] = ""
                })
                addInterceptor(PostParamsInterceptor { url, postParams ->
                    postParams["test-post-params"] = ""
                })
                addNetworkInterceptor(NetworkLoggingInterceptor(object : NetworkLoggingInterceptor.Logger {
                    override fun d(message: String) {
                        Logger.d(message)
                    }
                }))
            }

            initRetrofit {
                addConverterFactory(CustomGsonConvertFactory.create())
                addConverterFactory(ScalarsConverterFactory.create())
                addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            }
        }
    }
}