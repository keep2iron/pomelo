package io.github.keep2iron.pomelo.interceptor

import android.util.Log
import io.github.keep2iron.pomelo.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @version 1.0
 * @since 2017/10/31 15:20
 *
 * 用于打印HTTP LOG信息
 */
open class HttpLogger(val tagName: String) : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        if (!BuildConfig.DEBUG) {
            return
        }

        Log.d(tagName, message)
    }
}
