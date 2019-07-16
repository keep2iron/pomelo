package io.github.keep2iron.pomelo.interceptor

import android.os.Handler
import android.os.Looper
import io.github.keep2iron.pomelo.NetworkErrorException
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.io.IOException

class NetworkErrorHandleInterceptor(private val listener: (exception: NetworkErrorException) -> Unit) : Interceptor {

    val handler = Handler(Looper.getMainLooper())

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url().toString()
        try {
            val response = chain.proceed(request)

            if (response.code() < 200 || response.code() >= 300) {
                val body = response.body()
                body?.let {
                    val buffer = Buffer()
                    body.source().readAll(buffer)
                    val bufferedBody = ResponseBody.create(body.contentType(), body.contentLength(), buffer)
                    val message = bufferedBody.string()
                    listener(NetworkErrorException(url, Exception(message)))
                }
            }

            return response
        } catch (ioException: IOException) {
            handler.post {
                listener(NetworkErrorException(url, ioException))
            }
            throw ioException
        }
    }
}