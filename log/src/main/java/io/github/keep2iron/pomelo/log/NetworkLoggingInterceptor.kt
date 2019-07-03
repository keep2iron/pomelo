package io.github.keep2iron.pomelo.log

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class NetworkLoggingInterceptor(logger: Logger) : Interceptor {

    interface Logger {
        fun d(message: String)
    }

    private val interceptor = HttpLoggingInterceptor(NetworkLogger(logger)).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return interceptor.intercept(chain)
    }
}
