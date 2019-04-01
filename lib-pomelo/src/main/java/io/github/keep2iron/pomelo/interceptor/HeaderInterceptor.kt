/*
 * Create bt Keep2iron on 17-5-25 下午2:33
 * Copyright (c) 2017. All rights reserved.
 */

package io.github.keep2iron.pomelo.interceptor

import android.support.v4.util.ArrayMap
import java.io.IOException
import java.util.HashMap

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @version 1.0
 * @since 2018/03/16 15:54
 *
 * 统一的header添加管理器
 */
class HeaderInterceptor(val listener: (url: String, headerParams: ArrayMap<String, String>) -> Unit) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest: Request
        val headerParams = ArrayMap<String, String>()
        val url = request.url().uri().toString()
        listener(url, headerParams)
        try {
            val builder = request.newBuilder()
            for ((key, value) in headerParams) {
                builder.addHeader(key, value)
            }
            newRequest = builder.build()
        } catch (e: Exception) {
            e.printStackTrace()
            return chain.proceed(request)
        }

        return chain.proceed(newRequest)
    }
}